package net.evilkingdom.commons.constructor.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ConstructorRegion {

    private final JavaPlugin plugin;

    private Region region;

    /**
     * Allows you to create a region for a plugin.
     *
     * @param plugin ~ The plugin the region is for.
     * @param cornerOne ~ The region's corner one.
     * @param cornerTwo ~ The region's corner two.
     */
    public ConstructorRegion(final JavaPlugin plugin, final Location cornerOne, final Location cornerTwo) {
        this.plugin = plugin;

        this.region = new CuboidRegion(BukkitAdapter.asBlockVector(cornerOne), BukkitAdapter.asBlockVector(cornerTwo));
        this.region.setWorld(BukkitAdapter.adapt(cornerOne.getWorld()));
    }

    /**
     * Allows you to set the region's corner one.
     *
     * @param cornerOne ~ The corner one to set.
     */
    public void setCornerOne(final Location cornerOne) {
        this.region = new CuboidRegion(BukkitAdapter.asBlockVector(cornerOne), this.region.getMaximumPoint());
    }

    /**
     * Allows you to set the region's corner two.
     *
     * @param cornerTwo ~ The corner two to set.
     */
    public void setCornerTwo(final Location cornerTwo) {
        this.region = new CuboidRegion(this.region.getMinimumPoint(), BukkitAdapter.asBlockVector(cornerTwo));
    }

    /**
     * Allows you to retrieve the region's corner one.
     *
     * @return The region's corner one.
     */
    public Location getCornerOne() {
        return BukkitAdapter.adapt(BukkitAdapter.adapt(this.region.getWorld()), region.getMinimumPoint());
    }

    /**
     * Allows you to retrieve the region's corner two.
     *
     * @return The region's corner two.
     */
    public Location getCornerTwo() {
        return BukkitAdapter.adapt(BukkitAdapter.adapt(this.region.getWorld()), region.getMaximumPoint());
    }

    /**
     * Allows you to retrieve the region's plugin.
     *
     * @return The region's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to fill the region from a material.
     * Uses FastAsyncWorldEdit's API.
     *
     * @param material ~ The material to set.
     * @return True when the task is complete or false if something goes wrong.
     */
    public CompletableFuture<Boolean> fill(final Material material) {
        return CompletableFuture.supplyAsync(() -> {
            final BlockState blockState = BukkitAdapter.adapt(material.createBlockData());
            try (final EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(this.region.getWorld()).fastMode(true).build()) {
                editSession.setBlocks(this.region, blockState);
            } catch (final WorldEditException worldEditException) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to fill the region from a HashMap of blocks to their percentages.
     * Uses FastAsyncWorldEdit's API.
     *
     * @param blockToPercentages ~ The HashMap of blocks to their percentages to set.
     * @return True when the task is complete or false if something goes wrong.
     */
    public CompletableFuture<Boolean> fill(final HashMap<Material, Double> blockToPercentages) {
        return CompletableFuture.supplyAsync(() -> {
            final RandomPattern pattern = new RandomPattern();
            blockToPercentages.forEach(((material, percentage) -> pattern.add(BukkitAdapter.adapt(material.createBlockData()), percentage)));
            try (final EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(this.region.getWorld()).fastMode(true).build()) {
                editSession.setBlocks(this.region, pattern);
            } catch (final WorldEditException worldEditException) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to retrieve the region's block composition.
     * Uses FastAsyncWorldEdit's API.
     *
     * @return The region's block composition when the task is complete or an empty map if something goes wrong.
     */
    public CompletableFuture<HashMap<Material, Integer>> getBlockComposition() {
        return CompletableFuture.supplyAsync(() -> {
            final HashMap<Material, Integer> blockComposition = new HashMap<Material, Integer>();
            try (final EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(this.region.getWorld()).fastMode(true).build()) {
                editSession.getBlockDistribution(this.region, false).forEach(blockState -> {
                    final Material material = BukkitAdapter.adapt(blockState.getID().getBlockType());
                    int count = blockComposition.getOrDefault(material, 0);
                    count++;
                    blockComposition.put(material, count);
                });
            }
            return blockComposition;
        });
    }

    /**
     * Allows you to retrieve the region's block count.
     * Uses FastAsyncWorldEdit's API.
     *
     *
     * @return The region's block count.
     */
    public int getBlockCount() {
        return (int) this.region.getVolume();
    }

    /**
     * Allows you to check if a location is within the region.
     * Uses FastAsyncWorldEdit's API.
     *
     * @return If the location is in the region.
     */
    public boolean isWithin(final Location location) {
        return this.region.contains(BukkitAdapter.asBlockVector(location));
    }

}
