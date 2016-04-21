package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import org.spongepowered.api.world.Location;

public class LocationSerializer implements Serializer<Location> {
    @Override
    public Object toJson(Location object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("x", object.getX());
        json.put("y", object.getY());
        json.put("z", object.getZ());
        return json;
    }
}
