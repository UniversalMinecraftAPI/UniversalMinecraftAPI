package com.koenv.jsonapi.sponge;

import com.google.common.collect.ImmutableMap;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.sponge.command.SpongeJSONAPICommandExecutor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(id = "com.koenv.jsonapi.sponge", name = "JSONAPI", version = "0.1-SNAPSHOT", description = "A JSON API for Sponge")
public class SpongeJSONAPI implements JSONAPIProvider {
    private JSONAPI jsonapi;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        jsonapi = new JSONAPI(this);
        jsonapi.setup();

        registerCommands();
    }

    private void registerCommands() {
        SpongeJSONAPICommandExecutor executor = new SpongeJSONAPICommandExecutor(this);

        CommandSpec execCommandSpec = CommandSpec.builder()
                .description(Text.of("Execute a JSONAPI expression and return the result"))
                .permission("jsonapi.command.execute")
                .arguments(
                        GenericArguments.remainingJoinedStrings(Text.of("expression"))
                )
                .executor(executor::executeExecCommand)
                .build();

        CommandSpec createApiDocCommandSpec = CommandSpec.builder()
                .description(Text.of("Create the API documentation"))
                .permission("jsonapi.command.createapidoc")
                .arguments(
                        GenericArguments.string(Text.of("file")),
                        GenericArguments.optional(
                                GenericArguments.choices(
                                        Text.of("format"),
                                        ImmutableMap.<String, String>builder()
                                                .put("json", "json")
                                                .put("markdown", "markdown")
                                                .put("md", "markdown")
                                                .build()
                                )
                        )
                )
                .executor(executor::executeCreateApiDocCommand)
                .build();

        CommandSpec mainCommandSpec = CommandSpec.builder()
                .permission("jsonapi.command")
                .description(Text.of("Main JSONAPI command"))
                .child(execCommandSpec, "exec", "execute", "e")
                .child(createApiDocCommandSpec, "createapidoc", "create_api_doc")
                .build();

        Sponge.getCommandManager().register(this, mainCommandSpec, "jsonapi");
    }

    @APIMethod(namespace = "players")
    public static Player getPlayer(String name) {
        return Sponge.getServer().getPlayer(name).orElse(null);
    }

    @APIMethod(operatesOn = Player.class)
    public static String getUUID(Player self) {
        return self.getUniqueId().toString();
    }

    public JSONAPI getJSONAPI() {
        return jsonapi;
    }
}