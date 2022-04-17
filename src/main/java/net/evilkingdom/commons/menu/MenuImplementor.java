package net.evilkingdom.commons.menu;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.command.CommandImplementor;
import net.evilkingdom.commons.command.implementations.CommandConverter;
import net.evilkingdom.commons.command.objects.Command;
import net.evilkingdom.commons.menu.objects.Menu;
import net.evilkingdom.commons.menu.implementations.MenuListener;
import net.evilkingdom.commons.menu.implementations.MenuRunnable;
import net.evilkingdom.commons.scoreboard.implementations.ScoreboardRunnable;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MenuImplementor {

    private final JavaPlugin plugin;

    private ArrayList<Menu> menus;

    private static final Set<MenuImplementor> cache = new HashSet<MenuImplementor>();

    /**
     * Allows you to create a MenuImplementor.
     *
     * @param plugin ~ The plugin to create the MenuImplementor for.
     */
    public MenuImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        this.menus = new ArrayList<Menu>();
        new MenuRunnable(this.plugin);
        new MenuListener(this.plugin);

        cache.add(this);
    }

    /**
     * Allows you to retrieve the MenuImplementor's menus.
     *
     * @return The MenuImplementor's menus.
     */
    public ArrayList<Menu> getMenus() {
        return this.menus;
    }

    /**
     * Allows you to retrieve the MenuImplementor's plugin.
     *
     * @return The MenuImplementor's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the MenuImplementor for a plugin.
     * It will either pull it out of the cache or look for one if it doesn't exist.
     *
     * @param plugin ~ The plugin the MenuImplementor is for.
     * @return The self class.
     */
    public static MenuImplementor get(final JavaPlugin plugin) {
        final Optional<MenuImplementor> optionalMenuImplementor = cache.stream().filter(menuImplementor -> menuImplementor.getPlugin() == plugin).findFirst();
        return optionalMenuImplementor.orElseGet(() -> new MenuImplementor(plugin));
    }

}
