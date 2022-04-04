package io.papermc.paper.console;

import com.destroystokyo.paper.PaperConfig;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecrell.terminalconsole.MinecraftFormattingConverter.KEEP_FORMATTING_PROPERTY;

/**
 * Modified version of <a href="https://github.com/Minecrell/TerminalConsoleAppender/blob/master/src/main/java/net/minecrell/terminalconsole/MinecraftFormattingConverter.java">
 * TerminalConsoleAppender's MinecraftFormattingConverter</a> to support hex color codes using the Adventure [char]#rrggbb format.
 */
@Plugin(name = "paperMinecraftFormatting", category = PatternConverter.CATEGORY)
@ConverterKeys({"paperMinecraftFormatting"})
@PerformanceSensitive("allocation")
public final class HexFormattingConverter extends LogEventPatternConverter {

    private static final boolean KEEP_FORMATTING = PropertiesUtil.getProperties().getBooleanProperty(KEEP_FORMATTING_PROPERTY);

    private static final String ANSI_RESET = "\u001B[m";

    private static final char COLOR_CHAR = 0x7f;
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
        .hexColors()
        .flattener(PaperAdventure.FLATTENER)
        .character(HexFormattingConverter.COLOR_CHAR)
        .build();
    private static final String LOOKUP = "0123456789abcdefklmnor";

    private static final String RGB_ANSI = "\u001B[38;2;%d;%d;%dm";
    private static final Pattern NAMED_PATTERN = Pattern.compile(COLOR_CHAR + "[0-9a-fk-orA-FK-OR]");
    private static final Pattern RGB_PATTERN = Pattern.compile(COLOR_CHAR + "#([0-9a-fA-F]){6}");

    private static final String[] RGB_ANSI_CODES = new String[]{
        formatHexAnsi(NamedTextColor.BLACK),         // Black §0
        formatHexAnsi(NamedTextColor.DARK_BLUE),     // Dark Blue §1
        formatHexAnsi(NamedTextColor.DARK_GREEN),    // Dark Green §2
        formatHexAnsi(NamedTextColor.DARK_AQUA),     // Dark Aqua §3
        formatHexAnsi(NamedTextColor.DARK_RED),      // Dark Red §4
        formatHexAnsi(NamedTextColor.DARK_PURPLE),   // Dark Purple §5
        formatHexAnsi(NamedTextColor.GOLD),          // Gold §6
        formatHexAnsi(NamedTextColor.GRAY),          // Gray §7
        formatHexAnsi(NamedTextColor.DARK_GRAY),     // Dark Gray §8
        formatHexAnsi(NamedTextColor.BLUE),          // Blue §9
        formatHexAnsi(NamedTextColor.GREEN),         // Green §a
        formatHexAnsi(NamedTextColor.AQUA),          // Aqua §b
        formatHexAnsi(NamedTextColor.RED),           // Red §c
        formatHexAnsi(NamedTextColor.LIGHT_PURPLE),  // Light Purple §d
        formatHexAnsi(NamedTextColor.YELLOW),        // Yellow §e
        formatHexAnsi(NamedTextColor.WHITE),         // White §f
        "\u001B[5m",                                 // Obfuscated §k
        "\u001B[21m",                                // Bold §l
        "\u001B[9m",                                 // Strikethrough §m
        "\u001B[4m",                                 // Underline §n
        "\u001B[3m",                                 // Italic §o
        ANSI_RESET,                                  // Reset §r
    };
    private static final String[] ANSI_ANSI_CODES = new String[]{
        "\u001B[0;30m",    // Black §0
        "\u001B[0;34m",    // Dark Blue §1
        "\u001B[0;32m",    // Dark Green §2
        "\u001B[0;36m",    // Dark Aqua §3
        "\u001B[0;31m",    // Dark Red §4
        "\u001B[0;35m",    // Dark Purple §5
        "\u001B[0;33m",    // Gold §6
        "\u001B[0;37m",    // Gray §7
        "\u001B[0;30;1m",  // Dark Gray §8
        "\u001B[0;34;1m",  // Blue §9
        "\u001B[0;32;1m",  // Green §a
        "\u001B[0;36;1m",  // Aqua §b
        "\u001B[0;31;1m",  // Red §c
        "\u001B[0;35;1m",  // Light Purple §d
        "\u001B[0;33;1m",  // Yellow §e
        "\u001B[0;37;1m",  // White §f
        "\u001B[5m",       // Obfuscated §k
        "\u001B[21m",      // Bold §l
        "\u001B[9m",       // Strikethrough §m
        "\u001B[4m",       // Underline §n
        "\u001B[3m",       // Italic §o
        ANSI_RESET,        // Reset §r
    };

