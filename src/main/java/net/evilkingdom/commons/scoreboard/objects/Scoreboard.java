package net.evilkingdom.commons.scoreboard.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.mojang.authlib.GameProfile;
import net.evilkingdom.commons.scoreboard.ScoreboardImplementor;
import net.evilkingdom.commons.utilities.string.StringUtilities;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.units.qual.C;

import javax.swing.text.html.Option;
import java.util.*;

public class Scoreboard {

    private final JavaPlugin plugin;

    private Player player;
    private ArrayList<String> lines, currentLines;
    private String title;
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
        this.objective = Optional.empty();
        this.runnable = Optional.empty();
        this.lines = new ArrayList<String>();
        this.currentLines = new ArrayList<String>();
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
        if (player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        } else {
            scoreboard = player.getScoreboard();
        }
        final ArrayList<Packet<?>> packets = new ArrayList<Packet<?>>();
        final net.minecraft.world.scores.Objective objective = new net.minecraft.world.scores.Objective(((CraftScoreboard) scoreboard).getHandle(), "csbo" + UUID.randomUUID().toString().replace("-", "").substring(0, 10), ObjectiveCriteria.DUMMY, net.minecraft.network.chat.Component.nullToEmpty(StringUtilities.colorize(this.title)), ObjectiveCriteria.RenderType.INTEGER);
        final ClientboundSetObjectivePacket clientboundSetObjectivePacket = new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_ADD);
        final ClientboundSetDisplayObjectivePacket clientboundSetDisplayObjectivePacket = new ClientboundSetDisplayObjectivePacket(1, objective);
        packets.add(clientboundSetObjectivePacket);
        packets.add(clientboundSetDisplayObjectivePacket);
        final ArrayList<String> clonedLines = this.lines;
        Collections.reverse(clonedLines);
        for (int i = 0; i < clonedLines.size(); i++) {
            final String line = clonedLines.get(i);
            final ClientboundSetScorePacket clientboundSetScorePacket = new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, objective.getName(), line, i);
            packets.add(clientboundSetScorePacket);
        }
        this.currentLines = this.lines;
        this.objective = Optional.of(objective);
        packets.forEach(packet -> ((CraftPlayer) this.player).getHandle().connection.send(packet));
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
        final ArrayList<Packet<?>> packets = new ArrayList<Packet<?>>();
        for (int i = this.lines.size(); i < this.currentLines.size(); i++) {
            final String currentLine = this.currentLines.get(i);
            final ClientboundSetScorePacket clientboundSetScorePacket = new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, this.objective.get().getName(), currentLine, i);
            packets.add(clientboundSetScorePacket);
        }
        final ArrayList<String> clonedLines = this.lines;
        Collections.reverse(clonedLines);
        for (int i = 0; i < clonedLines.size(); i++) {
            final String line = clonedLines.get(i);
            final ClientboundSetScorePacket clientboundSetScorePacket = new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, this.objective.get().getName(), line, i);
            packets.add(clientboundSetScorePacket);
        }
        this.currentLines = this.lines;
        packets.forEach(packet -> ((CraftPlayer) this.player).getHandle().connection.send(packet));
    }

    /**
     * Allows you to hide the scoreboard from the player.
     */
    public void hide() {
        if (this.objective.isEmpty()) {
            return;
        }
        final ClientboundSetObjectivePacket clientboundSetObjectivePacket = new ClientboundSetObjectivePacket(objective.get(), ClientboundSetObjectivePacket.METHOD_REMOVE);
        ((CraftPlayer) this.player).getHandle().connection.send(clientboundSetObjectivePacket);
        this.objective = Optional.empty();
        this.currentLines.clear();
        final ScoreboardImplementor scoreboardImplementor = ScoreboardImplementor.get(this.plugin);
        scoreboardImplementor.getScoreboards().remove(this);
    }

}
