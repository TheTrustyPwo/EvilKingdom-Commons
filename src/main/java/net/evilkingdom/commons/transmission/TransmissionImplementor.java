package net.evilkingdom.commons.transmission;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.scoreboard.implementations.ScoreboardListener;
import net.evilkingdom.commons.scoreboard.implementations.ScoreboardRunnable;
import net.evilkingdom.commons.scoreboard.objects.Scoreboard;
import net.evilkingdom.commons.transmission.objects.Transmission;
import net.evilkingdom.commons.transmission.objects.TransmissionSite;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Optional;

public class TransmissionImplementor {

    private final JavaPlugin plugin;

    private final HashSet<TransmissionSite> transmissionSites;

    private static final HashSet<TransmissionImplementor> cache = new HashSet<TransmissionImplementor>();

    /**
     * Allows you to create a TransmissionImplementor.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin to create the TransmissionImplementor for.
     */
    public TransmissionImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        this.transmissionSites = new HashSet<TransmissionSite>();

        cache.add(this);
    }

    /**
     * Allows you to retrieve the implementor's tasks.
     *
     * @return The implementor's tasks.
     */
    public HashSet<TransmissionSite> getTransmissionSites() {
        return this.transmissionSites;
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
     * Allows you to retrieve the TransmissionImplementor for a plugin.
     * It will either pull it out of the cache or look for one if it doesn't exist.
     *
     * @param plugin ~ The plugin the TransmissionImplementor is for.
     * @return The self class.
     */
    public static TransmissionImplementor get(final JavaPlugin plugin) {
        final Optional<TransmissionImplementor> optionalTransmissionImplementor = cache.stream().filter(transmissionImplementor -> transmissionImplementor.getPlugin() == plugin).findFirst();
        return optionalTransmissionImplementor.orElseGet(() -> new TransmissionImplementor(plugin));
    }

}
