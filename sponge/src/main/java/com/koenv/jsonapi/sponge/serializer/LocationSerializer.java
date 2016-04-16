package com.koenv.jsonapi.sponge.serializer;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;
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
