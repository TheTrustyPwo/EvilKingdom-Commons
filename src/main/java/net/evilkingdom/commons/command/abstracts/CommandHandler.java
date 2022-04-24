package net.evilkingdom.commons.command.abstracts;

/*
 * Made with love by https://kodirati.com/.
 */

import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public abstract class CommandHandler {

    /**
     * The execution of the command.
     * Just uses the bukkit arguments since bukkit handles the magic.
     */
    public boolean onExecution(final CommandSender commandSender, final String[] arguments) {
        return false;
    }

    /**
     * The tab completion of the command.
     * Just uses the bukkit arguments since bukkit handles the magic and the converter filters the options returned.
     */
    public ArrayList<String> onTabCompletion(final CommandSender commandSender, final String[] arguments) {
        return new ArrayList<String>();
    }

}
