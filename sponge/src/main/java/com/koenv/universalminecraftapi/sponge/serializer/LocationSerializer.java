package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;
import org.spongepowered.api.world.Location;

public class LocationSerializer implements Serializer<Location> {
    @Override
    public void toJson(Location object, SerializerManager serializerManager, JSONWriter writer) {
        writer.object()
                .key("x").value(object.getX())
                .key("y").value(object.getY())
                .key("z").value(object.getZ())
                .endObject();
    }
}
