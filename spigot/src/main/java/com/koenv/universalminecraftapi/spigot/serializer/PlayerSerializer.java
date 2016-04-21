package com.koenv.universalminecraftapi.spigot.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import org.bukkit.entity.Player;

public class PlayerSerializer implements Serializer<Player> {
    @Override
    public Object toJson(Player object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("name", object.getName());
        json.put("uuid", object.getUniqueId());
        return json;
    }
}
