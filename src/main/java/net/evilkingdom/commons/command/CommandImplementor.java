package net.evilkingdom.commons.command;

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

import net.evilkingdom.commons.command.objects.Command;
import net.evilkingdom.commons.command.implementations.CommandConverter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CommandImplementor {

    private final JavaPlugin plugin;

    private ArrayList<Command> commands;

    private static final Set<CommandImplementor> cache = new HashSet<CommandImplementor>();

    /**
     * Allows you to create a CommandImplementor.
     *
     * @param plugin ~ The plugin to create the CommandImplementor for.
     */
    public CommandImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        this.commands = new ArrayList<Command>();

        cache.add(this);
    }

    /**
     * Allows you to register a command.
     * This converts the command to bukkit format and adds it to the list.
     *
     * @param command ~ The command to register.
     */
    public void register(final Command command) {
        final org.bukkit.command.Command convertedCommand = new CommandConverter(command);
        ((CraftServer) Bukkit.getServer()).getCommandMap().register(this.plugin.getName(), convertedCommand);
        this.commands.add(command);
    }

    /**
     * Allows you to retrieve the CommandImplementor's commands.
     * This should not be used for registering commands, rather just for listing them.
     *
     * @return The CommandImplementor's commands.
     */
    public ArrayList<Command> getCommands() {
        return this.commands;
    }

    /**
     * Allows you to retrieve the CommandImplementor's plugin.
     *
     * @return The CommandImplementor's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the CommandImplementor for a plugin.
     * It will either pull it out of the cache or look for one if it doesn't exist.
     *
     * @param plugin ~ The plugin the CommandImplementor is for.
     * @return The self class.
     */
    public static CommandImplementor get(final JavaPlugin plugin) {
        final Optional<CommandImplementor> optionalCommandImplementor = cache.stream().filter(commandImplementor -> commandImplementor.getPlugin() == plugin).findFirst();
        return optionalCommandImplementor.orElseGet(() -> new CommandImplementor(plugin));
    }

}
