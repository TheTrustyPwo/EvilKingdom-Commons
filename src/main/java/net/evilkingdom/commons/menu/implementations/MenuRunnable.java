package net.evilkingdom.commons.menu.implementations;

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

import net.evilkingdom.commons.menu.MenuImplementor;
import net.evilkingdom.commons.menu.objects.Menu;
import net.evilkingdom.commons.menu.objects.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class MenuRunnable implements Runnable {

    private final JavaPlugin plugin;

    /**
     * Allows you to create a MenuRunnable for a plugin.
     * This is used in the MenuImplementor to register the menu system on plugins.
     *
     * @param plugin ~ The plugin the MenuRunnable is for.
     */
    public MenuRunnable(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Allows you to register the runnable.
     */
    public void register() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, this, 0L, 20L);
    }

    /**
     * The runnable for the menus.
     */
    @Override
    public void run() {
        final MenuImplementor menuImplementor = MenuImplementor.get(this.plugin);
        menuImplementor.getMenus().forEach(menu -> {
            menu.getRunnable().ifPresent(runnable -> runnable.run());
            menu.update();
        });
    }

}
