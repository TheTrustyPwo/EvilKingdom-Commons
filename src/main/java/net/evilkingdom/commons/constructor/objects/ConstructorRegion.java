package net.evilkingdom.commons.constructor.objects;

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

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.evilkingdom.commons.constructor.implementations.ConstructorTask;
import net.evilkingdom.commons.utilities.number.NumberUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
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
     * Load balanced and chunk based using WorldEdit's API.
     *
     * @param material ~ The material to set.
     * @return True when the task is complete.
     */
    public CompletableFuture<Boolean> fill(final Material material) {
        return CompletableFuture.supplyAsync(() -> {
            final HashMap<Location, ArrayList<Location>> chunkBlockLocations = new HashMap<Location, ArrayList<Location>>();
            this.region.forEach(blockBlockVector3 -> {
                final Location blockLocation = BukkitAdapter.adapt(BukkitAdapter.adapt(this.region.getWorld()), blockBlockVector3);
                final Location chunkLocation = new Location(blockLocation.getWorld(), blockLocation.getChunk().getX(), 69, blockLocation.getChunk().getZ());
                final ArrayList<Location> blockLocations = chunkBlockLocations.getOrDefault(chunkLocation, new ArrayList<Location>());
                blockLocations.add(blockLocation);
                chunkBlockLocations.put(chunkLocation, blockLocations);
            });
            return chunkBlockLocations;
        }).thenApply(chunkBlockLocations -> {
            final ArrayList<Clipboard> clipboards = new ArrayList<Clipboard>();
            chunkBlockLocations.forEach((chunkLocation, blockLocations) -> {
                final Location cornerOne = new Location(chunkLocation.getWorld(), (chunkLocation.getBlockX() << 4), chunkLocation.getWorld().getMinHeight(), (chunkLocation.getBlockY() << 4));
                final Location cornerTwo = new Location(chunkLocation.getWorld(), ((chunkLocation.getBlockX() << 4) + 15), chunkLocation.getWorld().getMinHeight(), (chunkLocation.getBlockY() << 4));
                final CuboidRegion region = new CuboidRegion(BukkitAdapter.asBlockVector(cornerOne), BukkitAdapter.asBlockVector(cornerTwo));
                region.setWorld(BukkitAdapter.adapt(cornerOne.getWorld()));
                final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                clipboard.setOrigin(BukkitAdapter.asBlockVector(chunkLocation));
                blockLocations.forEach(blockLocation -> {
                    try {
                        clipboard.setBlock(BukkitAdapter.asBlockVector(blockLocation), BukkitAdapter.asBlockType(material).getDefaultState());
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
            final HashMap<Location, ArrayList<Location>> chunkBlockLocations = new HashMap<Location, ArrayList<Location>>();
            this.region.forEach(blockBlockVector3 -> {
                final Location blockLocation = BukkitAdapter.adapt(BukkitAdapter.adapt(this.region.getWorld()), blockBlockVector3);
                final Location chunkLocation = new Location(blockLocation.getWorld(), blockLocation.getChunk().getX(), 69, blockLocation.getChunk().getZ());
                final ArrayList<Location> blockLocations = chunkBlockLocations.getOrDefault(chunkLocation, new ArrayList<Location>());
                blockLocations.add(blockLocation);
                chunkBlockLocations.put(chunkLocation, blockLocations);
            });
            return chunkBlockLocations;
        }).thenApply(chunkBlockLocations -> {
            final ArrayList<Clipboard> clipboards = new ArrayList<Clipboard>();
            chunkBlockLocations.forEach((chunkLocation, blockLocations) -> {
                final Location cornerOne = new Location(chunkLocation.getWorld(), (chunkLocation.getBlockX() << 4), chunkLocation.getWorld().getMinHeight(), (chunkLocation.getBlockY() << 4));
                final Location cornerTwo = new Location(chunkLocation.getWorld(), ((chunkLocation.getBlockX() << 4) + 15), chunkLocation.getWorld().getMinHeight(), (chunkLocation.getBlockY() << 4));
                final CuboidRegion region = new CuboidRegion(BukkitAdapter.asBlockVector(cornerOne), BukkitAdapter.asBlockVector(cornerTwo));
                region.setWorld(BukkitAdapter.adapt(cornerOne.getWorld()));
                final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                clipboard.setOrigin(BukkitAdapter.asBlockVector(chunkLocation));
                Optional<Material> optionalMaterial = Optional.empty();
                while (optionalMaterial.isEmpty()) {
                    optionalMaterial = blockToPercentages.keySet().stream().filter(block -> NumberUtilities.chanceOf(blockToPercentages.get(block))).findFirst();
                }
                final Material material = optionalMaterial.get();
                blockLocations.forEach(blockLocation -> {
                    try {
                        clipboard.setBlock(BukkitAdapter.asBlockVector(blockLocation), BukkitAdapter.asBlockType(material).getDefaultState());
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
     * Allows you to retrieve the region's count.
     * Uses WorldEdit's API.
     *
     *
     * @return The region's block count if the task is successful- returns -1 if it fails.
     */
    public int getBlockCount() {
        int blockCount;
        try (final EditSession editSession = WorldEdit.getInstance().newEditSession(this.region.getWorld())) {
            blockCount = editSession.getBlockDistribution(this.region, false).size();
        }
        return blockCount;
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
