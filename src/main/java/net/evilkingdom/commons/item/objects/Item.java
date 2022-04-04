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

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class Item {

    private ItemMeta itemMeta;
    private ItemStack itemStack;

    /**
     * Allows you to create an item.
     *
     * @param itemStack ~ The item stack to create off.
     */
    public Item(final ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    /**
     * Allows you to set the amount of the item.
     *
     * @param amount ~ The amount to be set.
     */
    public void setAmount(final int amount) {
        this.itemStack.setAmount(amount);
    }

    /**
     * Allows you to make the item unbreakable.
     */
    public void unbreakable() {
        this.itemMeta.setUnbreakable(true);
        this.itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        this.itemStack.setItemMeta(this.itemMeta);
    }

    /**
     * Allows you to make the item glow.
     */
    public void glow() {
        if (!this.itemMeta.getEnchants().isEmpty()) {
            return;
        }
        this.itemMeta.addEnchant(Enchantment.RIPTIDE, 1, true);
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.itemStack.setItemMeta(this.itemMeta);
    }

    /**
     * Allows you to set the name of the item.
     *
     * @param name ~ The name to be set.
     */
    public void setName(final String name) {
        this.itemMeta.displayName(Component.text(name));
        this.itemStack.setItemMeta(this.itemMeta);
    }

    /**
     * Allows you to set the lore of the item.
     *
     * @param lore ~ The lore to be set.
     * @return The self class.
     */
    public Item setLore(final ArrayList<String> lore) {
        ArrayList<Component> componentLore = new ArrayList<Component>(lore.stream().map(loreItem -> Component.text(loreItem)).collect(Collectors.toList()));
        this.itemMeta.lore(componentLore);
        this.itemStack.setItemMeta(this.itemMeta);
        return this;
    }

    /**
     * Allows you to set the Base 64 of the head.
     * This only applies if the item is a PLAYER_HEAD.
     *
     * @param uuid ~ The UUID for the Player Profile (useful for stacking, etc.).
     * @param base64 ~ The Base 64 to be set.
     */
    public void setHeadBase64(final UUID uuid, final String base64) {
        if (this.itemStack.getType() == Material.PLAYER_HEAD) {
            final SkullMeta skullMeta = ((SkullMeta) this.itemMeta);
            final PlayerProfile playerProfile = Bukkit.getServer().createProfile(uuid);
            playerProfile.getProperties().add(new ProfileProperty("textures", base64));
            skullMeta.setPlayerProfile(playerProfile);
            this.itemStack.setItemMeta(this.itemMeta);
        }
    }

    /**
     * Allows you to set the owner of the item.
     * This only applies if the item is a PLAYER_HEAD.
     *
     * @param owner ~ The owner to be set.
     */
    public void setHeadOwner(final OfflinePlayer owner) {
        if (this.itemStack.getType() == Material.PLAYER_HEAD) {
            final SkullMeta skullMeta = ((SkullMeta) this.itemMeta);
            skullMeta.setOwningPlayer(owner);
            this.itemStack.setItemMeta(this.itemMeta);
        }
    }

    /**
     * Allows you to set the leather color of the item.
     * This only applies if the item is LEATHER_BOOTS, LEATHER_LEGGINGS, LEATHER_CHESTPLATE, or LEATHER_HELMET.
     *
     * @param color ~ The color to be set.
     */
    public void setLeatherColor(final Color color) {
        if (this.itemStack.getType() == Material.LEATHER_BOOTS || this.itemStack.getType() == Material.LEATHER_LEGGINGS || this.itemStack.getType() == Material.LEATHER_CHESTPLATE || this.itemStack.getType() == Material.LEATHER_HELMET) {
            final LeatherArmorMeta leatherArmorMeta = ((LeatherArmorMeta) this.itemMeta);
            leatherArmorMeta.setColor(color);
            this.itemStack.setItemMeta(this.itemMeta);
        }
    }

    /**
     * Allows you to add an enchant to the item.
     *
     * @param enchantment ~ The enchantment to be added.
     * @param level ~ The level of the enchantment to be added.
     */
    public void addEnchantment(final Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        this.itemStack.setItemMeta(this.itemMeta);
    }

    /**
     * Allows you to remove an enchant from the item.
     *
     * @param enchantment ~ The enchantment to be removed.
     */
    public void removeEnchantment(final Enchantment enchantment) {
        this.itemMeta.removeEnchant(enchantment);
        this.itemStack.setItemMeta(this.itemMeta);
    }

    /**
     * Allows you to set the custom model data of the item.
     *
     * @param customModelData ~ The custom model data ID.
     */
    public void setCustomModelData(final int customModelData) {
        this.itemMeta.setCustomModelData(customModelData);
        this.itemStack.setItemMeta(this.itemMeta);
    }

    /**
     * Allows you to get the item's ItemStack.
     *
     * @return The item's ItemStack.
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

}
