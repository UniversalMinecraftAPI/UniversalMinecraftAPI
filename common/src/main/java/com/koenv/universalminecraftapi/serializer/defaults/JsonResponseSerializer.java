package com.koenv.universalminecraftapi.serializer.defaults;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

public class JsonResponseSerializer implements Serializer<JsonSerializable> {
    @Override
    public void toJson(JsonSerializable object, SerializerManager serializerManager, JSONWriter writer) {
        object.toJson(writer, serializerManager);
    }
}
