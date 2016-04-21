package com.koenv.universalminecraftapi.http.model;

import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;

public interface JsonSerializable {
    JSONObject toJson(SerializerManager serializerManager);
}
