package net.evilkingdom.commons.scoreboard.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.menu.MenuImplementor;
import net.evilkingdom.commons.menu.objects.Menu;
import net.evilkingdom.commons.scoreboard.ScoreboardImplementor;
import net.evilkingdom.commons.scoreboard.objects.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ScoreboardRunnable implements Runnable {

    private final JavaPlugin plugin;

    /**
     * Allows you to create a ScoreboardRunnable for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin the ScoreboardRunnable is for.
     */
    public ScoreboardRunnable(final JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this, 0L, 5L);
    }

    /**
     * The runnable for updating scoreboards.
     */
    @Override
    public void run() {
        final ScoreboardImplementor implementor = ScoreboardImplementor.get(this.plugin);
        implementor.getScoreboards().forEach(scoreboard -> scoreboard.getRunnable().ifPresent(runnable -> Bukkit.getScheduler().runTask(this.plugin, runnable)));
    }

}
