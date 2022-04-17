package net.evilkingdom.commons.constructor.objects;

/*
 * Made with love by https://kodirati.com/.
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
            final ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(cuboidRegion.getWorld(), clipboard.getRegion(), clipboard, BukkitAdapter.asBlockVector(this.center));
            forwardExtentCopy.setCopyingEntities(true);
            forwardExtentCopy.setCopyingBiomes(false);
            try {
                Operations.complete(forwardExtentCopy);
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
            final ArrayList<Long> chunkKeys = new ArrayList<Long>();
            this.clipboard.getRegion().forEach(blockBlockVector3 -> {
                final Location blockLocation = BukkitAdapter.adapt(BukkitAdapter.adapt(this.clipboard.getRegion().getWorld()), blockBlockVector3);
                if (!chunkKeys.contains(blockLocation.getChunk().getChunkKey())) {
                    chunkKeys.add(blockLocation.getChunk().getChunkKey());
                }
            });
            return chunkKeys;
        }).thenApply(chunkKeys -> {
            final ArrayList<Clipboard> clipboards = new ArrayList<Clipboard>();
            chunkKeys.forEach(chunkKey -> {
                final Chunk chunk = BukkitAdapter.adapt(this.clipboard.getRegion().getWorld()).getChunkAt(chunkKey);
                final Location chunkLocation = new Location(chunk.getWorld(), chunk.getX() << 4, 69, chunk.getZ() << 4).add(7, 0, 7);
                final Location cornerOne = new Location(chunkLocation.getWorld(), chunkLocation.getX(), chunkLocation.getWorld().getMaxHeight(), chunkLocation.getZ()).add(8, 0, 9);
                final Location cornerTwo = new Location(chunkLocation.getWorld(), chunkLocation.getX(), chunkLocation.getWorld().getMinHeight(), chunkLocation.getZ()).add(-7, 0, -8);
                final CuboidRegion region = new CuboidRegion(BukkitAdapter.asBlockVector(cornerOne), BukkitAdapter.asBlockVector(cornerTwo));
                region.setWorld(BukkitAdapter.adapt(cornerOne.getWorld()));
                final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                clipboard.setOrigin(BukkitAdapter.asBlockVector(cornerOne));
                this.clipboard.getEntities(region).forEach(entity -> clipboard.createEntity(entity.getLocation(), entity.getState()));
                region.forEach(blockBlockVector3 -> {
                    try {
                        clipboard.setBlock(blockBlockVector3, this.clipboard.getBlock(blockBlockVector3).toBaseBlock());
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
