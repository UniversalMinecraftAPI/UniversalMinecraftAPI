package com.koenv.universalminecraftapi.serializer.defaults;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

import java.util.Collection;

public class CollectionSerializer implements Serializer<Collection> {
    @Override
    public void toJson(Collection object, SerializerManager serializerManager, JSONWriter writer) {
        writer.array();
        for (Object item : object) {
            serializerManager.serialize(item, writer);
        }
        writer.endArray();
    }
}
