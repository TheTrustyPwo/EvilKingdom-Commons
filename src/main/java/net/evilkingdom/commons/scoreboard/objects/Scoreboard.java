package net.evilkingdom.commons.scoreboard.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.mojang.authlib.GameProfile;
import net.evilkingdom.commons.scoreboard.ScoreboardImplementor;
import net.evilkingdom.commons.utilities.string.StringUtilities;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
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
    private Optional<Objective> objective;
    private Optional<Runnable> runnable;
    private HashMap<Integer, ServerPlayer> lineFakePlayers;

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
        this.lineFakePlayers = new HashMap<Integer, ServerPlayer>();
    }

    /**
     * Allows you to set scoreboard's lines.
     *
     * @param lines ~ The lines that will be set.
     */
    public void setLines(final ArrayList<String> lines) {
        if (lines.size() > 15) {
            return;
        }
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
        final Objective objective = scoreboard.registerNewObjective("csbo" + UUID.randomUUID().toString().replace("-", "").substring(0, 10), "dummy", Component.text(StringUtilities.colorize(this.title)));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective = Optional.of(objective);
        for (int i = this.lines.size(); i > 0; i--) {
            final String line = this.lines.stream().sorted(Comparator.reverseOrder()).toList().get(i);
            final GameProfile fakePlayerProfile = new GameProfile(UUID.randomUUID(), "csbfp-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
            final ServerPlayer fakePlayer = new ServerPlayer(((CraftServer) Bukkit.getServer()).getHandle().getServer(), ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle(), fakePlayerProfile);
            fakePlayer.listName = net.minecraft.network.chat.Component.nullToEmpty(line);
            objective.getScore(fakePlayer.getName().getString()).setScore(i);
            this.lineFakePlayers.put(i, fakePlayer);
        }
        if (this.player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
            this.player.setScoreboard(scoreboard);
        }
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
        for (int i = this.lineFakePlayers.keySet().size(); i > this.lines.size(); i--) {
            final ServerPlayer fakePlayer = this.lineFakePlayers.get(i);
            objective.getScore(fakePlayer.getName().getString()).resetScore();
            this.lineFakePlayers.remove(i);
        }
        for (int i = this.lines.size(); i > 0; i--) {
            final String line = this.lines.stream().sorted(Comparator.reverseOrder()).toList().get(i);
            ServerPlayer fakePlayer;
            if (this.lineFakePlayers.containsKey(i)) {
                fakePlayer = this.lineFakePlayers.get(i);
            } else {
                final GameProfile fakePlayerProfile = new GameProfile(UUID.randomUUID(), "csbfp-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
                fakePlayer = new ServerPlayer(((CraftServer) Bukkit.getServer()).getHandle().getServer(), ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle(), fakePlayerProfile);
                objective.getScore(fakePlayer.getName().getString()).setScore(i);
            }
            fakePlayer.listName = net.minecraft.network.chat.Component.nullToEmpty(line);
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
        this.lineFakePlayers.clear();
        final ScoreboardImplementor scoreboardImplementor = ScoreboardImplementor.get(this.plugin);
        scoreboardImplementor.getScoreboards().remove(this);
    }

}
