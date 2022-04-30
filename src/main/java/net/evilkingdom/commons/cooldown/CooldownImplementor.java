package net.evilkingdom.commons.cooldown;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.cooldown.implementations.CooldownRunnable;
import net.evilkingdom.commons.cooldown.objects.Cooldown;
import net.evilkingdom.commons.datapoint.objects.Datasite;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Optional;

public class CooldownImplementor {

    private final JavaPlugin plugin;

    private final HashSet<Cooldown> cooldowns;

    private static final HashSet<CooldownImplementor> cache = new HashSet<CooldownImplementor>();

    /**
     * Allows you to create a CooldownImplementor.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin to create the DataImplementor for.
     */
    public CooldownImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        new CooldownRunnable(this.plugin);
        this.cooldowns = new HashSet<Cooldown>();

        cache.add(this);
    }

    /**
     * Allows you to retrieve the implementor's plugin.
     *
     * @return The implementor's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the implementor's cooldowns.
     *
     * @return The implementor's cooldowns.
     */
    public HashSet<Cooldown> getCooldowns() {
        return this.cooldowns;
    }

    /**
     * Allows you to retrieve the CooldownImplementor for a plugin.
     * It will either pull it out of the cache or look for one if it doesn't exist.
     *
     * @param plugin ~ The plugin the CooldownImplementor is for.
     * @return The self class.
     */
    public static CooldownImplementor get(final JavaPlugin plugin) {
        final Optional<CooldownImplementor> optionalCooldownImplementor = cache.stream().filter(cooldownImplementor -> cooldownImplementor.getPlugin() == plugin).findFirst();
        return optionalCooldownImplementor.orElseGet(() -> new CooldownImplementor(plugin));
    }

}
