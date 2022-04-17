package net.evilkingdom.commons.command.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.command.CommandImplementor;
import net.evilkingdom.commons.command.abstracts.CommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Command {

    private final JavaPlugin plugin;

    private final String name;
    private CommandHandler handler;
    private ArrayList<String> aliases;

    /**
     * Allows you to create a command.
     *
     * @param plugin ~ The plugin the command is for.
     * @param name ~ The name of the command to create.
     */
    public Command(final JavaPlugin plugin, final String name) {
        this.plugin = plugin;
        this.name = name;
        this.aliases = new ArrayList<String>();
    }

    /**
     * Allows you to retrieve the command's name.
     *
     * @return ~ The command's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Allows you to retrieve the command's aliases.
     *
     * @return ~ The command's aliases.
     */
    public ArrayList<String> getAliases() {
        return this.aliases;
    }

    /**
     * Allows you to set the command's aliases.
     *
     * @param aliases ~ The command's aliases that will be set.
     */
    public void setAliases(final ArrayList<String> aliases) {
        this.aliases = aliases;
    }

    /**
     * Allows you to set the command's handler.
     *
     * @param handler ~ The command's handler that will be set.
     */
    public void setHandler(final CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Allows you to retrieve the command's handler.
     *
     * @return ~ The command's handler.
     */
    public CommandHandler getHandler() {
        return this.handler;
    }

    /**
     * Allows you to register the command.
     */
    public void register() {
        final CommandImplementor commandImplementor = CommandImplementor.get(this.plugin);
        commandImplementor.register(this);
    }

}
