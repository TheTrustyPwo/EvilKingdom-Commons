package com.destroystokyo.paper;

import com.destroystokyo.paper.io.chunk.ChunkTaskManager;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import co.aikar.timings.Timings;
import co.aikar.timings.TimingsManager;
import org.spigotmc.SpigotConfig;
import org.spigotmc.WatchdogThread;

public class PaperConfig {

    private static File CONFIG_FILE;
    private static final String HEADER = "This is the main configuration file for Paper.\n"
            + "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
            + "with caution, and make sure you know what each option does before configuring.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to Paper,\n"
            + "join us in our Discord or IRC channel.\n"
            + "\n"
            + "Discord: https://discord.gg/papermc\n"
            + "IRC: #paper @ irc.esper.net ( https://webchat.esper.net/?channels=paper ) \n"
            + "Website: https://papermc.io/ \n"
            + "Docs: https://docs.papermc.io/ \n";
    /*========================================================================*/
    public static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;
    private static boolean verbose;
    private static boolean fatalError;
    /*========================================================================*/
    private static boolean metricsStarted;

    public static void init(File configFile) {
        CONFIG_FILE = configFile;
        config = new YamlConfiguration();
        try {
            config.load(CONFIG_FILE);
        } catch (IOException ex) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load paper.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);
        verbose = getBoolean("verbose", false);

        commands = new HashMap<String, Command>();
        commands.put("paper", new PaperCommand("paper"));
        commands.put("mspt", new MSPTCommand("mspt"));

