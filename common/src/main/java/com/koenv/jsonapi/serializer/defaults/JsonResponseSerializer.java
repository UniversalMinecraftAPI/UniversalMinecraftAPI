package com.koenv.jsonapi.serializer.defaults;

import com.koenv.jsonapi.http.model.JsonSerializable;
import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONValue;

public class JsonResponseSerializer implements Serializer<JsonSerializable> {
    @Override
    public JSONValue toJson(JsonSerializable object, SerializerManager serializerManager) {
        return object.toJson(serializerManager);
    }
}
