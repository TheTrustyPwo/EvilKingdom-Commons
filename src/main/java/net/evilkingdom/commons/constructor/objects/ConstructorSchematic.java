package net.evilkingdom.commons.constructor.objects;

/*
 * Made with love by https://kodirati.com/.
 */

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
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transforms;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.session.ClipboardHolder;
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
import org.bukkit.util.BlockVector;

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
     * Allows you to retrieve the schematic's plugin.
     *
     * @return The schematic's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to save the schematic to a file.
     * Uses FastAsyncWorldEdit's API.
     *
     * @param file ~ The file of the schematic will save to.
     * @return If the save was successful.
     */
    public boolean save(final File file) {
        if (file.exists()) {
            file.delete();
        }
        try (final ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(new FileOutputStream(file))) {
            writer.write(this.clipboard);
        } catch (final IOException ioException) {
            return false;
        }
        return true;
    }

    /**
     * Allows you to load the schematic from a file.
     * Uses FastAsyncWorldEdit's API.
     *
     * @param file ~ The file to load from.
     * @return If the load was successful.
     */
    public boolean load(final File file) {
        final ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        if (clipboardFormat == null) {
            return false;
        }
        try (final ClipboardReader reader = clipboardFormat.getReader(new FileInputStream(file))) {
            final Clipboard clipboard = reader.read();
            clipboard.getRegion().setWorld(BukkitAdapter.adapt(this.center.getWorld()));
            this.clipboard = clipboard;
        } catch (final IOException ioException) {
            return false;
        }
        return true;
    }

    /**
     * Allows you to load the schematic from a region.
     * Uses FastAsyncWorldEdit's API.
     *
     * @param region ~ The region to load from.
     * @return If the load was successful.
     */
    public boolean load(final ConstructorRegion region) {
        final CuboidRegion cuboidRegion = new CuboidRegion(BukkitAdapter.asBlockVector(region.getCornerOne()), BukkitAdapter.asBlockVector(region.getCornerTwo()));
        cuboidRegion.setWorld(BukkitAdapter.adapt(this.center.getWorld()));
        final BlockArrayClipboard clipboard = new BlockArrayClipboard(cuboidRegion);
        final ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(cuboidRegion.getWorld(), cuboidRegion, clipboard, BukkitAdapter.asBlockVector(this.center));
        forwardExtentCopy.setCopyingEntities(trsue);
        forwardExtentCopy.setCopyingBiomes(false);
        try {
            Operations.complete(forwardExtentCopy);
            clipboard.setOrigin(BukkitAdapter.asBlockVector(this.center));
            this.clipboard = clipboard;
        }  catch (final WorldEditException worldEditException) {
            return false;
        }
        return true;
    }

    /**
     * Allows you to paste the schematic.
     * Uses FastAsyncWorldEdit's API.
     *
     * @return True when the task is complete or false if something goes wrong.
     */
    public boolean paste() {
        try (final EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(this.clipboard.getRegion().getWorld()).build()) {
            Operations.complete(new ClipboardHolder(this.clipboard).createPaste(editSession.getBypassAll()).to(BukkitAdapter.asBlockVector(this.center)).copyBiomes(false).ignoreAirBlocks(true).copyEntities(true).build());
        } catch (final WorldEditException worldEditException) {
            return false;
        }
        return true;
    }

}
