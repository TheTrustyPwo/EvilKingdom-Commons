package net.evilkingdom.commons;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.utilities.string.StringUtilities;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Commons extends JavaPlugin implements Listener {

    private static Commons plugin;

    /**
     * Bukkit's detection for the plugin enabling.
     */
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(StringUtilities.colorize("&2[Commons] &aInitializing..."));
        plugin = this;
        Bukkit.getConsoleSender().sendMessage(StringUtilities.colorize("&2[Commons] &aInitialized."));
    }

    /**
     * Bukkit's detection for the plugin disabling.
     */
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(StringUtilities.colorize("&4[Commons] &cTerminating..."));
        plugin = null;
        Bukkit.getConsoleSender().sendMessage(StringUtilities.colorize("&4[Commons] &cTerminated."));
    }

    /**
     * Allows you to retrieve the plugin.
     *
     * @return The plugin.
     */
    public static Commons getPlugin() {
        return plugin;
    }

}
