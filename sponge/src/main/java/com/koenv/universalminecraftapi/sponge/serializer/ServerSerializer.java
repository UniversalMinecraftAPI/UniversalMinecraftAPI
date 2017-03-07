package com.koenv.universalminecraftapi.sponge.serializer;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;
import org.spongepowered.api.Server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerSerializer implements Serializer<Server> {
    @Override
    public void toJson(Server object, SerializerManager serializerManager, JSONWriter writer) {
        writer.object()
                .key("maxPlayers").value(object.getMaxPlayers())
                .key("port").value(object.getBoundAddress().map(InetSocketAddress::getPort).orElse(-1))
                .key("ip").value(object.getBoundAddress().map(InetSocketAddress::getAddress).map(InetAddress::getHostAddress).orElse(""));

        writer.key("players");
        serializerManager.serialize(object.getOnlinePlayers(), writer);

        writer.key("worlds");
        serializerManager.serialize(object.getWorlds(), writer);

        writer
                .key("motd").value(object.getMotd().toPlain())
                .endObject();
    }
}
