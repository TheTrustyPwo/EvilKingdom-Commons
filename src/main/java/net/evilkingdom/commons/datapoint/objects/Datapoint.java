package net.evilkingdom.commons.datapoint.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import net.evilkingdom.commons.datapoint.enums.DatasiteType;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import org.bukkit.plugin.java.JavaPlugin;

import javax.print.Doc;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Datapoint {
    
    private final String name;
    private final Datasite site;

    /**
     * Allows you to create a datapoint for a plugin.
     *
     * @param datasite ~ The datasite of the datapoint.
     * @param name ~ The type of datapoint.
     */
    public Datapoint(final Datasite datasite, final String name) {
        this.name = name;
        this.site = datasite;
    }

    /**
     * Allows you to retrieve the datapoint's site.
     *
     * @return The datapoint's site.
     */
    public Datasite getSite() {
        return this.site;
    }

    /**
     * Allows you to retrieve the datapoint's name.
     *
     * @return The datapoint's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Allows you to register the datapoint.
     */
    public void register() {
        this.site.getPoints().add(this);
        switch (this.site.getType()) {
            case MONGO_DATABASE -> {
                final MongoDatabase mongoDatabase = this.site.getMongoClient().getDatabase(this.site.getName());
                if (!mongoDatabase.listCollectionNames().into(new ArrayList<String>()).contains(this.name)) {
                    mongoDatabase.createCollection(this.name);
                }
            }
            case JSON -> {
                final File dataFolder = new File(this.site.getPlugin().getDataFolder(), "data");
                final File datapointFolder = new File(dataFolder, this.name);
                if (!datapointFolder.exists()) {
                    datapointFolder.mkdirs();
                }
            }
        }
    }

    /**
     * Allows you to retrieve a datapoint model from an identifier.
     *
     * @param identifier ~ The identifier of the datapoint model.
     * @return The json object.
     */
    public CompletableFuture<Optional<JsonObject>> get(final String identifier) {
        return CompletableFuture.supplyAsync(() -> {
            switch (this.site.getType()) {
                case MONGO_DATABASE -> {
                    final Optional<Document> optionalDocument = Optional.ofNullable(this.site.getMongoClient().getDatabase(this.site.getName()).getCollection(this.name).find(Filters.eq("_id", identifier)).first());
                    if (optionalDocument.isPresent()) {
                        final Document document = optionalDocument.get();
                        return Optional.of(JsonParser.parseString(document.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build())).getAsJsonObject());
                    }
                }
                case JSON -> {
                    final Optional<File> optionalFile = Arrays.stream(new File(this.site.getPlugin().getDataFolder() + File.separator + "data", this.name).listFiles()).filter(file -> file.getName().equals(identifier + ".json")).findFirst();
                    if (optionalFile.isPresent()) {
                        final File file = optionalFile.get();
                        String jsonString = null;
                        try {
                            jsonString = Files.readString(file.toPath());
                        } catch (final IOException ioException) {
                            //Does nothing, just in case :)
                        }
                        return Optional.of(JsonParser.parseString(jsonString).getAsJsonObject());
                    }
                }
            }
            return Optional.empty();
        });
    }

    /**
     * Allows you to save a datapoint model.
     *
     * @param jsonObject ~ The json object to save.
     * @param asynchronous ~ If the save is asynchronous (should always be unless it's an emergency saves).
     */
    public void save(final JsonObject jsonObject, final String identifier, final boolean asynchronous) {
        switch (this.site.getType()) {
            case MONGO_DATABASE -> {
                final Document document = Document.parse(new Gson().toJson(jsonObject));
                if (asynchronous) {
                    CompletableFuture.runAsync(() -> this.site.getMongoClient().getDatabase(this.site.getName()).getCollection(this.name).findOneAndReplace(Filters.eq("_id", identifier), document, new FindOneAndReplaceOptions().upsert(true)));
                } else {
                    this.site.getMongoClient().getDatabase(this.site.getName()).getCollection(this.name).findOneAndReplace(Filters.eq("_id", identifier), document, new FindOneAndReplaceOptions().upsert(true));
                }
            }
            case JSON -> {
                if (asynchronous) {
                    CompletableFuture.runAsync(() -> {
                        final File file = new File(this.site.getPlugin().getDataFolder() + File.separator + "data" + File.separator + this.name, identifier + ".json");
                        if (file.exists()) {
                            file.delete();
                        }
                        try {
                            file.createNewFile();
                            final FileWriter fileWriter = new FileWriter(file);
                            fileWriter.write(new Gson().toJson(jsonObject));
                            fileWriter.flush();
                            fileWriter.close();
                        } catch (final IOException ioException) {
                            //Does nothing, just required (this error will never happen anyway).
                        }
                    });
                } else {
                    final File file = new File(this.site.getPlugin().getDataFolder() + File.separator + "data" + File.separator + this.name, identifier + ".json");
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        file.createNewFile();
                        final FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(new Gson().toJson(jsonObject));
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (final IOException ioException) {
                        //Does nothing, just required (this error will never happen anyway).
                    }
                }
            }
        }
    }
    /**
     * Allows you to retrieve if a datapoint model exists from an identifier.
     *
     * @param identifier ~ The identifier of the datapoint model.
     * @return If a datapoint model exists from the identifier.
     */

    public CompletableFuture<Boolean> exists(final String identifier) {
        return CompletableFuture.supplyAsync(() -> {
            switch (this.site.getType()) {
                case MONGO_DATABASE -> {
                    final Optional<Document> optionalDocument = Optional.ofNullable(this.site.getMongoClient().getDatabase(this.site.getName()).getCollection(this.name).find(Filters.eq("_id", identifier)).first());
                    if (optionalDocument.isPresent()) {
                        return true;
                    }
                }
                case JSON -> {
                    final Optional<File> optionalFile = Arrays.stream(new File(this.site.getPlugin().getDataFolder() + File.separator + "data", this.name).listFiles()).filter(file -> file.getName().equals(identifier + ".json")).findFirst();
                    if (optionalFile.isPresent()) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

}
