package com.koenv.universalminecraftapi.serializer.defaults;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONValue;

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
