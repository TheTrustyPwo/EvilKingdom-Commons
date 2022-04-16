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

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import net.evilkingdom.commons.constructor.ConstructorImplementor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class ConstructorTask {

    private final JavaPlugin plugin;

    private boolean running;
    private ArrayList<Clipboard> clipboardsLeft;

    /**
     * Allows you to create a ConstructorTask for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin the task is for.
     * @param clipboards ~ The clipboards.
     */
    public ConstructorTask(final JavaPlugin plugin, final ArrayList<Clipboard> clipboards) {
        this.plugin = plugin;

        this.clipboardsLeft = clipboards;
        this.running = false;
    }

    /**
     * Allows you to retrieve the task's clipboards left.
     *
     * @return The task's clipboards left.
     */
    public ArrayList<Clipboard> getClipboardsLeft() {
        return this.clipboardsLeft;
    }

    /**
     * Allows you to retrieve the if ConstructorTask is running.
     *
     * @return If the ConstructorTask is running.
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Allows you to start the task.
     */
    public void start() {
        final ConstructorImplementor constructorImplementor = ConstructorImplementor.get(this.plugin);
        constructorImplementor.getTasks().add(this);
        this.running = true;
    }

    /**
     * Allows you to stop the task.
     */
    public void stop() {
        final ConstructorImplementor constructorImplementor = ConstructorImplementor.get(this.plugin);
        constructorImplementor.getTasks().remove(this);
        this.running = false;
    }

}
