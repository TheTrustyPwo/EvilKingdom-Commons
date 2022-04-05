package net.evilkingdom.commons.utilities.fawe;

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
import com.sk89q.worldedit.EditSessionBuilder;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.EditSessionBlockChangeDelegate;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.EditContext;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.Countable;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class FAWEUtilities {

    /**
     * Allows you to retrieve the air to block percentage from two corners.
     * This should be used highly for mine based systems and checking how full they are.
     * Uses FastAsyncWorldEdit's API and runs asynchronously in order to keep the server from lagging.
     *
     * @return The air to block percentage from two corners when the FastAsyncWorldEdit task is done, if the FastAsyncWorldEdit task fails- it'll just return that it's 100% full.
     */
    public static CompletableFuture<Integer> getAirToBlockPercentage(final Location corner1, final Location corner2) {
        return CompletableFuture.supplyAsync(() -> {
            double air = 0;
            double total = 0;
            final World world = BukkitAdapter.adapt(corner1.getWorld());
            final Region region = new CuboidRegion(BlockVector3.at(corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()), BlockVector3.at(corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ()));
            try (final EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                for (final Countable<BlockType> blocks : editSession.getBlockDistribution(region)) {
                    if (blocks.getID() == BlockTypes.AIR) {
                        air += blocks.getAmount();
                    }
                    total += blocks.getAmount();
                }
            } catch (final Exception exception) {
                return 100;
            }
            return ((int) Math.round((((total - air) / total) * 100)));
        });
    }

    /**
     * Allows you to retrieve a FastAsyncWorldEdit Pattern from a HashMap of blocks to their percentages.
     * This should be used highly for mine based systems and filling them with specific blocks with specific chances.
     * Uses FastAsyncWorldEdit's API.
     *
     * @return The FastAsyncWorldEdit Pattern from a HashMap of blocks to their percentages.
     */
   public static Pattern getPattern(final HashMap<Material, Double> blockToPercentage) {
       RandomPattern randomPattern = new RandomPattern();
       for (final Material material : blockToPercentage.keySet()) {
           final BlockState blockState = BukkitAdapter.adapt(material.createBlockData());
           final double chance = blockToPercentage.get(material);
           randomPattern.add(blockState, chance);
       }
       return randomPattern;
   }

    /**
     * Allows you to fill from two corners and a FastAsyncWorldEdit pattern.
     * This should be used highly for mine based systems and resetting them.
     * Uses FastAsyncWorldEdit's API and runs asynchronously in order to keep the server from lagging.
     *
     * @return If the fill was successful when FastAsyncWorldEdit task is done, if the FastAsyncWorldEdit task fails- it'll just return that false.
     */
    public static CompletableFuture<Boolean> fill(final Location corner1, final Location corner2, final Pattern pattern) {;
        return CompletableFuture.supplyAsync(() -> {
            final World world = BukkitAdapter.adapt(corner1.getWorld());
            final Region region = new CuboidRegion(BlockVector3.at(corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()), BlockVector3.at(corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ()));
            try (final EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                editSession.setBlocks(region, pattern);
            } catch (final Exception exception) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to fill from two corners and a material.
     * This should be used highly for mine based systems and deleting them.
     * Uses FastAsyncWorldEdit's API and runs asynchronously in order to keep the server from lagging.
     *
     * @return If the fill was successful when FastAsyncWorldEdit task is done, if the FastAsyncWorldEdit task fails- it'll just return that false.
     */
    public static CompletableFuture<Boolean> fill(final Location corner1, final Location corner2, final Material material) {
        return CompletableFuture.supplyAsync(() -> {
            final World world = BukkitAdapter.adapt(corner1.getWorld());
            final Region region = new CuboidRegion(BlockVector3.at(corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()), BlockVector3.at(corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ()));
            final BlockState blockState = BukkitAdapter.adapt(material.createBlockData());
            try (final EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                editSession.setBlocks(region, blockState);
            } catch (final Exception exception) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to paste a schematic from a schematic file and a location.
     * This should be used highly for mine based systems and creating them.
     * Uses FastAsyncWorldEdit's API and runs asynchronously in order to keep the server from lagging.
     *
     * @return If the fill was successful when FastAsyncWorldEdit task is done, if the FastAsyncWorldEdit task fails- it'll just return that false.
     */
    public static CompletableFuture<Boolean> pasteSchematic(final File schematicFile, final Location center) {
        return CompletableFuture.supplyAsync(() -> {
            InputStream schematicInputStream;
            try {
                 schematicInputStream = new FileInputStream(schematicFile);
            } catch (final Exception exception) {
                return false;
            }
            Clipboard clipboard;
            try (final ClipboardReader reader = BuiltInClipboardFormat.FAST.getReader(schematicInputStream)) {
                clipboard = reader.read();
            } catch (final Exception exception) {
                return false;
            }
            final World world = BukkitAdapter.adapt(center.getWorld());
            final BlockVector3 blockVector3 = BlockVector3.at(center.getBlockX(), center.getBlockY(), center.getBlockZ());
            try (final EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(blockVector3)
                        .copyEntities(true)
                        .ignoreAirBlocks(true)
                        .build();
                Operations.complete(operation);
            } catch (final Exception exception) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to check if a location is in an area from two corners.
     * This should be used for zone or mine based systems and checking if players are within them.
     * Uses FastAsyncWorldEdit's API.
     *
     * @return If the location is within the area from two corners.
     */
    public static boolean isLocationWithin(final Location corner1, final Location corner2, final Location location) {
        final Region region = new CuboidRegion(BlockVector3.at(corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()), BlockVector3.at(corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ()));
        final BlockVector3 blockVector3 = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        return region.contains(blockVector3);
    }

}
