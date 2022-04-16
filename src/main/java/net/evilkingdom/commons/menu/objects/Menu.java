package net.evilkingdom.commons.menu.objects;

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
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Optional;

public class Menu {

    private final JavaPlugin plugin;

    private String data;
    private final Player player;
    private final String title;
    private final InventoryType type;
    private Optional<Integer> rows;
    private Optional<Inventory> inventory;
    private Optional<Runnable> runnable;
    private HashMap<Integer, MenuItem> slots;

    /**
     * Allows you to create a menu for a plugin.
     * This is used for CHEST interfaces.
     *
     * @param plugin ~ The plugin the menu is for.
     * @param player ~ The player the menu is for.
     * @param rows ~ The rows of the menu.
     * @param title ~ The title of the menu.
     */
    public Menu(final JavaPlugin plugin, final Player player, final Integer rows, final String title) {
        this.plugin = plugin;

        this.player = player;
        this.title = title;
        this.rows = Optional.of(rows);
        this.type = InventoryType.CHEST;
        this.slots = new HashMap<Integer, MenuItem>();
    }

    /**
     * Allows you to create a menu for a plugin.
     * This is for anything but CHEST interfaces.
     *
     * @param plugin ~ The plugin the menu is for.
     * @param player ~ The player the menu is for.
     * @param type ~ The type of menu.
     * @param title ~ The title of the menu.
     */
    public Menu(final JavaPlugin plugin, final Player player, final InventoryType type, final String title) {
        this.plugin = plugin;

        this.player = player;
        this.title = title;
        this.type = type;
        this.slots = new HashMap<Integer, MenuItem>();
    }

    /**
     * Allows you to retrieve the menu's slots.
     *
     * @return The menu's slots.
     */
    public HashMap<Integer, MenuItem> getSlots() {
        return this.slots;
    }

    /**
     * Allows you to set the menu's data.
     * This may serve useful for paginated interfaces, etc.
     *
     * @param data ~ The data that will be set.
     */
    public void setData(final String data) {
        this.data = data;
    }

    /**
     * Allows you to retrieve the menu's data.
     *
     * @return The menu's data.
     */
    public String getData() {
        return this.data;
    }

    /**
     * Allows you to set the menu's runnable.
     * This may serve useful for automatic updating menus, etc.
     *
     * @param runnable ~ The runnable that will be set.
     */
    public void setRunnable(final Optional<Runnable> runnable) {
        this.runnable = runnable;
    }

    /**
     * Allows you to retrieve the menu's runnable.
     *
     * @return The menu's runnable.
     */
    public Optional<Runnable> getRunnable() {
        return this.runnable;
    }

    /**
     * Allows you to retrieve the menu's player.
     *
     * @return The menu's player.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Allows you to retrieve the menu's plugin.
     *
     * @return The menu's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the menu's rows.
     *
     * @return The menu's rows.
     */
    public Optional<Integer> getRows() {
        return this.rows;
    }

    /**
     * Allows you to retrieve the menu's type.
     *
     * @return The menu's type.
     */
    public InventoryType getType() {
        return this.type;
    }

    /**
     * Allows you to open the menu for the player.
     */
    public void open() {
        if (this.inventory.isPresent()) {
            return;
        }
        if (this.type == InventoryType.CHEST) {
            this.inventory = Optional.of(Bukkit.getServer().createInventory(null, this.rows.get(), Component.text(title)));
        } else {
            this.inventory = Optional.of(Bukkit.getServer().createInventory(null, this.type, Component.text(title)));
        }
        Inventory inventory = this.inventory.get();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (this.slots.get(i) != null) {
                inventory.setItem(i, this.slots.get(i).getItemStack());
            }
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
        this.player.openInventory(inventory);
        final MenuImplementor menuImplementor = MenuImplementor.get(this.plugin);
        menuImplementor.getMenus().add(this);
    }

    /**
     * Allows you to update the menu for the player.
     */
    public void update() {
        if (this.inventory.isPresent()) {
            return;
        }
        Inventory inventory = this.inventory.get();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (this.slots.get(i) != null) {
                inventory.setItem(i, this.slots.get(i).getItemStack());
            }
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
    }

    /**
     * Allows you to close the menu for the player.
     */
    public void close() {
        if (this.inventory.isPresent()) {
            return;
        }
        this.player.closeInventory();
        this.inventory = Optional.empty();
        final MenuImplementor menuImplementor = MenuImplementor.get(this.plugin);
        menuImplementor.getMenus().remove(this);
    }

    /**
     * Allows you to swap the menu to another menu for the player.
     * Pretty similar to the close function, except it abuses the fact that Bukkit closes the current inventory when opening another one (so it shouldn't move their cursor).
     *
     * @param menu ~ The menu to swap to.
     */
    public void swap(final Menu menu) {
        if (this.inventory.isPresent()) {
            return;
        }
        final MenuImplementor menuImplementor = MenuImplementor.get(this.plugin);
        menuImplementor.getMenus().remove(this);
        menu.open();
    }

}