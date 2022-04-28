package net.evilkingdom.commons.border;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.border.implementations.BorderListener;
import net.evilkingdom.commons.border.objects.Border;
import net.evilkingdom.commons.command.implementations.CommandConverter;
import net.evilkingdom.commons.command.objects.Command;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Optional;

public class BorderImplementor {

    private final JavaPlugin plugin;

    private HashSet<Border> borders;

    private static final HashSet<BorderImplementor> cache = new HashSet<BorderImplementor>();

    /**
     * Allows you to create a BorderImplementor.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin to create the BorderImplementor for.
     */
    public BorderImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        new BorderListener(this.plugin);
        this.borders = new HashSet<Border>();

        cache.add(this);
    }

    /**
     * Allows you to retrieve the implementor's borders.
     *
     * @return The implementor's commands.
     */
    public HashSet<Border> getBorders() {
        return this.borders;
    }

    /**
     * Allows you to retrieve the implementor's plugin.
     *
     * @return The implementor's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the CommandImplementor for a plugin.
     * It will either pull it out of the cache or look for one if it doesn't exist.
     *
     * @param plugin ~ The plugin the CommandImplementor is for.
     * @return The self class.
     */
    public static BorderImplementor get(final JavaPlugin plugin) {
        final Optional<BorderImplementor> optionalBorderImplementor = cache.stream().filter(borderImplementor -> borderImplementor.getPlugin() == plugin).findFirst();
        return optionalBorderImplementor.orElseGet(() -> new BorderImplementor(plugin));
    }

}
