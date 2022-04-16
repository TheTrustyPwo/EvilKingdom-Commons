package net.evilkingdom.commons.item.objects;

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

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemData {

    private final JavaPlugin plugin;

    private ItemStack itemStack;

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
