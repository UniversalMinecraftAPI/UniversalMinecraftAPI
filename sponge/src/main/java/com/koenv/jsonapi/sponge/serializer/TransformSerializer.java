package com.koenv.jsonapi.sponge.serializer;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;
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
