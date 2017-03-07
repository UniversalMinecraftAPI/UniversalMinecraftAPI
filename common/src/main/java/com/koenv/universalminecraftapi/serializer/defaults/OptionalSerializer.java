package com.koenv.universalminecraftapi.serializer.defaults;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

import java.util.Optional;

public class OptionalSerializer implements Serializer<Optional<?>> {
    @Override
    public void toJson(Optional<?> object, SerializerManager serializerManager, JSONWriter writer) {
        serializerManager.serialize(object.orElse(null), writer);
    }
}
