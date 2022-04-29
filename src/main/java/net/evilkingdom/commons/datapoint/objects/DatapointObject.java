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

import java.util.HashMap;
import java.util.Map;

public class DatapointObject {

    private Object object;
    private final HashMap<String, DatapointObject> innerObjects;

    /**
     * Allows you to create a datapoint object.
     * This is used for objects that require a heading object.
     *
     * @param object ~ The heading object.
     */
    public DatapointObject(final Object object) {
        this.object = object;
        this.innerObjects = new HashMap<String, DatapointObject>();
    }

    /**
     * Allows you to create a datapoint object.
     * This is used for objects that don't require a heading object.
     */
    public DatapointObject() {
        this.innerObjects = new HashMap<String, DatapointObject>();
    }

    /**
     * Allows you to retrieve the datapoint object's object.
     *
     * @return The datapoint object's object.
     */
    public Object getObject() {
        return this.object;
    }

    /**
     * Allows you to retrieve the datapoint object's inner objects.
     *
     * @return The datapoint object's inner objects.
     */
    public HashMap<String, DatapointObject> getInnerObjects() {
        return this.innerObjects;
    }

    /**
     * Allows you to retrieve the datapoint object as Mongo.
     *
     * @return The datapoint object as Mongo.
     */
    public Object asMongo() {
        if (this.innerObjects.isEmpty()) {
            if (this.object.getClass() != DatapointObject.class) {
                return this.object;
            } else {
                final DatapointObject datapointObject = (DatapointObject) this.object;
                return datapointObject.asMongo();
            }
        } else {
            final BasicDBObject basicDBObject = new BasicDBObject();
            this.innerObjects.forEach((key, object) -> {
                if (object.getInnerObjects().isEmpty()) {
                    basicDBObject.put(key, object.getObject());
                } else {
                    final BasicDBObject innerBasicDBObject = new BasicDBObject();
                    object.getInnerObjects().forEach((innerKey, innerObject) -> {
                        innerBasicDBObject.put(innerKey, innerObject.asMongo());
                    });
                    basicDBObject.put(key, innerBasicDBObject);
                }
            });
            return basicDBObject;
        }
    }

    /**
     * Allows you to retrieve the datapoint object as JSON.
     *
     * @return The datapoint object as JSON.
     */
    public Object asJson() {
        if (this.innerObjects.isEmpty()) {
            if (this.object.getClass() != DatapointObject.class) {
                return JsonParser.parseString(new Gson().toJson(this.object)).getAsJsonObject();
            } else {
                final DatapointObject datapointObject = (DatapointObject) this.object;
                return datapointObject.asMongo();
            }
        } else {
            final JsonObject jsonObject = new JsonObject();
            this.innerObjects.forEach((key, object) -> {
                if (object.innerObjects.isEmpty()) {
                    jsonObject.add(key, JsonParser.parseString(new Gson().toJson(object.getObject())));
                } else {
                    final JsonObject innerJsonObject = new JsonObject();
                    object.getInnerObjects().forEach((innerKey, innerObject) -> {
                        innerJsonObject.add(innerKey, JsonParser.parseString(new Gson().toJson(innerObject.asJson())));
                    });
                    jsonObject.add(key, innerJsonObject);
                }
            });
            return jsonObject;
        }
    }

    /**
     * Allows you to retrieve the datapoint object from a Mongo object.
     *
     * @param basicDBObject ~ The Mongo object.
     * @return The datapoint object from a Mongo object.
     */
    public DatapointObject fromMongo(final BasicDBObject basicDBObject) {
        DatapointObject datapointObject = null;
        for (final Map.Entry<String, Object> objectEntry : basicDBObject.entrySet()) {
            if (objectEntry.getValue().getClass() == BasicDBObject.class) {
                datapointObject = new DatapointObject();
                final BasicDBObject innerBasicDBObject = (BasicDBObject) object;
                for (final Map.Entry<String, Object> innerObjectEntry : innerBasicDBObject.entrySet()) {
                    final BasicDBObject innerInnerBasicDBObject = (BasicDBObject) innerObjectEntry.getValue();
                    datapointObject.getInnerObjects().put(innerObjectEntry.getKey(), this.fromMongo(innerInnerBasicDBObject));
                }
            } else {
                datapointObject = new DatapointObject(object);
            }
        }
        return datapointObject;
    }

    /**
     * Allows you to retrieve the datapoint object from JSON.
     *
     * @param jsonObject ~ The JSON object.
     * @return The datapoint object from JSON.
     */
    public DatapointObject fromJson(final JsonObject jsonObject) {
        DatapointObject datapointObject = null;
        for (final Map.Entry<String, JsonElement> objectEntry : jsonObject.entrySet()) {
            if (objectEntry.getValue().isJsonObject()) {
                datapointObject = new DatapointObject();
                final JsonObject innerJsonObject = objectEntry.getValue().getAsJsonObject();
                for (final Map.Entry<String, JsonElement> innerObjectEntry : innerJsonObject.entrySet()) {
                    final JsonObject innerInnerJsonObject = innerObjectEntry.getValue().getAsJsonObject();
                    datapointObject.getInnerObjects().put(innerObjectEntry.getKey(), this.fromJson(innerInnerJsonObject));
                }
            } else {
                datapointObject = new DatapointObject(object);
            }
        }
        return datapointObject;
    }

}
