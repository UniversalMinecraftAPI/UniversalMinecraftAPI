package com.koenv.jsonapi.sponge;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.sponge.command.SpongeJSONAPICommandExecutor;
import com.koenv.jsonapi.sponge.listeners.ChatStreamListener;
import com.koenv.jsonapi.sponge.methods.PlayerMethods;
import com.koenv.jsonapi.sponge.serializer.PlayerSerializer;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import spark.utils.IOUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Plugin(id = "com.koenv.jsonapi.sponge", name = "JSONAPI", version = "0.1-SNAPSHOT", description = "A JSON API for Sponge")
public class SpongeJSONAPI implements JSONAPIProvider {
    private JSONAPI jsonapi;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private JSONAPIConfiguration configuration;

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path configFile = Paths.get(configDir + "/config.conf");

        if (Files.notExists(configFile)) {
            InputStream defaultConfig = getClass().getResourceAsStream("defaultConfig.conf");

            try (FileWriter writer = new FileWriter(configFile.toFile())) {
                IOUtils.copy(defaultConfig, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(configFile).build();

        CommentedConfigurationNode node;
        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Path usersConfigFile = Paths.get(configDir + "/users.conf");

        if (Files.notExists(usersConfigFile)) {
            InputStream defaultConfig = getClass().getResourceAsStream("defaultUsers.conf");

            try (FileWriter writer = new FileWriter(usersConfigFile.toFile())) {
                IOUtils.copy(defaultConfig, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ConfigurationLoader<CommentedConfigurationNode> usersLoader = HoconConfigurationLoader.builder().setPath(usersConfigFile).build();

        CommentedConfigurationNode usersNode;
        try {
            usersNode = usersLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        SpongeConfigurationLoader configurationLoader = new SpongeConfigurationLoader();
        configuration = configurationLoader.load(node, usersNode);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        jsonapi = new JSONAPI(this);
        jsonapi.setup(configuration);

        registerSerializers();
        registerCommands();
        registerListeners();
        registerMethods();
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        jsonapi.destroy();
        jsonapi = null;
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

    private void registerSerializers() {
        jsonapi.getSerializerManager().registerSerializer(Player.class, new PlayerSerializer());
    }

    private void registerListeners() {
        Sponge.getEventManager().registerListeners(this, new ChatStreamListener(jsonapi.getStreamManager()));
    }

    private void registerMethods() {
        jsonapi.getMethodInvoker().registerMethods(PlayerMethods.class);
    }

    public JSONAPI getJSONAPI() {
        return jsonapi;
    }
}