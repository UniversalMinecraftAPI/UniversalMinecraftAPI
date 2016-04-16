package com.koenv.jsonapi.sponge.serializer;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import org.spongepowered.api.CatalogType;

public class CatalogTypeSerializer implements Serializer<CatalogType> {
    @Override
    public Object toJson(CatalogType object, SerializerManager serializerManager) {
        return object.getName();
    }
}
