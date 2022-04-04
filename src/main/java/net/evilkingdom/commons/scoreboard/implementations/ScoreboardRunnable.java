package net.evilkingdom.commons.scoreboard.implementations;

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

import net.evilkingdom.commons.menu.MenuImplementor;
import net.evilkingdom.commons.menu.objects.Menu;
import net.evilkingdom.commons.scoreboard.ScoreboardImplementor;
import net.evilkingdom.commons.scoreboard.objects.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ScoreboardRunnable implements Runnable {

    private final JavaPlugin plugin;

    /**
     * Allows you to create a ScoreboardRunnable for a plugin.
     * This is used in the ScoreboardImplementor to register the scoreboard system on plugins.
     *
     * @param plugin ~ The plugin the ScoreboardRunnable is for.
     */
    public ScoreboardRunnable(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Allows you to register the runnable.
     */
    public void register() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, this, 0L, 5L);
    }

    /**
     * The runnable for updating scoreboards.
     */
    @Override
    public void run() {
        final ScoreboardImplementor scoreboardImplementor = ScoreboardImplementor.get(this.plugin);
        scoreboardImplementor.getScoreboards().forEach(scoreboard -> {
            scoreboard.getRunnable().ifPresent(runnable -> runnable.run());
            scoreboard.update();
        });
    }

}
