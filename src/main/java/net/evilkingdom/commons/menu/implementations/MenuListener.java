package net.evilkingdom.commons.menu.implementations;

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
        if (!menu.getSlots().containsKey(inventoryClickEvent.getSlot())) {
            return;
        }
        final MenuItem menuItem = menu.getSlots().get(inventoryClickEvent.getSlot());
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
        if (optionalMenu.isEmpty()) {
            return;
        }
        final Menu menu = optionalMenu.get();
        menu.close();
    }

}
