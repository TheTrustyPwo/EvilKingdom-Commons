package net.evilkingdom.commons.menu.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class MenuItem {

    private final ItemStack itemStack;
    private Consumer<InventoryClickEvent> clickAction;

    /**
     * Allows you to create an item for a menu.
     *
     * @param itemStack ~ The item showcased.
     * @param clickAction ~ The action preformed upon interacting with the item.
     */
    public MenuItem(final ItemStack itemStack, final Consumer<InventoryClickEvent> clickAction) {
        this.itemStack = itemStack;
        this.clickAction = clickAction;
    }
    /**
     * Allows you to retrieve the action preformed upon interacting with the item.
     *
     * @return The action preformed upon interacting with the item.
     */
    public Consumer<InventoryClickEvent> getClickAction() {
        return this.clickAction;
    }

    /**
     * Allows you to retrieve the showcased item.
     *
     * @return The showcased item.
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

}
