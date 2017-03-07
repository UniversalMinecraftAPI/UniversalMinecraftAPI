package com.koenv.universalminecraftapi.serializer;

import com.koenv.universalminecraftapi.util.json.JSONWriter;

public interface Serializer<T> {
    void toJson(T object, SerializerManager serializerManager, JSONWriter writer);
}
