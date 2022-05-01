package net.evilkingdom.commons.utilities.inventory;

/*
 * Made with love by https://kodirati.com/.
 */

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtilities {

    /**
     * Allows you to check if an inventory can fit an ItemStack.
     *
     * @param inventory ~ The inventory that needs to be checked.
     * @param itemStack ~ The ItemStack that needs to be checked.
     * @return If the checked ItemStack can fit in the checked inventory.
     */
    public static boolean canFit(final Inventory inventory, final ItemStack itemStack) {
        final Inventory clonedInventory = Bukkit.getServer().createInventory(inventory.getHolder(), inventory.getType());
        clonedInventory.setContents(inventory.getContents());
        return clonedInventory.addItem(itemStack).isEmpty();
    }

}
