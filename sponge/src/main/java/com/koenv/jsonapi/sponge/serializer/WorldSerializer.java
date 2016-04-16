package com.koenv.jsonapi.sponge.serializer;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.stream.Collectors;

public class WorldSerializer implements Serializer<World> {
    @Override
    public Object toJson(World object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("name", object.getProperties().getWorldName());
        json.put("difficulty", serializerManager.serialize(object.getDifficulty()));
        json.put("dimension", serializerManager.serialize(object.getDimension()));
        json.put("seed", object.getCreationSettings().getSeed());
        json.put("time", object.getProperties().getTotalTime());
        json.put("players", object.getEntities(entity -> entity instanceof Player).stream().map(e -> ((Player) e).getName()).collect(Collectors.toList()));
        return json;
    }
}
