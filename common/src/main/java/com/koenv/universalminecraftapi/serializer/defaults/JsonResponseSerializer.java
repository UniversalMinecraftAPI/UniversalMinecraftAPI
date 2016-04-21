package com.koenv.universalminecraftapi.serializer.defaults;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONValue;

public class JsonResponseSerializer implements Serializer<JsonSerializable> {
    @Override
    public JSONValue toJson(JsonSerializable object, SerializerManager serializerManager) {
        return object.toJson(serializerManager);
    }
}