        version = getInt("config-version", 27);
        set("config-version", 27);
        readConfig(PaperConfig.class, null);
    }

    protected static void logError(String s) {
        Bukkit.getLogger().severe(s);
    }

    protected static void fatal(String s) {
        fatalError = true;
        throw new RuntimeException("Fatal paper.yml config error: " + s);
    }

    protected static void log(String s) {
        if (verbose) {
            Bukkit.getLogger().info(s);
        }
    }

    public static boolean logPlayerIpAddresses = true;
    private static void playerIpAddresses() {
        logPlayerIpAddresses = getBoolean("settings.log-player-ip-addresses", logPlayerIpAddresses);
    }

    public static int maxJoinsPerTick;
    private static void maxJoinsPerTick() {
        maxJoinsPerTick = getInt("settings.max-joins-per-tick", 3);
    }

    public static boolean trackPluginScoreboards;
    private static void trackPluginScoreboards() {
        trackPluginScoreboards = getBoolean("settings.track-plugin-scoreboards", false);
    }

    public static boolean fixEntityPositionDesync = true;
    private static void fixEntityPositionDesync() {
        fixEntityPositionDesync = getBoolean("settings.fix-entity-position-desync", fixEntityPositionDesync);
    }

    public static boolean enableBrigadierConsoleHighlighting = true;
    public static boolean enableBrigadierConsoleCompletions = true;
    private static void consoleSettings() {
        enableBrigadierConsoleHighlighting = getBoolean("settings.console.enable-brigadier-highlighting", enableBrigadierConsoleHighlighting);
        enableBrigadierConsoleCompletions = getBoolean("settings.console.enable-brigadier-completions", enableBrigadierConsoleCompletions);
    }

    public static void registerCommands() {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Paper", entry.getValue());
        }

        if (!metricsStarted) {
            Metrics.PaperMetrics.startMetrics();
            metricsStarted = true;
        }
    }

    static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        throw Throwables.propagate(ex.getCause());
                    } catch (Exception ex) {
                        Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
                    }
                }
            }
        }
        saveConfig();
    }

    static void saveConfig() {
        try {
            config.save(CONFIG_FILE);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    private static final Pattern SPACE = Pattern.compile(" ");
    private static final Pattern NOT_NUMERIC = Pattern.compile("[^-\\d.]");
    public static int getSeconds(String str) {
        str = SPACE.matcher(str).replaceAll("");
        final char unit = str.charAt(str.length() - 1);
        str = NOT_NUMERIC.matcher(str).replaceAll("");
        double num;
        try {
            num = Double.parseDouble(str);
        } catch (Exception e) {
            num = 0D;
        }
        switch (unit) {
            case 'd': num *= (double) 60*60*24; break;
            case 'h': num *= (double) 60*60; break;
            case 'm': num *= (double) 60; break;
            default: case 's': break;
        }
        return (int) num;
    }

    protected static String timeSummary(int seconds) {
        String time = "";

        if (seconds > 60 * 60 * 24) {
            time += TimeUnit.SECONDS.toDays(seconds) + "d";
            seconds %= 60 * 60 * 24;
        }

        if (seconds > 60 * 60) {
            time += TimeUnit.SECONDS.toHours(seconds) + "h";
            seconds %= 60 * 60;
        }

        if (seconds > 0) {
            time += TimeUnit.SECONDS.toMinutes(seconds) + "m";
        }
        return time;
    }

    private static void set(String path, Object val) {
        config.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    private static float getFloat(String path, float def) {
        // TODO: Figure out why getFloat() always returns the default value.
        return (float) getDouble(path, (double) def);
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(String path, T def) {
        config.addDefault(path, def);
        return (List<T>) config.getList(path, config.getList(path));
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    public static boolean useDisplayNameInQuit = false;
    private static void useDisplayNameInQuit() {
        if (version < 21) {
            boolean oldValue = getBoolean("use-display-name-in-quit-message", useDisplayNameInQuit);
            set("settings.use-display-name-in-quit-message", oldValue);
        }
        useDisplayNameInQuit = getBoolean("settings.use-display-name-in-quit-message", useDisplayNameInQuit);
    }

    public static String timingsServerName;
    private static void timings() {
        boolean timings = getBoolean("timings.enabled", true);
        boolean verboseTimings = getBoolean("timings.verbose", true);
        TimingsManager.url = getString("timings.url", "https://timings.aikar.co/");
        if (!TimingsManager.url.endsWith("/")) {
            TimingsManager.url += "/";
        }
        TimingsManager.privacy = getBoolean("timings.server-name-privacy", false);
        TimingsManager.hiddenConfigs = getList("timings.hidden-config-entries", Lists.newArrayList("database", "settings.bungeecord-addresses", "settings.velocity-support.secret"));
        if (!TimingsManager.hiddenConfigs.contains("settings.velocity-support.secret")) {
            TimingsManager.hiddenConfigs.add("settings.velocity-support.secret");
        }
        int timingHistoryInterval = getInt("timings.history-interval", 300);
        int timingHistoryLength = getInt("timings.history-length", 3600);
        timingsServerName = getString("timings.server-name", "Unknown Server");


        Timings.setVerboseTimingsEnabled(verboseTimings);
        Timings.setTimingsEnabled(timings);
        Timings.setHistoryInterval(timingHistoryInterval * 20);
        Timings.setHistoryLength(timingHistoryLength * 20);

        log("Timings: " + timings +
                " - Url: " + TimingsManager.url +
                " - Verbose: " + verboseTimings +
                " - Interval: " + timeSummary(Timings.getHistoryInterval() / 20) +
                " - Length: " + timeSummary(Timings.getHistoryLength() / 20) +
                " - Server Name: " + timingsServerName);
    }

    public static boolean loadPermsBeforePlugins = true;
    private static void loadPermsBeforePlugins() {
        loadPermsBeforePlugins = getBoolean("settings.load-permissions-yml-before-plugins", true);
    }

    public static int regionFileCacheSize = 256;
    private static void regionFileCacheSize() {
        regionFileCacheSize = Math.max(getInt("settings.region-file-cache-size", 256), 4);
    }

    public static boolean enablePlayerCollisions = true;
    private static void enablePlayerCollisions() {
        enablePlayerCollisions = getBoolean("settings.enable-player-collisions", true);
    }

    public static boolean saveEmptyScoreboardTeams = false;
    private static void saveEmptyScoreboardTeams() {
        saveEmptyScoreboardTeams = getBoolean("settings.save-empty-scoreboard-teams", false);
    }

    public static boolean bungeeOnlineMode = true;
    private static void bungeeOnlineMode() {
        bungeeOnlineMode = getBoolean("settings.bungee-online-mode", true);
    }

    public static boolean isProxyOnlineMode() {
        return Bukkit.getOnlineMode() || (SpigotConfig.bungee && bungeeOnlineMode) || (velocitySupport && velocityOnlineMode);
    }

    public static int packetInSpamThreshold = 300;
    private static void packetInSpamThreshold() {
        if (version < 11) {
            int oldValue = getInt("settings.play-in-use-item-spam-threshold", 300);
            set("settings.incoming-packet-spam-threshold", oldValue);
        }
        packetInSpamThreshold = getInt("settings.incoming-packet-spam-threshold", 300);
    }

    public static String flyingKickPlayerMessage = "Flying is not enabled on this server";
    public static String flyingKickVehicleMessage = "Flying is not enabled on this server";
    private static void flyingKickMessages() {
        flyingKickPlayerMessage = getString("messages.kick.flying-player", flyingKickPlayerMessage);
        flyingKickVehicleMessage = getString("messages.kick.flying-vehicle", flyingKickVehicleMessage);
    }

    public static boolean suggestPlayersWhenNullTabCompletions = true;
    private static void suggestPlayersWhenNull() {
        suggestPlayersWhenNullTabCompletions = getBoolean("settings.suggest-player-names-when-null-tab-completions", suggestPlayersWhenNullTabCompletions);
    }

    public static String authenticationServersDownKickMessage = ""; // empty = use translatable message
    private static void authenticationServersDownKickMessage() {
        authenticationServersDownKickMessage = Strings.emptyToNull(getString("messages.kick.authentication-servers-down", authenticationServersDownKickMessage));
    }

    public static String connectionThrottleKickMessage = "Connection throttled! Please wait before reconnecting.";
    private static void connectionThrottleKickMessage() {
        connectionThrottleKickMessage = getString("messages.kick.connection-throttle", connectionThrottleKickMessage);
    }

    public static String noPermissionMessage = "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.";
    private static void noPermissionMessage() {
        noPermissionMessage = ChatColor.translateAlternateColorCodes('&', getString("messages.no-permission", noPermissionMessage));
    }

    private static void savePlayerData() {
        Object val = config.get("settings.save-player-data");
        if (val instanceof Boolean) {
            SpigotConfig.disablePlayerDataSaving = !(Boolean) val;
            SpigotConfig.config.set("players.disable-saving", SpigotConfig.disablePlayerDataSaving);
            SpigotConfig.save();
            config.set("settings.save-player-data", null);
        }
    }

    private static void namedEntityDeaths() {
        Object val = config.get("settings.log-named-entity-deaths");
        if (val instanceof Boolean bool && !bool) {
            SpigotConfig.logNamedDeaths = false;
            SpigotConfig.config.set("settings.log-named-deaths", false);
            SpigotConfig.save();
        }
    }

    public static boolean useAlternativeLuckFormula = false;
    private static void useAlternativeLuckFormula() {
        useAlternativeLuckFormula = getBoolean("settings.use-alternative-luck-formula", false);
        if (useAlternativeLuckFormula) {
            Bukkit.getLogger().log(Level.INFO, "Using Aikar's Alternative Luck Formula to apply Luck attribute to all loot pool calculations. See https://luckformula.emc.gs");
        }
    }

    public static int watchdogPrintEarlyWarningEvery = 5000;
    public static int watchdogPrintEarlyWarningDelay = 10000;
    private static void watchdogEarlyWarning() {
        watchdogPrintEarlyWarningEvery = getInt("settings.watchdog.early-warning-every", 5000);
        watchdogPrintEarlyWarningDelay = getInt("settings.watchdog.early-warning-delay", 10000);
        WatchdogThread.doStart(SpigotConfig.timeoutTime, SpigotConfig.restartOnCrash );
    }

    public static int tabSpamIncrement = 1;
    public static int tabSpamLimit = 500;
    private static void tabSpamLimiters() {
        tabSpamIncrement = getInt("settings.spam-limiter.tab-spam-increment", tabSpamIncrement);
        // Older versions used a smaller limit, which is too low for 1.13, we'll bump this up if default
        if (version < 14) {
            if (tabSpamIncrement == 10) {
                set("settings.spam-limiter.tab-spam-increment", 2);
                tabSpamIncrement = 2;
            }
        }
        tabSpamLimit = getInt("settings.spam-limiter.tab-spam-limit", tabSpamLimit);
    }

    public static int autoRecipeIncrement = 1;
    public static int autoRecipeLimit = 20;
    private static void autoRecipieLimiters() {
        autoRecipeIncrement = getInt("settings.spam-limiter.recipe-spam-increment", autoRecipeIncrement);
        autoRecipeLimit = getInt("settings.spam-limiter.recipe-spam-limit", autoRecipeLimit);
    }

    public static boolean velocitySupport;
    public static boolean velocityOnlineMode;
    public static byte[] velocitySecretKey;
    private static void velocitySupport() {
        velocitySupport = getBoolean("settings.velocity-support.enabled", false);
        velocityOnlineMode = getBoolean("settings.velocity-support.online-mode", false);
        String secret = getString("settings.velocity-support.secret", "");
        if (velocitySupport && secret.isEmpty()) {
            fatal("Velocity support is enabled, but no secret key was specified. A secret key is required!");
        } else {
            velocitySecretKey = secret.getBytes(StandardCharsets.UTF_8);
        }
    }

    public static int maxBookPageSize = 2560;
    public static double maxBookTotalSizeMultiplier = 0.98D;
    private static void maxBookSize() {
        maxBookPageSize = Math.min(8192, getInt("settings.book-size.page-max", maxBookPageSize));
        maxBookTotalSizeMultiplier = getDouble("settings.book-size.total-multiplier", maxBookTotalSizeMultiplier);
    }

    public static boolean asyncChunks = false;
    private static void asyncChunks() {
        ConfigurationSection section;
        if (version < 15) {
            section = config.createSection("settings.async-chunks");
            section.set("threads", -1);
        } else {
            section = config.getConfigurationSection("settings.async-chunks");
            if (section == null) {
                section = config.createSection("settings.async-chunks");
            }
        }
        // Clean up old configs
        if (section.contains("load-threads")) {
            if (!section.contains("threads")) {
                section.set("threads", section.get("load-threads"));
            }
            section.set("load-threads", null);
        }
        section.set("generation", null);
        section.set("enabled", null);
        section.set("thread-per-world-generation", null);

        int threads = getInt("settings.async-chunks.threads", -1);
        int cpus = Runtime.getRuntime().availableProcessors() / 2;
        if (threads <= 0) {
            if (cpus <= 4) {
                threads = cpus <= 2 ? 1 : 2;
            } else {
                threads = (int) Math.min(Integer.getInteger("paper.maxChunkThreads", 4), cpus / 2);
            }
        }
        if (cpus == 1 && !Boolean.getBoolean("Paper.allowAsyncChunksSingleCore")) {
            asyncChunks = false;
        } else {
            asyncChunks = true;
        }

        // Let Shared Host set some limits
        String sharedHostThreads = System.getenv("PAPER_ASYNC_CHUNKS_SHARED_HOST_THREADS");
        if (sharedHostThreads != null) {
            try {
                threads = Math.max(1, Math.min(threads, Integer.parseInt(sharedHostThreads)));
            } catch (NumberFormatException ignored) {}
        }

        if (!asyncChunks) {
            log("Async Chunks: Disabled - Chunks will be managed synchronously, and will cause tremendous lag.");
        } else {
            ChunkTaskManager.initGlobalLoadThreads(threads);
            log("Async Chunks: Enabled - Chunks will be loaded much faster, without lag.");
        }
    }

    public static boolean deobfuscateStacktraces = true;
    public static boolean useRgbForNamedTextColors = true;
    private static void loggerSettings() {
        deobfuscateStacktraces = getBoolean("settings.loggers.deobfuscate-stacktraces", deobfuscateStacktraces);
        useRgbForNamedTextColors = getBoolean("settings.loggers.use-rgb-for-named-text-colors", useRgbForNamedTextColors);
    }

    public static boolean allowBlockPermanentBreakingExploits = false;
    private static void allowBlockPermanentBreakingExploits() {
        if (config.contains("allow-perm-block-break-exploits")) {
            allowBlockPermanentBreakingExploits = config.getBoolean("allow-perm-block-break-exploits", false);
            config.set("allow-perm-block-break-exploits", null);
        }

        config.set("settings.unsupported-settings.allow-permanent-block-break-exploits-readme", "This setting controls if players should be able to break bedrock, end portals and other intended to be permanent blocks.");
        allowBlockPermanentBreakingExploits = getBoolean("settings.unsupported-settings.allow-permanent-block-break-exploits", allowBlockPermanentBreakingExploits);
    }

    public static boolean consoleHasAllPermissions = false;
    private static void consoleHasAllPermissions() {
        consoleHasAllPermissions = getBoolean("settings.console-has-all-permissions", consoleHasAllPermissions);
    }

    public static boolean allowPistonDuplication;
    private static void allowPistonDuplication() {
        config.set("settings.unsupported-settings.allow-piston-duplication-readme", "This setting controls if player should be able to use TNT duplication, but this also allows duplicating carpet, rails and potentially other items");
        allowPistonDuplication = getBoolean("settings.unsupported-settings.allow-piston-duplication", config.getBoolean("settings.unsupported-settings.allow-tnt-duplication", false));
        set("settings.unsupported-settings.allow-tnt-duplication", null);
    }

    public static boolean performUsernameValidation;
    private static void performUsernameValidation() {
        performUsernameValidation = getBoolean("settings.unsupported-settings.perform-username-validation", true);
    }


    public static int playerAutoSaveRate = -1;
    public static int maxPlayerAutoSavePerTick = 10;
    private static void playerAutoSaveRate() {
        playerAutoSaveRate = getInt("settings.player-auto-save-rate", -1);
        maxPlayerAutoSavePerTick = getInt("settings.max-player-auto-save-per-tick", -1);
        if (maxPlayerAutoSavePerTick == -1) { // -1 Automatic / "Recommended"
            // 10 should be safe for everyone unless you mass spamming player auto save
            maxPlayerAutoSavePerTick = (playerAutoSaveRate == -1 || playerAutoSaveRate > 100) ? 10 : 20;
        }
    }

    public static boolean allowHeadlessPistons;
    private static void allowHeadlessPistons() {
        config.set("settings.unsupported-settings.allow-headless-pistons-readme", "This setting controls if players should be able to create headless pistons.");
        allowHeadlessPistons = getBoolean("settings.unsupported-settings.allow-headless-pistons", false);
    }

    public static int itemValidationDisplayNameLength = 8192;
    public static int itemValidationLocNameLength = 8192;
    public static int itemValidationLoreLineLength = 8192;
    public static int itemValidationBookTitleLength = 8192;
    public static int itemValidationBookAuthorLength = 8192;
    public static int itemValidationBookPageLength = 16384;
    private static void itemValidationSettings() {
        itemValidationDisplayNameLength = getInt("settings.item-validation.display-name", itemValidationDisplayNameLength);
        itemValidationLocNameLength = getInt("settings.item-validation.loc-name", itemValidationLocNameLength);
        itemValidationLoreLineLength = getInt("settings.item-validation.lore-line", itemValidationLoreLineLength);
        itemValidationBookTitleLength = getInt("settings.item-validation.book.title", itemValidationBookTitleLength);
        itemValidationBookAuthorLength = getInt("settings.item-validation.book.author", itemValidationBookAuthorLength);
        itemValidationBookPageLength = getInt("settings.item-validation.book.page", itemValidationBookPageLength);
    }

    public static boolean fixTargetSelectorTagCompletion = true;
    private static void fixTargetSelectorTagCompletion() {
        fixTargetSelectorTagCompletion = getBoolean("settings.fix-target-selector-tag-completion", fixTargetSelectorTagCompletion);
    }

    public static final class PacketLimit {
        public final double packetLimitInterval;
        public final double maxPacketRate;
        public final ViolateAction violateAction;

        public PacketLimit(final double packetLimitInterval, final double maxPacketRate, final ViolateAction violateAction) {
            this.packetLimitInterval = packetLimitInterval;
            this.maxPacketRate = maxPacketRate;
            this.violateAction = violateAction;
        }

        public static enum ViolateAction {
            KICK, DROP;
        }
    }

    public static String kickMessage;
    public static PacketLimit allPacketsLimit;
    public static java.util.Map<Class<? extends net.minecraft.network.protocol.Packet<?>>, PacketLimit> packetSpecificLimits = new java.util.HashMap<>();

    private static void packetLimiter() {
        packetSpecificLimits.clear();
        kickMessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', getString("settings.packet-limiter.kick-message", "&cSent too many packets"));
        allPacketsLimit = new PacketLimit(
            getDouble("settings.packet-limiter.limits.all.interval", 7.0),
            getDouble("settings.packet-limiter.limits.all.max-packet-rate", 500.0),
            PacketLimit.ViolateAction.KICK
        );
        if (allPacketsLimit.maxPacketRate <= 0.0 || allPacketsLimit.packetLimitInterval <= 0.0) {
            allPacketsLimit = null;
        }
        final ConfigurationSection section = config.getConfigurationSection("settings.packet-limiter.limits");

        // add default packets

        // auto recipe limiting
        getDouble("settings.packet-limiter.limits." +
            "PacketPlayInAutoRecipe" + ".interval", 4.0);
        getDouble("settings.packet-limiter.limits." +
            "PacketPlayInAutoRecipe" + ".max-packet-rate", 5.0);
        getString("settings.packet-limiter.limits." +
            "PacketPlayInAutoRecipe" + ".action", PacketLimit.ViolateAction.DROP.name());

        final Map<String, String> mojangToSpigot = new HashMap<>();
        final Map<String, io.papermc.paper.util.ObfHelper.ClassMapping> maps = io.papermc.paper.util.ObfHelper.INSTANCE.mappingsByObfName();
        if (maps != null) {
            maps.forEach((spigotName, classMapping) ->
                mojangToSpigot.put(classMapping.mojangName(), classMapping.obfName()));
        }

        for (final String packetClassName : section.getKeys(false)) {
            if (packetClassName.equals("all")) {
                continue;
            }
            Class<?> packetClazz = null;

            for (final String subpackage : List.of("game", "handshake", "login", "status")) {
                final String fullName = "net.minecraft.network.protocol." + subpackage + "." + packetClassName;
                try {
                    packetClazz = Class.forName(fullName);
                    break;
                } catch (final ClassNotFoundException ex) {
                    try {
                        final String spigot = mojangToSpigot.get(fullName);
                        if (spigot != null) {
                            packetClazz = Class.forName(spigot);
                        }
                    } catch (final ClassNotFoundException ignore) {}
                }
            }

            if (packetClazz == null || !net.minecraft.network.protocol.Packet.class.isAssignableFrom(packetClazz)) {
                MinecraftServer.LOGGER.warn("Packet '" + packetClassName + "' does not exist, cannot limit it! Please update paper.yml");
                continue;
            }

            if (!(section.get(packetClassName.concat(".interval")) instanceof Number) || !(section.get(packetClassName.concat(".max-packet-rate")) instanceof Number)) {
                throw new RuntimeException("Packet limit setting " + packetClassName + " is missing interval or max-packet-rate!");
            }

            final String actionString = section.getString(packetClassName.concat(".action"), "KICK");
            PacketLimit.ViolateAction action = PacketLimit.ViolateAction.KICK;
            for (PacketLimit.ViolateAction test : PacketLimit.ViolateAction.values()) {
                if (actionString.equalsIgnoreCase(test.name())) {
                    action = test;
                    break;
                }
            }

            final double interval = section.getDouble(packetClassName.concat(".interval"));
            final double rate = section.getDouble(packetClassName.concat(".max-packet-rate"));

            if (interval > 0.0 && rate > 0.0) {
                packetSpecificLimits.put((Class)packetClazz, new PacketLimit(interval, rate, action));
            }
        }
    }

    public static boolean lagCompensateBlockBreaking;

    private static void lagCompensateBlockBreaking() {
        lagCompensateBlockBreaking = getBoolean("settings.lag-compensate-block-breaking", true);
    }

    public static boolean sendFullPosForHardCollidingEntities;

    private static void sendFullPosForHardCollidingEntities() {
        sendFullPosForHardCollidingEntities = getBoolean("settings.send-full-pos-for-hard-colliding-entities", true);
    }

    public static boolean timeCommandAffectsAllWorlds = false; // See https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/commits/aeaeb359317e6ba25b7c45cf6d70ff945a3777cf
    private static void timeCommandAffectsAllWorlds() {
        timeCommandAffectsAllWorlds = getBoolean("settings.time-command-affects-all-worlds", timeCommandAffectsAllWorlds);
    }


    public static int playerMinChunkLoadRadius;
    public static boolean playerAutoConfigureSendViewDistance;
    public static int playerMaxConcurrentChunkSends;
    public static double playerTargetChunkSendRate;
    public static double globalMaxChunkSendRate;
    public static boolean playerFrustumPrioritisation;
    public static double globalMaxChunkLoadRate;
    public static double playerMaxConcurrentChunkLoads;
    public static double globalMaxConcurrentChunkLoads;
    public static double playerMaxChunkLoadRate;

    private static void newPlayerChunkManagement() {
        playerMinChunkLoadRadius = getInt("settings.chunk-loading.min-load-radius", 2);
        playerMaxConcurrentChunkSends = getInt("settings.chunk-loading.max-concurrent-sends", 2);
        playerAutoConfigureSendViewDistance = getBoolean("settings.chunk-loading.autoconfig-send-distance", true);
        playerTargetChunkSendRate = getDouble("settings.chunk-loading.target-player-chunk-send-rate", 100.0);
        globalMaxChunkSendRate = getDouble("settings.chunk-loading.global-max-chunk-send-rate", -1.0);
        playerFrustumPrioritisation = getBoolean("settings.chunk-loading.enable-frustum-priority", false);
        globalMaxChunkLoadRate = getDouble("settings.chunk-loading.global-max-chunk-load-rate", -1.0);
        if (version < 23 && globalMaxChunkLoadRate == 300.0) {
            set("settings.chunk-loading.global-max-chunk-load-rate", globalMaxChunkLoadRate = -1.0);
        }
        playerMaxConcurrentChunkLoads = getDouble("settings.chunk-loading.player-max-concurrent-loads", 20.0);
        if (version < 25 && playerMaxConcurrentChunkLoads == 4.0) {
            set("settings.chunk-loading.player-max-concurrent-loads", playerMaxConcurrentChunkLoads = 20.0);
        }
        globalMaxConcurrentChunkLoads = getDouble("settings.chunk-loading.global-max-concurrent-loads", 500.0);
        playerMaxChunkLoadRate = getDouble("settings.chunk-loading.player-max-chunk-load-rate", -1.0);
    }

    public static boolean useDimensionTypeForCustomSpawners;
    private static void useDimensionTypeForCustomSpawners() {
        useDimensionTypeForCustomSpawners = getBoolean("settings.use-dimension-type-for-custom-spawners", false);
    }

    public static boolean useProxyProtocol;
    private static void useProxyProtocol() {
        useProxyProtocol = getBoolean("settings.proxy-protocol", false);
    }
}
