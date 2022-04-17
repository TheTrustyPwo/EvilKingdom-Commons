package net.evilkingdom.commons.constructor.implementations;

/*
 * Made with love by https://kodirati.com/.
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
        final ConstructorImplementor constructorImplementor = ConstructorImplementor.get(this.plugin);
        return constructorImplementor.getTasks().contains(this);
    }

    /**
     * Allows you to start the task.
     */
    public void start() {
        final ConstructorImplementor constructorImplementor = ConstructorImplementor.get(this.plugin);
        constructorImplementor.getTasks().add(this);
    }

}
