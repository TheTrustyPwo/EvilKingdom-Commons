package net.evilkingdom.commons.constructor.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.evilkingdom.commons.constructor.ConstructorImplementor;
import net.evilkingdom.commons.menu.MenuImplementor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;

public class ConstructorRunnable implements Runnable {

    private final JavaPlugin plugin;

    /**
     * Allows you to create a ConstructorRunnable for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin the ConstructorRunnable is for.
     */
    public ConstructorRunnable(final JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this, 0L, 1L);
    }

    /**
     * The runnable for the tasks.
     */
    @Override
    public void run() {
        final ConstructorImplementor constructorImplementor = ConstructorImplementor.get(this.plugin);
        for (final Iterator<ConstructorTask> taskIterator = constructorImplementor.getTasks().iterator(); taskIterator.hasNext(); ) {
            final ConstructorTask task = taskIterator.next();
            final long stopTime = System.nanoTime() + 75000000L;
            while (System.nanoTime() <= stopTime) {
                if (task.getClipboardsLeft().isEmpty()) {
                    taskIterator.remove();
                    break;
                }
                for (final Iterator<Clipboard> clipboardIterator = task.getClipboardsLeft().iterator(); clipboardIterator.hasNext();) {
                    final Clipboard clipboard = clipboardIterator.next();
                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        try (final EditSession editSession = WorldEdit.getInstance().newEditSession(clipboard.getRegion().getWorld())) {
                            final Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(clipboard.getOrigin()).copyEntities(true).ignoreAirBlocks(true).build();
                            Operations.completeBlindly(operation);
                        }
                    });
                    clipboardIterator.remove();
                }
            }
        };
    }

}
