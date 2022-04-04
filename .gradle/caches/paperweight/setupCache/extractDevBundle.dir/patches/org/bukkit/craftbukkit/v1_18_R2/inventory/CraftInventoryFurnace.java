package org.bukkit.craftbukkit.v1_18_R2.inventory;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryFurnace extends CraftInventory implements FurnaceInventory {
    public CraftInventoryFurnace(AbstractFurnaceBlockEntity inventory) {
        super(inventory);
    }

    @Override
    public ItemStack getResult() {
        return getItem(2);
    }

    @Override
    public ItemStack getFuel() {
        return getItem(1);
    }

    @Override
    public ItemStack getSmelting() {
        return getItem(0);
    }

    @Override
    public void setFuel(ItemStack stack) {
        setItem(1, stack);
    }

    @Override
    public void setResult(ItemStack stack) {
        setItem(2, stack);
    }

    @Override
    public void setSmelting(ItemStack stack) {
        setItem(0, stack);
    }

    // Paper start
    @Override
    public boolean isFuel(ItemStack stack) {
        return stack != null && !stack.getType().isEmpty() && AbstractFurnaceBlockEntity.isFuel(CraftItemStack.asNMSCopy(stack));
    }

    @Override
    public boolean canSmelt(ItemStack stack) {
        // data packs are always loaded in the main world
        net.minecraft.server.level.ServerLevel world = ((org.bukkit.craftbukkit.v1_18_R2.CraftWorld) org.bukkit.Bukkit.getWorlds().get(0)).getHandle();
        return stack != null && !stack.getType().isEmpty() && world.getRecipeManager().getRecipeFor(((AbstractFurnaceBlockEntity) this.inventory).recipeType, new net.minecraft.world.SimpleContainer(CraftItemStack.asNMSCopy(stack)), world).isPresent();
    }
    // Paper end

    @Override
    public Furnace getHolder() {
        return (Furnace) inventory.getOwner();
    }
}
