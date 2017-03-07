package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;
import org.spongepowered.api.entity.living.player.Player;

import static org.spongepowered.api.data.key.Keys.GAME_MODE;

public class PlayerSerializer implements Serializer<Player> {
    @Override
    public void toJson(Player object, SerializerManager serializerManager, JSONWriter writer) {
        writer.object()
                .key("name").value(object.getName())
                .key("uuid").value(object.getUniqueId());

        writer.key("world");
        serializerManager.serialize(object.getWorld(), writer);

        writer.key("health").value(object.getHealthData().health().get());

        writer.key("location");
        serializerManager.serialize(object.getTransform(), writer);

        writer.key("foodLevel").value(object.getFoodData().foodLevel().get());

        writer.key("gameMode");
        serializerManager.serialize(object.get(GAME_MODE), writer);

        writer.endObject();
    }
}
