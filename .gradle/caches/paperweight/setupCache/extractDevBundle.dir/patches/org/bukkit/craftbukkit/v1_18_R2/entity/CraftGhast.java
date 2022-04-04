package org.bukkit.craftbukkit.v1_18_R2.entity;

import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;

public class CraftGhast extends CraftFlying implements Ghast {

    public CraftGhast(CraftServer server, net.minecraft.world.entity.monster.Ghast entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.Ghast getHandle() {
        return (net.minecraft.world.entity.monster.Ghast) entity;
    }

    @Override
    public String toString() {
        return "CraftGhast";
    }

    @Override
    public EntityType getType() {
        return EntityType.GHAST;
    }

    // Paper start
    @Override
    public boolean isCharging() {
        return this.getHandle().isCharging();
    }

    @Override
    public void setCharging(boolean charging) {
        this.getHandle().setCharging(charging);
    }

    @Override
    public int getExplosionPower() {
        return this.getHandle().getExplosionPower();
    }

    @Override
    public void setExplosionPower(int explosionPower) {
        com.google.common.base.Preconditions.checkArgument(explosionPower >= 0 && explosionPower <= 127, "The explosion power has to be between 0 and 127");
        this.getHandle().setExplosionPower(explosionPower);
    }
    // Paper end
}
