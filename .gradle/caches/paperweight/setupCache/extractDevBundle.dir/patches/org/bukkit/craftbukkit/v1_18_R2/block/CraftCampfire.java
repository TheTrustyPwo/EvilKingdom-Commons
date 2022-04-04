package org.bukkit.craftbukkit.v1_18_R2.block;

import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.bukkit.World;
import org.bukkit.block.Campfire;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class CraftCampfire extends CraftBlockEntityState<CampfireBlockEntity> implements Campfire {

    public CraftCampfire(World world, CampfireBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    @Override
    public int getSize() {
        return getSnapshot().getItems().size();
    }

    @Override
    public ItemStack getItem(int index) {
        net.minecraft.world.item.ItemStack item = getSnapshot().getItems().get(index);
        return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
    }

    @Override
    public void setItem(int index, ItemStack item) {
        getSnapshot().getItems().set(index, CraftItemStack.asNMSCopy(item));
    }

    @Override
    public int getCookTime(int index) {
        return getSnapshot().cookingProgress[index];
    }

    @Override
    public void setCookTime(int index, int cookTime) {
        getSnapshot().cookingProgress[index] = cookTime;
    }

    @Override
    public int getCookTimeTotal(int index) {
        return getSnapshot().cookingTime[index];
    }

    @Override
    public void setCookTimeTotal(int index, int cookTimeTotal) {
        getSnapshot().cookingTime[index] = cookTimeTotal;
    }

    // Paper start
    @Override
    public void stopCooking() {
        for (int i = 0; i < getSnapshot().stopCooking.length; ++i)
            this.stopCooking(i);
    }

    @Override
    public void startCooking() {
        for (int i = 0; i < getSnapshot().stopCooking.length; ++i)
            this.startCooking(i);
    }

    @Override
    public boolean stopCooking(int index) {
        org.apache.commons.lang.Validate.isTrue(-1 < index && index < 4, "Slot index must be between 0 (incl) to 3 (incl)");
        boolean previous = this.isCookingDisabled(index);
        getSnapshot().stopCooking[index] = true;
        return previous;
    }

    @Override
    public boolean startCooking(int index) {
        org.apache.commons.lang.Validate.isTrue(-1 < index && index < 4, "Slot index must be between 0 (incl) to 3 (incl)");
        boolean previous = this.isCookingDisabled(index);
        getSnapshot().stopCooking[index] = false;
        return previous;
    }

    @Override
    public boolean isCookingDisabled(int index) {
        org.apache.commons.lang.Validate.isTrue(-1 < index && index < 4, "Slot index must be between 0 (incl) to 3 (incl)");
        return getSnapshot().stopCooking[index];
    }
    // Paper end
}
