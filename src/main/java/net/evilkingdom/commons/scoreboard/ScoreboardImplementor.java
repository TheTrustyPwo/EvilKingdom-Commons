package net.evilkingdom.commons.scoreboard;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.menu.MenuImplementor;
import net.evilkingdom.commons.scoreboard.implementations.ScoreboardListener;
import net.evilkingdom.commons.scoreboard.implementations.ScoreboardRunnable;
import net.evilkingdom.commons.scoreboard.objects.Scoreboard;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ScoreboardImplementor {

    private final JavaPlugin plugin;

    private final HashSet<Scoreboard> scoreboards;

    private static HashSet<ScoreboardImplementor> cache = new HashSet<ScoreboardImplementor>();

    /**
     * Allows you to create a ScoreboardImplementor.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin to create the ScoreboardImplementor for.
     */
    public ScoreboardImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        new ScoreboardListener(this.plugin);
        new ScoreboardRunnable(this.plugin);
        this.scoreboards = new HashSet<Scoreboard>();

        final HashSet<ScoreboardImplementor> previousCache = cache;
        previousCache.add(this);
        cache = previousCache;
    }

    /**
     * Allows you to retrieve the implementor's scoreboards.
     *
     * @return The implementor's scoreboards.
     */
    public HashSet<Scoreboard> getScoreboards() {
        return this.scoreboards;
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
     * Allows you to retrieve the ScoreboardImplementor for a plugin.
     * It will either pull it out of the cache or look for one if it doesn't exist.
     *
     * @param plugin ~ The plugin the ScoreboardImplementor is for.
     * @return The self class.
     */
    public static ScoreboardImplementor get(final JavaPlugin plugin) {
        final Optional<ScoreboardImplementor> optionalScoreboardImplementor = cache.stream().filter(scoreboardImplementor -> scoreboardImplementor.getPlugin() == plugin).findFirst();
        return optionalScoreboardImplementor.orElseGet(() -> new ScoreboardImplementor(plugin));
    }

}
