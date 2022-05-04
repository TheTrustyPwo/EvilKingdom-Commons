package net.evilkingdom.commons.item.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemData {

    private final JavaPlugin plugin;

    private final ItemStack itemStack;

    /**
     * Allows you to create an ItemData for a plugin.
     *
     * @param plugin ~ The ItemData's plugin.
     * @param itemStack ~ The ItemData's item.
     */
    public ItemData(final JavaPlugin plugin, final ItemStack itemStack) {
        this.plugin = plugin;

        this.itemStack = itemStack;
    }

    /**
     * Allows you to retrieve a value on the item's data.
     *
     * @param key ~ The key of the data.
     * @param keyType ~ The key of the data's type.
     * @return The value on the item's data.
     */
    public Object getValue(final String key, final PersistentDataType keyType) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, key);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.get(namespacedKey, keyType);
    }

    /**
     * Allows you to retrieve if the item's data has a key.
     *
     * @param key ~ The key of the data.
     * @param keyType ~ The key of the data's type.
     * @return If the item's data has the key.
     */
    public boolean hasKey(final String key, final PersistentDataType keyType) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, key);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(namespacedKey, keyType);
    }

    /**
     * Allows you to remove a key on the item's data.
     *
     * @param key ~ The key to remove from the item's data.
     */
    public void removeKey(final String key) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, key);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.remove(namespacedKey);
        this.itemStack.setItemMeta(itemMeta);
    }

    /**
     * Allows you to set a value on the item's data.
     *
     * @param key ~ The key of the data.
     * @param value ~ The value of the data.
     * @param valueType ~ The value of the data's type.
     */
    public void setValue(final String key, final Object value, final PersistentDataType valueType) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, key);
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(namespacedKey, valueType, value);
        this.itemStack.setItemMeta(itemMeta);
    }

    /**
     * Allows you to retrieve the ItemData's ItemStack.
     *
     * @return The ItemData's ItemStack.
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * Allows you to retrieve the ItemData's plugin.
     *
     * @return The retrieve the ItemData's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

}
