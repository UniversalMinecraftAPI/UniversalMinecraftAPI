package com.koenv.jsonapi.sponge;

import com.google.common.base.Optional;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.methods.APIMethod;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import java.util.ArrayList;
import java.util.List;

@Plugin(id = "JSONAPI", name = "JSONAPI", version="0.1-SNAPSHOT")
public class SpongeJSONAPI implements JSONAPIProvider {
    private static Game game;

    private JSONAPI jsonapi;

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        jsonapi = new JSONAPI(this);
        jsonapi.setup();

        game = event.getGame();

        game.getCommandDispatcher().register(this, new CommandCallable() {
            @Override
            public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
                String[] args = arguments.split(" ");
                if (args.length < 1) {
                    source.sendMessage(Texts.builder("This plugin needs at least 1 parameter.").color(TextColors.RED).build());
                    return Optional.of(CommandResult.empty());
                }
                jsonapi.getCommandManager().handle(new SpongeCommandSource(source), args);
                return Optional.of(CommandResult.empty());
            }

            @Override
            public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
                return new ArrayList<>();
            }

            @Override
            public boolean testPermission(CommandSource source) {
                return true;
            }

            @Override
            public Optional<Text> getShortDescription(CommandSource source) {
                return Optional.of(Texts.of("JSONAPI"));
            }

            @Override
            public Optional<Text> getHelp(CommandSource source) {
                return Optional.of(Texts.of("JSONAPI"));
            }

            @Override
            public Text getUsage(CommandSource source) {
                return Texts.of("/jsonapi");
            }
        }, "jsonapi");
    }

    @APIMethod(namespace = "players")
    public static Player getPlayer(String name) {
        return game.getServer().getPlayer(name).orNull();
    }

    @APIMethod(operatesOn = Player.class)
    public static String getUUID(Player self) {
        return self.getUniqueId().toString();
    }
}