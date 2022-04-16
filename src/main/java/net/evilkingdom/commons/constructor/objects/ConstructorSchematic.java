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

import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.evilkingdom.commons.constructor.implementations.ConstructorTask;
import net.evilkingdom.commons.utilities.number.NumberUtilities;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.Tag;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_18_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Zombie;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sound.sampled.Clip;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ConstructorSchematic {

    private final JavaPlugin plugin;

    private Location center;
    private Clipboard clipboard;

    /**
     * Allows you to create a schematic for a plugin.
     *
     * @param plugin ~ The plugin the schematic is for.
     */
    public ConstructorSchematic(final JavaPlugin plugin, final Location center) {
        this.plugin = plugin;

        this.center = center;
    }

    /**
     * Allows you to set the schematic's center.
     *
     * @param center ~ The center to set.
     */
    public void setCenter(final Location center) {
        this.center = center;
    }

    /**
     * Allows you to retrieve the schematic's center.
     *
     * @return The schematic's center.
     */
    public Location getCenter() {
        return this.center;
    }

    /**
     * Allows you to retrieve the schematic's region.
     *
     * @return The schematic's region.
     */
    public ConstructorRegion getRegion() {
        if (this.clipboard == null) {
            return null;
        }
        return new ConstructorRegion(this.plugin, BukkitAdapter.adapt(BukkitAdapter.adapt(this.clipboard.getRegion().getWorld()), this.clipboard.getRegion().getMinimumPoint()), BukkitAdapter.adapt(BukkitAdapter.adapt(this.clipboard.getRegion().getWorld()), this.clipboard.getRegion().getMaximumPoint()));
    }

    /**
     * Allows you to retrieve the schematic's plugin.
     *
     * @return The schematic's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to save the schematic to a file.
     * Uses WorldEdit's API.
     *
     * @param file ~ The file of the schematic will save to.
     * @return If the load was successful when the task is complete.
     */
    public CompletableFuture<Boolean> save(final File file) {
        return CompletableFuture.supplyAsync(() -> {
            try (final ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
                writer.write(clipboard);
            } catch (final IOException ioException) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to load the schematic from a file.
     * Uses WorldEdit's API.
     *
     * @param file ~ The file to load from.
     * @return If the load was successful when the task is complete.
     */
    public CompletableFuture<Boolean> load(final File file) {
        return CompletableFuture.supplyAsync(() -> {
            Clipboard clipboard = null;
            final ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = clipboardFormat.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
            } catch (final IOException ioException) {
                return false;
            }
            clipboard.setOrigin(BukkitAdapter.asBlockVector(this.center));
            clipboard.getRegion().setWorld(BukkitAdapter.adapt(this.center.getWorld()));
            this.clipboard = clipboard;
            return true;
        });
    }

    /**
     * Allows you to load the schematic from a region.
     * Uses WorldEdit's API.
     *
     * @param region ~ The region to load from.
     * @return If the load was successful when the task is complete.
     */
    public CompletableFuture<Boolean> load(final ConstructorRegion region) {
        return CompletableFuture.supplyAsync(() -> {
            final CuboidRegion cuboidRegion = new CuboidRegion(BukkitAdapter.asBlockVector(region.getCornerOne()), BukkitAdapter.asBlockVector(region.getCornerTwo()));
            cuboidRegion.setWorld(BukkitAdapter.adapt(region.getCornerOne().getWorld()));
            final BlockArrayClipboard clipboard = new BlockArrayClipboard(cuboidRegion);
            try (final EditSession editSession = WorldEdit.getInstance().newEditSession(cuboidRegion.getWorld())) {
                final Operation operation = new ForwardExtentCopy(cuboidRegion.getWorld(), clipboard.getRegion(), clipboard, BukkitAdapter.asBlockVector(this.center));
                Operations.complete(operation);
            }  catch (final WorldEditException worldEditException) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to paste the schematic.
     * Load balanced and chunk based using WorldEdit's API.
     *
     * @return True when the task is complete- or if the schematic is empty, it will return false (as it has nothing to paste).
     */
    public CompletableFuture<Boolean> paste() {
        return CompletableFuture.supplyAsync(() -> {
            final ArrayList<Location> chunkLocations = new ArrayList<Location>();
            this.clipboard.getRegion().forEach(blockBlockVector3 -> {
                final Location blockLocation = BukkitAdapter.adapt(BukkitAdapter.adapt(this.clipboard.getRegion().getWorld()), blockBlockVector3);
                final Location chunkLocation = new Location(blockLocation.getWorld(), blockLocation.getChunk().getX(), 69, blockLocation.getChunk().getZ());
                if (!chunkLocations.contains(chunkLocation)) {
                    chunkLocations.add(chunkLocation);
                }
            });
            return chunkLocations;
        }).thenApply(chunkLocations -> {
            final ArrayList<Clipboard> clipboards = new ArrayList<Clipboard>();
            chunkLocations.forEach(chunkLocation -> {
                final Location cornerOne = new Location(chunkLocation.getWorld(), (chunkLocation.getBlockX() << 4), chunkLocation.getWorld().getMinHeight(), (chunkLocation.getBlockY() << 4));
                final Location cornerTwo = new Location(chunkLocation.getWorld(), ((chunkLocation.getBlockX() << 4) + 15), chunkLocation.getWorld().getMinHeight(), (chunkLocation.getBlockY() << 4));
                final CuboidRegion region = new CuboidRegion(BukkitAdapter.asBlockVector(cornerOne), BukkitAdapter.asBlockVector(cornerTwo));
                region.setWorld(BukkitAdapter.adapt(cornerOne.getWorld()));
                final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                clipboard.setOrigin(BukkitAdapter.asBlockVector(chunkLocation));
                this.clipboard.getEntities(region).forEach(entity -> clipboard.createEntity(entity.getLocation(), entity.getState()));
                region.forEach(blockBlockVector3 -> {
                    try {
                        clipboard.setBlock(blockBlockVector3, this.clipboard.getFullBlock(blockBlockVector3));
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

}
