package net.evilkingdom.commons.datapoint.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.mongodb.ClientSessionOptions;
import com.mongodb.MongoClientException;
import com.mongodb.TransactionOptions;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import net.evilkingdom.commons.datapoint.DataImplementor;
import net.evilkingdom.commons.datapoint.enums.DatasiteType;
import net.evilkingdom.commons.utilities.string.StringUtilities;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Datasite {

    private final JavaPlugin plugin;

    private final DatasiteType type;
    private final String[] parameters;
    private MongoClient mongoClient;
    private final ArrayList<Datapoint> datapoints;

    /**
     * Allows you to create a datasite for a plugin.
     * This is used for datasites that don't require any extra parameters.
     *
     * @param plugin ~ The plugin the datasite is for.
     * @param type ~ The type of datasite.
     */
    public Datasite(final JavaPlugin plugin, final DatasiteType type) {
        this.plugin = plugin;

        this.type = type;
        this.parameters = new String[]{};
        this.datapoints = new ArrayList<Datapoint>();
        final DataImplementor dataImplementor = DataImplementor.get(this.plugin);
        dataImplementor.getDatasites().add(this);
    }

    /**
     * Allows you to create a datasite for a plugin.
     * This is used for datasites that requires any extra parameters.
     *
     * @param plugin ~ The plugin the datasite is for.
     * @param type ~ The type of datasite.
     * @param parameters ~ Any extra parameters the datasite will need.
     */
    public Datasite(final JavaPlugin plugin, final DatasiteType type, final String[] parameters) {
        this.plugin = plugin;

        this.type = type;
        this.parameters = parameters;
        this.datapoints = new ArrayList<Datapoint>();
        final DataImplementor dataImplementor = DataImplementor.get(this.plugin);
        dataImplementor.getDatasites().add(this);
    }

    /**
     * Allows you to initialize the datasite.
     *
     * @throws Exception ~ If anything fails.
     */
    public void initialize() throws Exception {
        switch (this.type) {
            case MONGO_DATABASE -> {
                this.mongoClient = MongoClients.create(parameters[0]);
                final MongoDatabase mongoDatabase = this.mongoClient.getDatabase(this.plugin.getName());
                if (!this.mongoClient.listDatabaseNames().into(new ArrayList<String>()).contains(mongoDatabase.getName())) {
                    this.datapoints.forEach(datapoint -> {
                        if (!mongoDatabase.listCollectionNames().into(new ArrayList<String>()).contains(datapoint.getName())) {
                            mongoDatabase.createCollection(datapoint.getName());
                        }
                    });
                }
            }
            case JSON -> {
                final File dataFolder = new File(this.plugin.getDataFolder(), "data");
                if (!dataFolder.exists()) {
                    dataFolder.mkdirs();
                }
                this.datapoints.forEach(datapoint -> {
                    final File datapointFolder = new File(dataFolder, datapoint.getName());
                    if (!datapointFolder.exists()) {
                        datapointFolder.mkdirs();
                    }
                });
            }
        }
    }

    /**
     * Allows you to terminate the datasite.
     */
    public void terminate() {
        switch (this.type) {
            case MONGO_DATABASE -> this.mongoClient.close();
        }
    }

    /**
     * Allows you to retrieve the datasite's plugin.
     *
     * @return The datasite's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the datasite's type.
     *
     * @return The datasite's type.
     */
    public DatasiteType getType() {
        return this.type;
    }

    /**
     * Allows you to retrieve the datasite's datapoints.
     *
     * @return The datasite's datapoints.
     */
    public ArrayList<Datapoint> getDatapoints() {
        return this.datapoints;
    }

    /**
     * Allows you to retrieve the datasite's mongo client.
     * This will only be used if the datasite's type is MONGO_DATABASE.
     *
     * @return The datasite's mongo client.
     */
    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

}
