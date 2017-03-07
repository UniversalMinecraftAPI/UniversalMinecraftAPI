package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;
import org.spongepowered.api.entity.Transform;

public class TransformSerializer implements Serializer<Transform> {
    @Override
    public void toJson(Transform object, SerializerManager serializerManager, JSONWriter writer) {
        writer.object()
                .key("x").value(object.getLocation().getX())
                .key("y").value(object.getLocation().getY())
                .key("z").value(object.getLocation().getZ())
                .key("pitch").value(object.getPitch())
                .key("yaw").value(object.getYaw())
                .endObject();
    }
}
