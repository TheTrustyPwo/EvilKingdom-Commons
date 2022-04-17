package net.evilkingdom.commons.constructor.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.evilkingdom.commons.constructor.implementations.ConstructorTask;
import net.evilkingdom.commons.utilities.number.NumberUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
     * Load balanced and chunk based using WorldEdit's API.
     *
     * @param material ~ The material to set.
     * @return True when the task is complete.
     */
    public CompletableFuture<Boolean> fill(final Material material) {
        return CompletableFuture.supplyAsync(() -> {
            final HashMap<Long, ArrayList<Location>> chunkBlockLocations = new HashMap<Long, ArrayList<Location>>();
            this.region.forEach(blockBlockVector3 -> {
                final Location blockLocation = BukkitAdapter.adapt(BukkitAdapter.adapt(this.region.getWorld()), blockBlockVector3);
                final ArrayList<Location> blockLocations = chunkBlockLocations.getOrDefault(blockLocation.getChunk().getChunkKey(), new ArrayList<Location>());
                blockLocations.add(blockLocation);
                chunkBlockLocations.put(blockLocation.getChunk().getChunkKey(), blockLocations);
            });
            return chunkBlockLocations;
        }).thenApply(chunkBlockLocations -> {
            final ArrayList<Clipboard> clipboards = new ArrayList<Clipboard>();
            chunkBlockLocations.forEach((chunkKey, blockLocations) -> {
                final Chunk chunk = BukkitAdapter.adapt(this.region.getWorld()).getChunkAt(chunkKey);
                final Location chunkLocation = new Location(chunk.getWorld(), chunk.getX() << 4, 69, chunk.getZ() << 4).add(7, 0, 7);
                final Location cornerOne = new Location(chunkLocation.getWorld(), chunkLocation.getX(), chunkLocation.getWorld().getMaxHeight(), chunkLocation.getZ()).add(8, 0, 9);
                final Location cornerTwo = new Location(chunkLocation.getWorld(), chunkLocation.getX(), chunkLocation.getWorld().getMinHeight(), chunkLocation.getZ()).add(-7, 0, -8);
                final CuboidRegion region = new CuboidRegion(BukkitAdapter.asBlockVector(cornerOne), BukkitAdapter.asBlockVector(cornerTwo));
                region.setWorld(this.region.getWorld());
                final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                clipboard.setOrigin(BukkitAdapter.asBlockVector(cornerOne));
                blockLocations.forEach(blockLocation -> {
                    try {
                        clipboard.setBlock(BukkitAdapter.asBlockVector(blockLocation), BukkitAdapter.adapt(material.createBlockData()));
                    } catch (final WorldEditException ignored) {
                        //Does nothing! :>
                    }
                });
                clipboards.add(clipboard);
            });
            return clipboards;
        }).thenApplyAsync(clipboards -> {
            final ConstructorTask constructorTask = new ConstructorTask(this.plugin, clipboards);
            constructorTask.start();
            while (constructorTask.isRunning());
            return true;
        });
    }

    /**
     * Allows you to fill the region from a HashMap of blocks to their percentages.
     * Load balanced and chunk based using WorldEdit's API.
     *
     * @param blockToPercentages ~ The HashMap of blocks to their percentages to set.
     * @return True when the task is complete.
     */
    public CompletableFuture<Boolean> fill(final HashMap<Material, Double> blockToPercentages) {
        return CompletableFuture.supplyAsync(() -> {
            final HashMap<Long, ArrayList<Location>> chunkBlockLocations = new HashMap<Long, ArrayList<Location>>();
            this.region.forEach(blockBlockVector3 -> {
                final Location blockLocation = BukkitAdapter.adapt(BukkitAdapter.adapt(this.region.getWorld()), blockBlockVector3);
                final ArrayList<Location> blockLocations = chunkBlockLocations.getOrDefault(blockLocation.getChunk().getChunkKey(), new ArrayList<Location>());
                blockLocations.add(blockLocation);
                chunkBlockLocations.put(blockLocation.getChunk().getChunkKey(), blockLocations);
            });
            return chunkBlockLocations;
        }).thenApply(chunkBlockLocations -> {
            final ArrayList<Clipboard> clipboards = new ArrayList<Clipboard>();
            chunkBlockLocations.forEach((chunkKey, blockLocations) -> {
                final Chunk chunk = BukkitAdapter.adapt(this.region.getWorld()).getChunkAt(chunkKey);
                final Location chunkLocation = new Location(chunk.getWorld(), chunk.getX() << 4, 69, chunk.getZ() << 4).add(7, 0, 7);
                final Location cornerOne = new Location(chunkLocation.getWorld(), chunkLocation.getX(), chunkLocation.getWorld().getMaxHeight(), chunkLocation.getZ()).add(8, 0, 9);
                final Location cornerTwo = new Location(chunkLocation.getWorld(), chunkLocation.getX(), chunkLocation.getWorld().getMinHeight(), chunkLocation.getZ()).add(-7, 0, -8);
                final CuboidRegion region = new CuboidRegion(BukkitAdapter.asBlockVector(cornerOne), BukkitAdapter.asBlockVector(cornerTwo));
                region.setWorld(BukkitAdapter.adapt(cornerOne.getWorld()));
                final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                clipboard.setOrigin(BukkitAdapter.asBlockVector(cornerOne));
                blockLocations.forEach(blockLocation -> {
                    final Material material = (Material) NumberUtilities.chanceOf(new HashMap<Object, Double>(blockToPercentages));
                    try {
                        clipboard.setBlock(BukkitAdapter.asBlockVector(blockLocation), BukkitAdapter.adapt(material.createBlockData()));
                    } catch (final WorldEditException worldEditException) {
                        //Does nothing! :>
                    }
                });
                clipboards.add(clipboard);
            });
            return clipboards;
        }).thenApplyAsync(clipboards -> {
            final ConstructorTask constructorTask = new ConstructorTask(this.plugin, clipboards);
            constructorTask.start();
            while (constructorTask.isRunning());
            return true;
        });
    }

    /**
     * Allows you to retrieve the region's block composition.
     * Uses WorldEdit's API.
     *
     * @return The region's block composition.
     */
    public HashMap<Material, Integer> getBlockComposition() {
        final HashMap<Material, Integer> blockComposition = new HashMap<Material, Integer>();
        try (final EditSession editSession = WorldEdit.getInstance().newEditSession(this.region.getWorld())) {
            editSession.getBlockDistribution(this.region, false).forEach(blockState -> {
                final Material material = BukkitAdapter.adapt(blockState.getID().getBlockType());
                int count = blockComposition.getOrDefault(material, 0);
                count++;
                blockComposition.put(material, count);
            });
        }
        return blockComposition;
    }

    /**
     * Allows you to retrieve the region's block count.
     * Uses WorldEdit's API.
     *
     *
     * @return The region's block count.
     */
    public int getBlockCount() {
        return (int) this.region.getVolume();
    }

    /**
     * Allows you to check if a location is within the region.
     * Uses WorldEdit's API.
     *
     * @return If the location is in the region.
     */
    public boolean isWithin(final Location location) {
        return this.region.contains(BukkitAdapter.asBlockVector(location));
    }

}
