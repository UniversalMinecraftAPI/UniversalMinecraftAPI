package com.koenv.jsonapi.sponge;

import com.google.common.eventbus.Subscribe;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.sponge.command.SpongeJSONAPICommandExecutor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(id = "com.koenv.jsonapi.sponge", name = "JSONAPI", version = "0.1-SNAPSHOT", description = "A JSON API for Sponge")
public class SpongeJSONAPI implements JSONAPIProvider {
    private JSONAPI jsonapi;

    @Subscribe
    public void onServerStart(GameStartedServerEvent event) {
        jsonapi = new JSONAPI(this);
        jsonapi.setup();

        CommandSpec mainCommandSpec = CommandSpec.builder()
                .description(Text.of("Main JSONAPI command"))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("arguments")))
                .executor(new SpongeJSONAPICommandExecutor(this))
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