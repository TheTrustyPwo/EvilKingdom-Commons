package net.evilkingdom.commons.menu.objects;

/*
 * Made with love by https://kodirati.com/.
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

    private String identifier;
    private final Player player;
    private final String title;
    private final InventoryType type;
    private Optional<Integer> rows;
    private Optional<Inventory> inventory;
    private Optional<Runnable> runnable;
    private HashMap<Integer, MenuItem> items;

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
        this.runnable = Optional.empty();
        this.inventory = Optional.empty();
        this.items = new HashMap<Integer, MenuItem>();
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
        this.runnable = Optional.empty();
        this.inventory = Optional.empty();
        this.items = new HashMap<Integer, MenuItem>();
    }

    /**
     * Allows you to retrieve the menu's items.
     *
     * @return The menu's items.
     */
    public HashMap<Integer, MenuItem> getItems() {
        return this.items;
    }

    /**
     * Allows you to set the menu's identifier.
     * This may serve useful for paginated interfaces, etc.
     *
     * @param identifier ~ The identifier that will be set.
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * Allows you to retrieve the menu's identifier.
     *
     * @return The menu's identifier.
     */
    public String getIdentifier() {
        return this.identifier;
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
            this.inventory = Optional.of(Bukkit.getServer().createInventory(null, (this.rows.get() * 9), Component.text(title)));
        } else {
            this.inventory = Optional.of(Bukkit.getServer().createInventory(null, this.type, Component.text(title)));
        }
        final Inventory inventory = this.inventory.get();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (this.items.containsKey(i)) {
                inventory.setItem(i, this.items.get(i).getItemStack());
            } else {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }
        final MenuImplementor menuImplementor = MenuImplementor.get(this.plugin);
        final Optional<Menu> previousMenu = menuImplementor.getMenus().stream().filter(menu -> menu.getPlayer() == this.player).findFirst();
        previousMenu.ifPresent(menu -> menuImplementor.getMenus().remove(menu));
        this.player.openInventory(inventory);
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
            if (this.items.containsKey(i)) {
                inventory.setItem(i, this.items.get(i).getItemStack());
            } else {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
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

}
