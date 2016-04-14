package com.koenv.jsonapi.serializer.defaults;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONArray;
import com.koenv.jsonapi.util.json.JSONValue;

import java.util.Collection;

public class CollectionSerializer implements Serializer<Collection> {
    @Override
    public JSONValue toJson(Collection object, SerializerManager serializerManager) {
        JSONArray jsonArray = new JSONArray();
        for (Object item : object) {
            jsonArray.put(serializerManager.serialize(item));
        }
        return jsonArray;
    }
}
