package net.evilkingdom.commons.command;

/*
 * Made with love by https://kodirati.com/.
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
