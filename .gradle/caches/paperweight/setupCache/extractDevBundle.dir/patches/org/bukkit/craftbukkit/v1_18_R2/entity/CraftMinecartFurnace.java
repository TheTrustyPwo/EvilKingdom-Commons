package org.bukkit.craftbukkit.v1_18_R2.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.PoweredMinecart;

@SuppressWarnings("deprecation")
public class CraftMinecartFurnace extends CraftMinecart implements PoweredMinecart {
    public CraftMinecartFurnace(CraftServer server, MinecartFurnace entity) {
        super(server, entity);
    }

    @Override
    public MinecartFurnace getHandle() {
        return (MinecartFurnace) entity;
    }

    @Override
    public int getFuel() {
        return this.getHandle().fuel;
    }

    @Override
    public void setFuel(int fuel) {
        Preconditions.checkArgument(fuel >= 0, "ticks cannot be negative");
        this.getHandle().fuel = fuel;
    }

    // Paper start
    @Override
    public double getPushX() {
        return getHandle().xPush;
    }

    @Override
    public double getPushZ() {
        return getHandle().zPush;
    }

    @Override
    public void setPushX(double xPush) {
        getHandle().xPush = xPush;
    }

    @Override
    public void setPushZ(double zPush) {
        getHandle().zPush = zPush;
    }
    // Paper end

    @Override
    public String toString() {
        return "CraftMinecartFurnace";
    }

    @Override
    public EntityType getType() {
        return EntityType.MINECART_FURNACE;
    }
}
