package net.evilkingdom.commons.command.implementations;

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
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandConverter extends org.bukkit.command.Command {

    private final Command command;

    /**
     * Allows you to convert a command to a bukkit command.
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
        if (command.getDescription() != null) {
            this.setDescription(command.getDescription());
        }
        this.command = command;
    }

    /**
     * The bukkit execution of the command.
     * Bukkit handles the magic.
     */
    @Override
    public boolean execute(final CommandSender commandSender, final String commandLabel, final String[] arguments) {
        return this.command.getHandler().onExecution(commandSender, commandLabel, arguments);
    }

    /**
     * The bukkit tab completion of the command.
     * The converter filters the returned options and bukkit handles the magic.
     */
    @Override
    public @NotNull List<String> tabComplete(final CommandSender commandSender, final String commandLabel, final String[] arguments) {
        final ArrayList<String> availableTabArguments = this.command.getHandler().onTabCompletion(commandSender, arguments);
        final String tabArgument = arguments.length > 0 ? arguments[(arguments.length - 1)] : "";
        return availableTabArguments.stream().filter(availableTabArgument -> (tabArgument.isEmpty() || availableTabArgument.startsWith(tabArgument))).collect(Collectors.toList());
    }
}
