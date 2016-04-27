package com.koenv.universalminecraftapi.sponge.methods;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.http.rest.RestBody;
import com.koenv.universalminecraftapi.http.rest.RestOperation;
import com.koenv.universalminecraftapi.http.rest.RestResource;
import com.koenv.universalminecraftapi.methods.APIMethod;
import com.koenv.universalminecraftapi.methods.APINamespace;
import com.koenv.universalminecraftapi.permissions.RequiresPermission;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@APINamespace("server")
public class ServerMethods {
    @APIMethod
    @RestResource("server")
    public static Server getServer() {
        return Sponge.getServer();
    }

    @APIMethod(operatesOn = Server.class)
    @RestOperation(Server.class)
    @RequiresPermission("server.port")
    public static int getPort(Server self) {
        return self.getBoundAddress().map(InetSocketAddress::getPort).orElse(-1);
    }

    @APIMethod(operatesOn = Server.class)
    @RestOperation(Server.class)
    @RequiresPermission("server.ip")
    public static String getIp(Server self) {
        return self.getBoundAddress().map(InetSocketAddress::getAddress).map(InetAddress::getHostAddress).orElse("");
    }

    @APIMethod(operatesOn = Server.class)
    @RestOperation(Server.class)
    @RequiresPermission("server.max_players")
    public static int getMaxPlayers(Server self) {
        return self.getMaxPlayers();
    }

    @APIMethod(operatesOn = Server.class)
    @RestOperation(Server.class)
    @RequiresPermission("server.motd")
    public static String getMotd(Server self) {
        return self.getMotd().toPlain();
    }

    @APIMethod
    @RequiresPermission("server.broadcast")
    public static boolean broadcast(String message) {
        Text msg = TextSerializers.formattingCode('%').deserialize(message);
        MessageChannel.TO_ALL.send(msg);

        return true;
    }

    @RestOperation(Server.class)
    @RequiresPermission("server.broadcast")
    // TODO: A @RestResource should also accept a body?
    public static boolean broadcast(Server self, @RestBody("message") String message) {
        return broadcast(message);
    }

    @APIMethod
    @RestResource("server/commands/names")
    public static List<String> getCommandNames() {
        return Sponge.getCommandManager().getAll().entries().stream().map(Map.Entry::getKey).distinct().collect(Collectors.toList());
    }

    @APIMethod
    @RestResource("server/commands")
    public static List<Command> getCommands() {
        return Sponge.getCommandManager().getPluginContainers().stream()
                .flatMap(container -> Sponge.getCommandManager().getOwnedBy(container).stream().map(commandMapping -> new ImmutablePair<>(container, commandMapping)))
                .map(pair -> {
                    JSONObject object = new JSONObject();
                    object.put("plugin", pair.getLeft().getName());
                    object.put("aliases", pair.getRight().getAllAliases());
                    object.put("description", pair.getRight().getCallable().getShortDescription(Sponge.getServer().getConsole()).map(Text::toPlain).orElse(""));
                    object.put("help", pair.getRight().getCallable().getHelp(Sponge.getServer().getConsole()).map(Text::toPlain).orElse(""));
                    object.put("usage", pair.getRight().getCallable().getUsage(Sponge.getServer().getConsole()).toPlain());

                    return new Command(object);
                })
                .collect(Collectors.toList());
    }

    public static class Command implements JsonSerializable {
        private JSONObject json;

        public Command(JSONObject json) {
            this.json = json;
        }

        @Override
        public JSONObject toJson(SerializerManager serializerManager) {
            return json;
        }
    }

    @APIMethod
    @RequiresPermission("server.run_command")
    public static void runCommand(String command) {
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
    }

    @RestOperation(Server.class)
    @RequiresPermission("server.run_command")
    public static void runCommand(Server self, @RestBody("command") String command) {
        runCommand(command);
    }
}
