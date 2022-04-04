package net.evilkingdom.commons;

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

import net.evilkingdom.commons.utilities.string.StringUtilities;
import org.bukkit.Bukkit;

public class Commons {

    private static Commons plugin;

    /**
     * Bukkit's on enable method for plugins.
     * It just uses the method I created further down on the class since it's sexier.
     */
    public void onEnable() {
        this.initialize();
    }

    /**
     * Bukkit's on enable method for plugins.
     * It just uses the method I created further down on the class since it's sexier.
     */
    public void onDisable() {
        this.terminate();
    }

    /**
     * Allows you to initialize the plugin.
     */
    private void initialize() {
        Bukkit.getConsoleSender().sendMessage(StringUtilities.colorize("&2[Commons] &aInitializing..."));
        plugin = this;
        Bukkit.getConsoleSender().sendMessage(StringUtilities.colorize("&2[Commons] &aInitialized."));
    }

    /**
     * Allows you to terminate the plugin.
     */
    private void terminate() {
        Bukkit.getConsoleSender().sendMessage(StringUtilities.colorize("&4[Commons] &cTerminating..."));
        plugin = null;
        Bukkit.getConsoleSender().sendMessage(StringUtilities.colorize("&4[Commons] &cTerminated."));
    }

    /**
     * Allows you to retrieve the plugin.
     *
     * @return The plugin.
     */
    public static Commons getPlugin() {
        return plugin;
    }

}
