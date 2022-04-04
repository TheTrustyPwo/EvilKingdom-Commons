package net.evilkingdom.commons.menu;

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

import net.evilkingdom.commons.command.CommandImplementor;
import net.evilkingdom.commons.command.implementations.CommandConverter;
import net.evilkingdom.commons.command.objects.Command;
import net.evilkingdom.commons.menu.objects.Menu;
import net.evilkingdom.commons.menu.implementations.MenuListener;
import net.evilkingdom.commons.menu.implementations.MenuRunnable;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MenuImplementor {

    private final JavaPlugin plugin;

    private ArrayList<Menu> menus;

    private static final Set<MenuImplementor> cache = new HashSet<MenuImplementor>();

    /**
     * Allows you to create a MenuImplementor.
     *
     * @param plugin ~ The plugin to create the MenuImplementor for.
     */
    public MenuImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        this.menus = new ArrayList<Menu>();

        cache.add(this);
    }

    /**
     * Allows you to retrieve the MenuImplementor's menus.
     *
     * @return The MenuImplementor's menus.
     */
    public ArrayList<Menu> getMenus() {
        return this.menus;
    }

    /**
     * Allows you to retrieve the MenuImplementor's plugin.
     *
     * @return The MenuImplementor's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the MenuImplementor for a plugin.
     * It will either pull it out of the cache or look for one if it doesn't exist.
     *
     * @param plugin ~ The plugin the MenuImplementor is for.
     * @return The self class.
     */
    public static MenuImplementor get(final JavaPlugin plugin) {
        final Optional<MenuImplementor> optionalMenuImplementor = cache.stream().filter(menuImplementor -> menuImplementor.getPlugin() == plugin).findFirst();
        return optionalMenuImplementor.orElseGet(() -> new MenuImplementor(plugin));
    }

}
