package com.koenv.universalminecraftapi.http.model;

import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

public interface JsonSerializable {
    void toJson(JSONWriter writer, SerializerManager serializerManager);
}
