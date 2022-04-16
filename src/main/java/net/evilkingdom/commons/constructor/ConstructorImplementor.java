package net.evilkingdom.commons.constructor;

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

import net.evilkingdom.commons.constructor.implementations.ConstructorTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ConstructorImplementor {

    private final JavaPlugin plugin;

    private ArrayList<ConstructorTask> constructorTasks;

    private static final Set<ConstructorImplementor> cache = new HashSet<ConstructorImplementor>();

    /**
     * Allows you to create a ConstructorImplementor.
     *
     * @param plugin ~ The plugin to create the ConstructorImplementor for.
     */
    public ConstructorImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

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
