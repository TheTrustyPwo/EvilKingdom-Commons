package net.evilkingdom.commons.item.objects;

/*
 * Made with love by https://kodirati.com/.
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

    private final ItemStack itemStack;

    /**
     * Allows you to create an item.
     *
     * @param itemStack ~ The item stack to create off.
     */
    public Item(final ItemStack itemStack) {
        this.itemStack = itemStack;
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
        this.itemStack.getItemMeta().setUnbreakable(true);
        this.itemStack.getItemMeta().addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    }

    /**
     * Allows you to make the item glow.
     */
    public void glow() {
        if (!this.itemStack.getItemMeta().getEnchants().isEmpty()) {
            return;
        }
        this.itemStack.getItemMeta().addEnchant(Enchantment.RIPTIDE, 1, true);
        this.itemStack.getItemMeta().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.itemStack.setItemMeta(this.itemStack.getItemMeta());
    }

    /**
     * Allows you to set the name of the item.
     *
     * @param name ~ The name to be set.
     */
    public void setName(final String name) {
        this.itemStack.getItemMeta().displayName(Component.text(name));
        this.itemStack.setItemMeta(this.itemStack.getItemMeta());
    }

    /**
     * Allows you to set the lore of the item.
     *
     * @param lore ~ The lore to be set.
     */
    public void setLore(final ArrayList<String> lore) {
        ArrayList<Component> componentLore = new ArrayList<Component>(lore.stream().map(loreItem -> Component.text(loreItem)).collect(Collectors.toList()));
        this.itemStack.getItemMeta().lore(componentLore);
        this.itemStack.setItemMeta(this.itemStack.getItemMeta());
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
            final SkullMeta skullMeta = ((SkullMeta) this.itemStack.getItemMeta());
            final PlayerProfile playerProfile = Bukkit.getServer().createProfile(uuid);
            playerProfile.getProperties().add(new ProfileProperty("textures", base64));
            skullMeta.setPlayerProfile(playerProfile);
            this.itemStack.setItemMeta(this.itemStack.getItemMeta());
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
            final SkullMeta skullMeta = ((SkullMeta) this.itemStack.getItemMeta());
            skullMeta.setOwningPlayer(owner);
            this.itemStack.setItemMeta(this.itemStack.getItemMeta());
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
            final LeatherArmorMeta leatherArmorMeta = ((LeatherArmorMeta) this.itemStack.getItemMeta());
            leatherArmorMeta.setColor(color);
            this.itemStack.setItemMeta(this.itemStack.getItemMeta());
        }
    }

    /**
     * Allows you to add an enchant to the item.
     *
     * @param enchantment ~ The enchantment to be added.
     * @param level ~ The level of the enchantment to be added.
     */
    public void addEnchantment(final Enchantment enchantment, int level) {
        this.itemStack.getItemMeta().addEnchant(enchantment, level, true);
        this.itemStack.setItemMeta(this.itemStack.getItemMeta());
    }

    /**
     * Allows you to remove an enchant from the item.
     *
     * @param enchantment ~ The enchantment to be removed.
     */
    public void removeEnchantment(final Enchantment enchantment) {
        this.itemStack.getItemMeta().removeEnchant(enchantment);
        this.itemStack.setItemMeta(this.itemStack.getItemMeta());
    }

    /**
     * Allows you to set the custom model data of the item.
     *
     * @param customModelData ~ The custom model data ID.
     */
    public void setCustomModelData(final int customModelData) {
        this.itemStack.getItemMeta().setCustomModelData(customModelData);
        this.itemStack.setItemMeta(this.itemStack.getItemMeta());
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
