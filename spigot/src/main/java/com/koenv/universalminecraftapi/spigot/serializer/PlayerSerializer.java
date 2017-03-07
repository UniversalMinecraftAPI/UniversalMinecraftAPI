package com.koenv.universalminecraftapi.spigot.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;
import org.bukkit.entity.Player;

public class PlayerSerializer implements Serializer<Player> {
    @Override
    public void toJson(Player object, SerializerManager serializerManager, JSONWriter writer) {
        writer.object()
                .key("name").value(object.getName())
                .key("uuid").value(object.getUniqueId())
                .endObject();
    }
}
