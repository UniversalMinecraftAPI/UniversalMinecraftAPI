package com.koenv.jsonapi.sponge.serializer;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;
import org.spongepowered.api.world.Dimension;

public class DimensionSerializer implements Serializer<Dimension> {
    @Override
    public Object toJson(Dimension object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("name", object.getName());
        json.put("type", object.getType().getId());
        return json;
    }
}
