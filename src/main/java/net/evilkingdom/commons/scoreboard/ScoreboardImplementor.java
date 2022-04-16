package net.evilkingdom.commons.scoreboard;

/*
 * This file is part of Commons (Server), licensed under the MIT License.
 *
 *  Copyright (c) kodirati (kodirati.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import net.evilkingdom.commons.scoreboard.implementations.ScoreboardRunnable;
import net.evilkingdom.commons.scoreboard.objects.Scoreboard;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ScoreboardImplementor {

    private final JavaPlugin plugin;

    private ArrayList<Scoreboard> scoreboards;

    private static final Set<ScoreboardImplementor> cache = new HashSet<ScoreboardImplementor>();

    /**
     * Allows you to create a ScoreboardImplementor.
     *
     * @param plugin ~ The plugin to create the ScoreboardImplementor for.
     */
    public ScoreboardImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        this.scoreboards = new ArrayList<Scoreboard>();
        new ScoreboardRunnable(this.plugin);

        cache.add(this);
    }

    /**
     * Allows you to retrieve the ScoreboardImplementor's scoreboards.
     *
     * @return The ScoreboardImplementor's scoreboards.
     */
    public ArrayList<Scoreboard> getScoreboards() {
        return this.scoreboards;
    }

    /**
     * Allows you to retrieve the ScoreboardImplementor's plugin.
     *
     * @return The ScoreboardImplementor's plugin.
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
