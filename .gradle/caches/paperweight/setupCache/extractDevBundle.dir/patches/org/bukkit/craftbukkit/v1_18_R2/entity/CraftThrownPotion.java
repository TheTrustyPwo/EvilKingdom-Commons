package org.bukkit.craftbukkit.v1_18_R2.entity;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R2.potion.CraftPotionUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class CraftThrownPotion extends CraftProjectile implements ThrownPotion {
    public CraftThrownPotion(CraftServer server, net.minecraft.world.entity.projectile.ThrownPotion entity) {
        super(server, entity);
    }

    @Override
    public Collection<PotionEffect> getEffects() {
        ImmutableList.Builder<PotionEffect> builder = ImmutableList.builder();
        for (MobEffectInstance effect : PotionUtils.getMobEffects(this.getHandle().getItemRaw())) {
            builder.add(CraftPotionUtil.toBukkit(effect));
        }
        return builder.build();
    }

    @Override
    public ItemStack getItem() {
        return CraftItemStack.asBukkitCopy(this.getHandle().getItemRaw());
    }

    @Override
    public void setItem(ItemStack item) {
        // The ItemStack must not be null.
        Validate.notNull(item, "ItemStack cannot be null.");

        // The ItemStack must be a potion.
        //Validate.isTrue(item.getType() == Material.LINGERING_POTION || item.getType() == Material.SPLASH_POTION, "ItemStack must be a lingering or splash potion. This item stack was " + item.getType() + "."); // Paper - Projectile API
        var meta = getPotionMeta(); // Paper - Projectile API

        this.getHandle().setItem(CraftItemStack.asNMSCopy(item));
        setPotionMeta(meta); // Paper - Projectile API
    }

    // Paper start - Projectile API
    @Override
    public org.bukkit.inventory.meta.PotionMeta getPotionMeta() {
        return (org.bukkit.inventory.meta.PotionMeta) CraftItemStack.getItemMeta(this.getHandle().getItemRaw(), Material.SPLASH_POTION);
    }

    @Override
    public void setPotionMeta(org.bukkit.inventory.meta.PotionMeta meta) {
        net.minecraft.world.item.ItemStack item = this.getHandle().getItem();
        CraftItemStack.applyMetaToItem(item, meta);
        this.getHandle().setItem(item); // Reset item
    }
    // Paper end
    @Override
    public net.minecraft.world.entity.projectile.ThrownPotion getHandle() {
        return (net.minecraft.world.entity.projectile.ThrownPotion) entity;
    }

    @Override
    public EntityType getType() {
        return EntityType.SPLASH_POTION;
    }
}
