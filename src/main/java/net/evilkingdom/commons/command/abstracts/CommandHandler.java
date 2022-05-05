package net.evilkingdom.commons.command.abstracts;

/*
 * Made with love by https://kodirati.com/.
 */

import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public abstract class CommandHandler {

    /**
     * The execution of the command.
     *
     * @param sender ~ The command's sender.
     * @param arguments ~ The command's arguments.
     */
    public boolean onExecution(final CommandSender sender, final String[] arguments) {
        //It'll be overridden wherever it is used, therefore it is empty.
        return false;
    }

    /**
     * The tab completion of the command.
     *
     * @param sender ~ The command's sender.
     * @param arguments ~ The command's arguments.
     */
    public ArrayList<String> onTabCompletion(final CommandSender sender, final String[] arguments) {
        return new ArrayList<String>();
    }

}
