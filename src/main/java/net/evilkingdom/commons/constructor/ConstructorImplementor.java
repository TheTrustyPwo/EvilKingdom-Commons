package net.evilkingdom.commons.constructor;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.constructor.implementations.ConstructorRunnable;
import net.evilkingdom.commons.constructor.implementations.ConstructorTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ConstructorImplementor {

    private final JavaPlugin plugin;

    private final ArrayList<ConstructorTask> constructorTasks;

    private static final Set<ConstructorImplementor> cache = new HashSet<ConstructorImplementor>();

    /**
     * Allows you to create a ConstructorImplementor.
     *
     * @param plugin ~ The plugin to create the ConstructorImplementor for.
     */
    public ConstructorImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        new ConstructorRunnable(this.plugin);
        this.constructorTasks = new ArrayList<ConstructorTask>();

        cache.add(this);
    }

    /**
     * Allows you to retrieve the ConstructorImplementor's plugin.
     *
     * @return The ConstructorImplementor's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the ConstructorImplementor's tasks.
     * This should not be used inside your plugin whatsoever!
     *
     * @return The ConstructorImplementor's tasks.
     */
    public ArrayList<ConstructorTask> getTasks() {
        return this.constructorTasks;
    }

    /**
     * Allows you to retrieve the ConstructorImplementor for a plugin.
     * It will either pull it out of the cache or look for one if it doesn't exist.
     *
     * @param plugin ~ The plugin the ConstructorImplementor is for.
     * @return The self class.
     */
    public static ConstructorImplementor get(final JavaPlugin plugin) {
        final Optional<ConstructorImplementor> optionalConstructorImplementor = cache.stream().filter(constructorImplementor -> constructorImplementor.getPlugin() == plugin).findFirst();
        return optionalConstructorImplementor.orElseGet(() -> new ConstructorImplementor(plugin));
    }

}
