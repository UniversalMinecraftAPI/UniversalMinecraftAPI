package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import static java.util.stream.Collectors.toList;

public class WorldSerializer implements Serializer<World> {
    @Override
    public void toJson(World object, SerializerManager serializerManager, JSONWriter writer) {
        writer.object()
                .key("name").value(object.getProperties().getWorldName());

        writer.key("difficulty");
        serializerManager.serialize(object.getDifficulty(), writer);

        writer.key("dimension");
        serializerManager.serialize(object.getDimension(), writer);

        writer.key("seed").value(object.getProperties().getSeed())
                .key("time").value(object.getProperties().getTotalTime())
                .key("players").value(object.getEntities(entity -> entity instanceof Player).stream().map(e -> ((Player) e).getName()).collect(toList()))
                .endObject();
    }
}
