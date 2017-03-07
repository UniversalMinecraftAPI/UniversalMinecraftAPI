package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;
import org.spongepowered.api.world.Dimension;

public class DimensionSerializer implements Serializer<Dimension> {
    @Override
    public void toJson(Dimension object, SerializerManager serializerManager, JSONWriter writer) {
        writer.value(object.getType().getId());
    }
}
