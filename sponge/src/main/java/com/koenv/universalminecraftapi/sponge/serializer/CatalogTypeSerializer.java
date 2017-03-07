package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;
import org.spongepowered.api.CatalogType;

public class CatalogTypeSerializer implements Serializer<CatalogType> {
    @Override
    public void toJson(CatalogType object, SerializerManager serializerManager, JSONWriter writer) {
        writer.value(object.getName());
    }
}
