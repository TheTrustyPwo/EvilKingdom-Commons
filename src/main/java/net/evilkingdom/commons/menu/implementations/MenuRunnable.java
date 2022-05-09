package net.evilkingdom.commons.menu.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.menu.MenuImplementor;
import net.evilkingdom.commons.menu.objects.Menu;
import net.evilkingdom.commons.menu.objects.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

public class MenuRunnable implements Runnable {

    private final JavaPlugin plugin;

    /**
     * Allows you to create a MenuRunnable for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin the MenuRunnable is for.
     */
    public MenuRunnable(final JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this, 0L, 20L);
    }

    /**
     * The runnable for the menus.
     */
    @Override
    public void run() {
        final MenuImplementor implementor = MenuImplementor.get(this.plugin);
        implementor.getMenus().forEach(menu -> menu.getRunnable().ifPresent(runnable ->  Bukkit.getScheduler().runTask(this.plugin, runnable)));
    }

}
