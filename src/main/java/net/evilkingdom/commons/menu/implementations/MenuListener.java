package net.evilkingdom.commons.menu.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.menu.MenuImplementor;
import net.evilkingdom.commons.menu.objects.Menu;
import net.evilkingdom.commons.menu.objects.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class MenuListener implements Listener {

    private final JavaPlugin plugin;

    /**
     * Allows you to create a MenuListener for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin the MenuListener is for.
     */
    public MenuListener(final JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * The listener for inventory clicking.
     */
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getCurrentItem() == null) {
            return;
        }
        final Player player = (Player) inventoryClickEvent.getWhoClicked();
        final MenuImplementor menuImplementor = MenuImplementor.get(this.plugin);
        final Optional<Menu> optionalMenu = menuImplementor.getMenus().stream().filter(menu -> menu.getPlayer() == player).findFirst();
        if (optionalMenu.isEmpty()) {
            return;
        }
        final Menu menu = optionalMenu.get();
        if (!menu.getItems().containsKey(inventoryClickEvent.getSlot())) {
            return;
        }
        final MenuItem menuItem = menu.getItems().get(inventoryClickEvent.getSlot());
        menuItem.getClickAction().accept(inventoryClickEvent);
    }

    /**
     * The listener for inventory closing.
     */
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent inventoryCloseEvent) {
        final Player player = (Player) inventoryCloseEvent.getPlayer();
        final MenuImplementor menuImplementor = MenuImplementor.get(this.plugin);
        final Optional<Menu> optionalMenu = menuImplementor.getMenus().stream().filter(menu -> menu.getPlayer() == player).findFirst();
        if (optionalMenu.isPresent()) {
            final Menu menu = optionalMenu.get();
            menu.close();
        }
    }

}
