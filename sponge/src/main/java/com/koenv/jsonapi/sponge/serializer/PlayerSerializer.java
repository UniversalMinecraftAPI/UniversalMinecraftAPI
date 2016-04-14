package com.koenv.jsonapi.sponge.serializer;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;
import org.spongepowered.api.entity.living.player.Player;

public class PlayerSerializer implements Serializer<Player> {
    @Override
    public Object toJson(Player object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("name", object.getName());
        json.put("uuid", object.getUniqueId());
        return json;
    }
}
