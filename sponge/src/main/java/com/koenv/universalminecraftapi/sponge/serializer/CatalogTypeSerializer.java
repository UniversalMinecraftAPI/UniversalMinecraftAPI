package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import org.spongepowered.api.CatalogType;

public class CatalogTypeSerializer implements Serializer<CatalogType> {
    @Override
    public Object toJson(CatalogType object, SerializerManager serializerManager) {
        return object.getName();
    }
}
