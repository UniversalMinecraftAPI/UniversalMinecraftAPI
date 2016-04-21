package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import org.spongepowered.api.Server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerSerializer implements Serializer<Server> {
    @Override
    public Object toJson(Server object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("maxPlayers", object.getMaxPlayers());
        json.put("port", object.getBoundAddress().map(InetSocketAddress::getPort).orElse(-1));
        json.put("ip", object.getBoundAddress().map(InetSocketAddress::getAddress).map(InetAddress::getHostAddress).orElse(""));
        json.put("players", serializerManager.serialize(object.getOnlinePlayers()));
        json.put("worlds", serializerManager.serialize(object.getWorlds()));
        json.put("motd", object.getMotd().toPlain());
        return json;
    }
}
