package net.evilkingdom.commons.command.objects;

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

import net.evilkingdom.commons.command.CommandImplementor;
import net.evilkingdom.commons.command.abstracts.CommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Command {

    private final JavaPlugin plugin;

    private final String name;
    private String description;
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
     * Allows you to set the command's description.
     *
     * @param description ~ The command's description that will be set.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Allows you to retrieve the command's description.
     *
     * @return ~ The command's description.
     */
    public String getDescription() {
        return this.description;
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
