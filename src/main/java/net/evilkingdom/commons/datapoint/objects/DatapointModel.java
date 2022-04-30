package net.evilkingdom.commons.datapoint.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DatapointModel {

    private HashMap<String, DatapointObject> objects;

    /**
     * Allows you to create a datapoint model.
     *
     * @param identifier ~ The identifier of the datapoint model.
     */
    public DatapointModel(final String identifier) {
        this.objects = new HashMap<String, DatapointObject>();
        this.objects.put("_id", new DatapointObject(identifier));
    }

    /**
     * Allows you to retrieve the datapoint model's objects.
     *
     * @return The datapoint model's objects.
     */
    public HashMap<String, DatapointObject> getObjects() {
        return this.objects;
    }

    /**
     * Allows you to retrieve the datapoint model as Mongo.
     *
     * @return The datapoint model as Mongo.
     */
    public Document asMongo() {
        final Document document = new Document();
        this.objects.forEach((key, object) -> document.put(key, object.asMongo()));
        return document;
    }

    /**
     * Allows you to retrieve the datapoint model as JSON.
     *
     * @return The datapoint model as JSON.
     */
    public JsonObject asJson() {
        final JsonObject jsonObject = new JsonObject();
        this.objects.forEach((key, object) -> jsonObject.add(key, JsonParser.parseString(new Gson().toJson(object.asJson()))));
        return jsonObject;
    }

    /**
     * Allows you to retrieve the datapoint model from a Mongo document.
     *
     * @param document ~ The Mongo document.
     * @return The datapoint model from a Mongo document.
     */
    public static DatapointModel fromMongo(final Document document) {
        final DatapointModel datapointModel = new DatapointModel(document.getString("_id"));
        document.forEach((key, value) -> {
            final DatapointObject datapointObject = DatapointObject.fromMongo(value);
            if (datapointObject.getObject() != "N/A") {
                datapointModel.getObjects().put(key, datapointObject);
            }
        });
        return datapointModel;
    }

    /**
     * Allows you to retrieve the datapoint model as JSON.
     *
     * @param jsonObject ~ The JSON object.
     * @return The datapoint model from JSON.
     */
    public static DatapointModel fromJson(final JsonObject jsonObject) {
        final DatapointModel datapointModel = new DatapointModel(jsonObject.get("_id").getAsString());
        jsonObject.entrySet().forEach(key -> {
            final DatapointObject datapointObject = DatapointObject.fromJson(key.getValue());
            if (datapointObject.getObject() != "N/A") {
                datapointModel.getObjects().put(key.getKey(), datapointObject);
            }
        });
        return datapointModel;
    }

}
