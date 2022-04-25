package net.evilkingdom.commons.cooldown.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.cooldown.CooldownImplementor;
import net.evilkingdom.commons.cooldown.objects.Cooldown;
import net.evilkingdom.commons.menu.MenuImplementor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public class CooldownRunnable implements Runnable {

    private final JavaPlugin plugin;

    /**
     * Allows you to create a CooldownRunnable for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin the CooldownRunnable is for.
     */
    public CooldownRunnable(final JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this, 0L, 1L);
    }

    /**
     * The runnable for the cooldowns.
     */
    @Override
    public void run() {
        final CooldownImplementor cooldownImplementor = CooldownImplementor.get(this.plugin);
        for (final Iterator<Cooldown> cooldownIterator = cooldownImplementor.getCooldowns().iterator(); cooldownIterator.hasNext();) {
            final Cooldown cooldown = cooldownIterator.next();
            if (cooldown.getTimeLeft() <= 0) {
                cooldownIterator.remove();
            }
            cooldown.setTimeLeft(cooldown.getTimeLeft() - 1L);
        }
    }

}
