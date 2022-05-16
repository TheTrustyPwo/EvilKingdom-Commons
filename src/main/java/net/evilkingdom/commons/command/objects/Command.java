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
    private final CommandHandler handler;
    private final ArrayList<String> aliases;

    /**
     * Allows you to create a command for a plugin.
     * This should be used for commands WITH aliases.
     *
     * @param plugin ~ The plugin the command is for.
     * @param name ~ The name of the command.
     * @param aliases ~ The aliases of the command.
     * @param handler ~ The handler of the command.
     */
    public Command(final JavaPlugin plugin, final String name, final ArrayList<String> aliases, final CommandHandler handler) {
        this.plugin = plugin;
        this.name = name;
        this.aliases = aliases;
        this.handler = handler;
    }

    /**
     * Allows you to create a command for a plugin.
     * This should be used for commands WITHOUT aliases.
     *
     * @param plugin ~ The plugin the command is for.
     * @param name ~ The name of the command.
     * @param handler ~ The handler of the command.
     */
    public Command(final JavaPlugin plugin, final String name, final CommandHandler handler) {
        this.plugin = plugin;
        this.name = name;
        this.aliases = new ArrayList<String>();
        this.handler = handler;
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
        final CommandImplementor implementor = CommandImplementor.get(this.plugin);
        implementor.register(this);
    }

}
