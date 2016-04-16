package com.koenv.jsonapi.sponge.methods;

import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.APINamespace;
import com.koenv.jsonapi.util.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@APINamespace("server")
public class ServerMethods {
    @APIMethod
    public static Server getServer() {
        return Sponge.getServer();
    }

    /*
    Doesn't work yet because of limitations in MethodInvoker: it needs to search for superclasses/interfaces, just like the serializer

    @APIMethod(operatesOn = Server.class)
    public static int getPort(Server self) {
        return self.getBoundAddress().map(InetSocketAddress::getPort).orElse(-1);
    }

    @APIMethod(operatesOn = Server.class)
    public static String getIp(Server self) {
        return self.getBoundAddress().map(InetSocketAddress::getAddress).map(InetAddress::getHostAddress).orElse("");
    }

    @APIMethod(operatesOn = Server.class)
    public static int getMaxPlayers(Server self) {
        return self.getMaxPlayers();
    }

    @APIMethod(operatesOn = Server.class)
    public static String getMotd(Server self) {
        return self.getMotd().toPlain();
    }*/

    @APIMethod
    public static boolean broadcast(String message) {
        Text msg = TextSerializers.formattingCode('%').deserialize(message);
        MessageChannel.TO_ALL.send(msg);

        return true;
    }

    @APIMethod
    public static List<String> getCommandNames() {
        return Sponge.getCommandManager().getAll().entries().stream().map(Map.Entry::getKey).distinct().collect(Collectors.toList());
    }

    @APIMethod
    public static List<JSONObject> getCommands() {
        return Sponge.getCommandManager().getPluginContainers().stream()
                .flatMap(container -> Sponge.getCommandManager().getOwnedBy(container).stream().map(commandMapping -> new ImmutablePair<>(container, commandMapping)))
                .map(pair -> {
                    JSONObject object = new JSONObject();
                    object.put("plugin", pair.getLeft().getName());
                    object.put("aliases", pair.getRight().getAllAliases());
                    object.put("description", pair.getRight().getCallable().getShortDescription(Sponge.getServer().getConsole()).map(Text::toPlain).orElse(""));
                    object.put("help", pair.getRight().getCallable().getHelp(Sponge.getServer().getConsole()).map(Text::toPlain).orElse(""));
                    object.put("usage", pair.getRight().getCallable().getUsage(Sponge.getServer().getConsole()).toPlain());

                    return object;
                })
                .collect(Collectors.toList());
    }

    @APIMethod
    public static CommandResult runCommand(String command) {
        return Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
    }
}
