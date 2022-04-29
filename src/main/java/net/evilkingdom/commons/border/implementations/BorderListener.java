package net.evilkingdom.commons.border.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.border.BorderImplementor;
import net.evilkingdom.commons.border.objects.Border;
import net.evilkingdom.commons.menu.MenuImplementor;
import net.evilkingdom.commons.menu.objects.Menu;
import net.evilkingdom.commons.menu.objects.MenuItem;
import net.evilkingdom.commons.scoreboard.ScoreboardImplementor;
import net.evilkingdom.commons.scoreboard.objects.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class BorderListener implements Listener {

    private final JavaPlugin plugin;

    /**
     * Allows you to create a BorderListener for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin the BorderListener is for.
     */
    public BorderListener(final JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * The listener for player world changes.
     */
    @EventHandler
    public void onPlayerWorldChange(final PlayerChangedWorldEvent playerChangedWorldEvent) {
        final Player player = playerChangedWorldEvent.getPlayer();
        final Optional<Border> optionalBorder = BorderImplementor.get(this.plugin).getBorders().stream().filter(implementedBorder -> implementedBorder.getPlayer() == player).findFirst();
        if (optionalBorder.isPresent()) {
            final Border border = optionalBorder.get();
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> border.hide(), 20L);
        }
    }

    /**
     * The listener for player teleports.
     */
    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent playerTeleportEvent) {
        final Player player = playerTeleportEvent.getPlayer();
        final Optional<Border> optionalBorder = BorderImplementor.get(this.plugin).getBorders().stream().filter(implementedBorder -> implementedBorder.getPlayer() == player).findFirst();
        if (optionalBorder.isPresent()) {
            final Border border = optionalBorder.get();
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> border.hide(), 20L);
        }
    }

    /**
     * The listener for player quits.
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {
        final Player player = playerQuitEvent.getPlayer();
        final Optional<Border> optionalBorder = BorderImplementor.get(this.plugin).getBorders().stream().filter(implementedBorder -> implementedBorder.getPlayer() == player).findFirst();
        if (optionalBorder.isPresent()) {
            final Border border = optionalBorder.get();
            border.hide();
        }
    }

}
