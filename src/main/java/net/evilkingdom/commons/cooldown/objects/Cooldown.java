package net.evilkingdom.commons.cooldown.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import net.evilkingdom.commons.cooldown.CooldownImplementor;
import net.evilkingdom.commons.datapoint.DataImplementor;
import net.evilkingdom.commons.datapoint.enums.DatasiteType;
import net.evilkingdom.commons.datapoint.objects.Datapoint;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class Cooldown {

    private final JavaPlugin plugin;

    private final String identifier;
    private long timeLeft;

    /**
     * Allows you to create a cooldown for a plugin.
     *
     * @param plugin ~ The plugin the cooldown is for.
     * @param identifier ~ The identifier of the cooldown.
     * @param duration ~ The duration the cooldown (in ticks).
     */
    public Cooldown(final JavaPlugin plugin, final String identifier, final long duration) {
        this.plugin = plugin;

        this.identifier = identifier;
        this.timeLeft = duration;
    }



    /**
     * Allows you to start the cooldown.
     */
    public void start() {
        final CooldownImplementor implementor = CooldownImplementor.get(this.plugin);
        implementor.getCooldowns().add(this);
    }

    /**
     * Allows you to stop the cooldown.
     */
    public void stop() {
        final CooldownImplementor implementor = CooldownImplementor.get(this.plugin);
        implementor.getCooldowns().remove(this);
    }

    /**
     * Allows you to retrieve the cooldown's plugin.
     *
     * @return The cooldown's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the cooldown's identifier.
     *
     * @return The cooldown's identifier.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Allows you to retrieve the cooldown's time left.
     *
     * @return The cooldown's time left.
     */
    public Long getTimeLeft() {
        return this.timeLeft;
    }

    /**
     * Allows you to set the cooldown's time left.
     *
     * @param timeLeft ~ The time left that will be set.
     */
    public void setTimeLeft(final long timeLeft) {
        this.timeLeft = timeLeft;
    }

}
