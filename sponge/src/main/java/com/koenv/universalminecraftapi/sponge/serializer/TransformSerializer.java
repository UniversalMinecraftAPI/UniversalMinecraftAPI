package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import org.spongepowered.api.entity.Transform;

public class TransformSerializer implements Serializer<Transform> {
    @Override
    public Object toJson(Transform object, SerializerManager serializerManager) {
        JSONObject json = (JSONObject) serializerManager.serialize(object.getLocation());
        json.put("pitch", object.getPitch());
        json.put("yaw", object.getYaw());
        return json;
    }
}
