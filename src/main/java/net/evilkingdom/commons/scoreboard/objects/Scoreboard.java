package net.evilkingdom.commons.scoreboard.objects;

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

import net.evilkingdom.commons.scoreboard.ScoreboardImplementor;
import net.evilkingdom.commons.utilities.string.StringUtilities;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Scoreboard {

    private final JavaPlugin plugin;

    private Player player;
    private ArrayList<String> lines;
    private String title;
    private HashMap<Integer, Team> lineTeams;
    private Optional<Objective> objective;
    private Optional<Runnable> runnable;

    /**
     * Allows you to create a scoreboard for a plugin.
     *
     * @param plugin ~ The plugin the scoreboard is for.
     * @param player ~ The player the scoreboard is for.
     */
    public Scoreboard(final JavaPlugin plugin, final Player player) {
        this.plugin = plugin;
        this.player = player;
        this.lines = new ArrayList<String>();
        this.lineTeams = new HashMap<Integer, Team>();
    }

    /**
     * Allows you to set scoreboard's lines.
     *
     * @param lines ~ The lines that will be set.
     */
    public void setLines(final ArrayList<String> lines) {
        this.lines = lines;
    }

    /**
     * Allows you to set the scoreboard's title.
     *
     * @param title ~ The title that will be set.
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Allows you to set the scoreboard's runnable.
     * This may serve useful for automatic updating scoreboards, etc.
     *
     * @param runnable ~ The runnable that will be set.
     */
    public void setRunnable(final Optional<Runnable> runnable) {
        this.runnable = runnable;
    }

    /**
     * Allows you to retrieve the scoreboard's runnable.
     *
     * @return The scoreboard's runnable.
     */
    public Optional<Runnable> getRunnable() {
        return this.runnable;
    }

    /**
     * Allows you to retrieve the scoreboard's title.
     *
     * @return The scoreboard's title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Allows you to retrieve the scoreboard's lines.
     *
     * @return The scoreboard's lines.
     */
    public ArrayList<String> getLines() {
        return this.lines;
    }

    /**
     * Allows you to retrieve the scoreboard's player.
     *
     * @return The scoreboard's player.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Allows you to retrieve the scoreboard's plugin.
     *
     * @return The scoreboard's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to show the scoreboard to the player.
     */
    public void show() {
        if (this.objective.isPresent()) {
            return;
        }
        final org.bukkit.scoreboard.Scoreboard scoreboard;
        if (this.player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        } else {
            scoreboard = this.player.getScoreboard();
        }
        Objective objective = scoreboard.registerNewObjective("csb-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12), "dummy", Component.text(StringUtilities.colorize(this.title)));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective = Optional.of(objective);
        for (int i = this.lines.size(); i > 0; i--) {
            String line = this.lines.stream().sorted(Comparator.reverseOrder()).toList().get(i);
            Team team = scoreboard.registerNewTeam(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
            team.addEntries(team.getName());
            team.displayName(Component.text(StringUtilities.colorize(line)));
            objective.getScore(team.getName()).setScore(i);
            this.lineTeams.put(i, team);
        }
        this.player.setScoreboard(scoreboard);
        final ScoreboardImplementor scoreboardImplementor = ScoreboardImplementor.get(this.plugin);
        scoreboardImplementor.getScoreboards().add(this);
    }

    /**
     * Allows you to update the scoreboard for the player.
     */
    public void update() {
        if (this.objective.isEmpty()) {
            return;
        }
        final Objective objective = this.objective.get();
        for (int i = 0; i < (this.lineTeams.keySet().size() - this.lines.size()); i++) {
            this.lineTeams.get(i).unregister();
        }
        for (int i = this.lines.size(); i > 0; i--) {
            String line = this.lines.stream().sorted(Comparator.reverseOrder()).toList().get(i);
            Team team;
            if (this.lineTeams.containsKey(i)) {
                team = this.lineTeams.get(i);
            } else {
                team = objective.getScoreboard().registerNewTeam(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
                team.addEntries(team.getName());
            }
            team.displayName(Component.text(StringUtilities.colorize(line)));
            objective.getScore(team.getName()).setScore(i);
        }
    }

    /**
     * Allows you to hide the scoreboard from the player.
     */
    public void hide() {
        if (this.objective.isEmpty()) {
            return;
        }
        this.objective.get().unregister();
        this.objective = Optional.empty();
        final ScoreboardImplementor scoreboardImplementor = ScoreboardImplementor.get(this.plugin);
        scoreboardImplementor.getScoreboards().remove(this);
    }

}
