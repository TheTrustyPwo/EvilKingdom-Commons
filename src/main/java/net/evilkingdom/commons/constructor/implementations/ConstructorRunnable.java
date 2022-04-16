package net.evilkingdom.commons.constructor.implementations;

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

        Bukkit.getScheduler().runTaskTimer(this.plugin, this, 0L, 1L);
    }

    /**
     * The runnable for the tasks.
     */
    @Override
    public void run() {
        final ConstructorImplementor constructorImplementor = ConstructorImplementor.get(this.plugin);
        for (Iterator<ConstructorTask> it = constructorImplementor.getTasks().iterator(); it.hasNext(); ) {
            final ConstructorTask task = it.next();
            while (System.nanoTime() <= 10000000) {
                if (task.getClipboardsLeft().isEmpty()) {
                    task.stop();
                    break;
                }
                for (Iterator<Clipboard> clipboardIterator = task.getClipboardsLeft().iterator(); clipboardIterator.hasNext();) {
                    final Clipboard clipboard = clipboardIterator.next();
                    clipboardIterator.remove();
                    try (final EditSession editSession = WorldEdit.getInstance().newEditSession(clipboard.getRegion().getWorld())) {
                        final Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(clipboard.getOrigin()).build();
                        Operations.completeBlindly(operation);
                    }
                }
            }
        };
    }

}
