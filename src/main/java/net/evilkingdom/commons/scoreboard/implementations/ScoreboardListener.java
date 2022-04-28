package net.evilkingdom.commons.scoreboard.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class ScoreboardListener implements Listener {

    private final JavaPlugin plugin;

    /**
     * Allows you to create a ScoreboardListener for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin the ScoreboardListener is for.
     */
    public ScoreboardListener(final JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * The listener for player quits.
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {
        final Player player = playerQuitEvent.getPlayer();
        final Optional<Scoreboard> optionalScoreboard = ScoreboardImplementor.get(this.plugin).getScoreboards().stream().filter(implementedScoreboard -> implementedScoreboard.getPlayer() == player).findFirst();
        if (optionalScoreboard.isPresent()) {
            final Scoreboard scoreboard = optionalScoreboard.get();
            scoreboard.hide();
        }
    }

}