    private final boolean ansi;
    private final List<PatternFormatter> formatters;

    /**
     * Construct the converter.
     *
     * @param formatters The pattern formatters to generate the text to manipulate
     * @param strip      If true, the converter will strip all formatting codes
     */
    protected HexFormattingConverter(List<PatternFormatter> formatters, boolean strip) {
        super("paperMinecraftFormatting", null);
        this.formatters = formatters;
        this.ansi = !strip;
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        int start = toAppendTo.length();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = formatters.size(); i < size; i++) {
            formatters.get(i).format(event, toAppendTo);
        }

        if (KEEP_FORMATTING || toAppendTo.length() == start) {
            // Skip replacement if disabled or if the content is empty
            return;
        }

        boolean useAnsi = ansi && TerminalConsoleAppender.isAnsiSupported();
        String content = toAppendTo.substring(start);
        content = useAnsi ? convertRGBColors(content) : stripRGBColors(content);
        format(content, toAppendTo, start, useAnsi);
    }

    private static String convertRGBColors(final String input) {
        return RGB_PATTERN.matcher(input).replaceAll(result -> {
            final int hex = Integer.decode(result.group().substring(1));
            return formatHexAnsi(hex);
        });
    }

    private static String formatHexAnsi(final TextColor color) {
        return formatHexAnsi(color.value());
    }

    private static String formatHexAnsi(final int color) {
        final int red = color >> 16 & 0xFF;
        final int green = color >> 8 & 0xFF;
        final int blue = color & 0xFF;
        return String.format(RGB_ANSI, red, green, blue);
    }

    private static String stripRGBColors(final String input) {
        return RGB_PATTERN.matcher(input).replaceAll("");
    }

    static void format(String content, StringBuilder result, int start, boolean ansi) {
        int next = content.indexOf(COLOR_CHAR);
        int last = content.length() - 1;
        if (next == -1 || next == last) {
            result.setLength(start);
            result.append(content);
            if (ansi) {
                result.append(ANSI_RESET);
            }
            return;
        }

        Matcher matcher = NAMED_PATTERN.matcher(content);
        StringBuilder buffer = new StringBuilder();
        final String[] ansiCodes = PaperConfig.useRgbForNamedTextColors ? RGB_ANSI_CODES : ANSI_ANSI_CODES;
        while (matcher.find()) {
            int format = LOOKUP.indexOf(Character.toLowerCase(matcher.group().charAt(1)));
            if (format != -1) {
                matcher.appendReplacement(buffer, ansi ? ansiCodes[format] : "");
            }
        }
        matcher.appendTail(buffer);

        result.setLength(start);
        result.append(buffer);
        if (ansi) {
            result.append(ANSI_RESET);
        }
    }

    /**
     * Gets a new instance of the {@link HexFormattingConverter} with the
     * specified options.
     *
     * @param config  The current configuration
     * @param options The pattern options
     * @return The new instance
     * @see HexFormattingConverter
     */
    public static HexFormattingConverter newInstance(Configuration config, String[] options) {
        if (options.length < 1 || options.length > 2) {
            LOGGER.error("Incorrect number of options on paperMinecraftFormatting. Expected at least 1, max 2 received " + options.length);
            return null;
        }
        if (options[0] == null) {
            LOGGER.error("No pattern supplied on paperMinecraftFormatting");
            return null;
        }

        PatternParser parser = PatternLayout.createPatternParser(config);
        List<PatternFormatter> formatters = parser.parse(options[0]);
        boolean strip = options.length > 1 && "strip".equals(options[1]);
        return new HexFormattingConverter(formatters, strip);
    }

}
