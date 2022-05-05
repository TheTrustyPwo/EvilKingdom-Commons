package net.evilkingdom.commons.command.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.command.objects.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandConverter extends org.bukkit.command.Command {

    private final Command command;

    /**
     * Allows you to convert a command to a bukkit command.
     * This should not be used inside your plugin whatsoever!
     *
     * @param command ~ The command to convert to a bukkit command.
     */
    public CommandConverter(final Command command) {
        super(command.getName());
        this.setName(command.getName());
        this.setLabel(command.getName());
        if (!command.getAliases().isEmpty()) {
            this.setAliases(command.getAliases());
        }
        this.command = command;
    }

    /**
     * The bukkit execution of the command.
     * All of the magic is handled by bukkit.
     */
    @Override
    public boolean execute(final CommandSender commandSender, final String commandLabel, final String[] arguments) {
        this.command.getHandler().onExecution(commandSender, arguments);
        return true;
    }

    /**
     * The bukkit tab completion of the command.
     * The converter filters the returned options and bukkit handles all of the magic.
     */
    @Override
    public @NotNull List<String> tabComplete(final CommandSender commandSender, final String commandLabel, final String[] arguments) {
        final ArrayList<String> availableTabArguments = this.command.getHandler().onTabCompletion(commandSender, arguments);
        final String tabArgument = arguments.length > 0 ? arguments[(arguments.length - 1)] : "";
        return availableTabArguments.stream().filter(availableTabArgument -> (tabArgument.isEmpty() || availableTabArgument.startsWith(tabArgument))).collect(Collectors.toList());
    }
}
