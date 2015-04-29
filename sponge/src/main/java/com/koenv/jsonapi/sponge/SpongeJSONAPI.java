package com.koenv.jsonapi.sponge;

import com.google.common.base.Optional;
import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.MethodInvocationException;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.MethodParser;
import com.koenv.jsonapi.parser.ParseException;
import com.koenv.jsonapi.parser.expressions.Expression;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import java.util.ArrayList;
import java.util.List;

@Plugin(id = "JSONAPI", name = "JSONAPI", version="0.1-SNAPSHOT")
public class SpongeJSONAPI {
    private static Game game;

    private MethodParser methodParser;
    private MethodInvoker methodInvoker;

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        methodParser = new MethodParser();
        methodInvoker = new MethodInvoker();

        methodInvoker.registerMethods(this);

        game = event.getGame();

        game.getCommandDispatcher().register(this, new CommandCallable() {
            @Override
            public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
                String[] args = arguments.split(" ");
                if (args.length < 1) {
                    source.sendMessage(Texts.of("This plugin needs at least 1 parameter."));
                    return Optional.of(CommandResult.empty());
                }
                String subCommand = args[0];
                if (subCommand.equals("execute") || subCommand.equals("exec")) {
                    if (args.length < 2) {
                        source.sendMessage(Texts.of("Please specify the command."));
                        return Optional.of(CommandResult.empty());
                    }
                    StringBuilder execStringBuilder = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        if (i == 0) {
                            continue;
                        }
                        execStringBuilder.append(args[i]);
                        execStringBuilder.append(" ");
                    }
                    try {
                        Expression expression = methodParser.parse(execStringBuilder.toString());
                        Object result = methodInvoker.invokeMethod(expression);
                        source.sendMessage(Texts.of(String.valueOf(result)));
                        return Optional.of(CommandResult.success());
                    } catch (ParseException | MethodInvocationException e) {
                        source.sendMessage(Texts.of("Failed to execute command: " + e.getMessage()));
                        e.printStackTrace();
                        return Optional.of(CommandResult.empty());
                    }
                }
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