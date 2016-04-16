package com.koenv.jsonapi.sponge.serializer;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

public class PlayerSerializer implements Serializer<Player> {
    @Override
    public Object toJson(Player object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("name", object.getName());
        json.put("uuid", object.getUniqueId());
        json.put("world", serializerManager.serialize(object.getWorld()));
        json.put("health", object.getHealthData().health().get());
        json.put("location", serializerManager.serialize(object.getTransform()));
        json.put("foodLevel", object.getFoodData().foodLevel().get());
        json.put("gameMode", object.get(Keys.GAME_MODE).map(serializerManager::serialize).orElse(null));
        return json;
    }
}
