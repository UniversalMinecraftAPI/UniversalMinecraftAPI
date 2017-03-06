package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import org.spongepowered.api.world.Dimension;

public class DimensionSerializer implements Serializer<Dimension> {
    @Override
    public Object toJson(Dimension object, SerializerManager serializerManager) {
        return object.getType().getId();
    }
}
