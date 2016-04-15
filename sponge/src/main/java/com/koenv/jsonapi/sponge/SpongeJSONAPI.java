package com.koenv.jsonapi.sponge;

import com.google.inject.Inject;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIInterface;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.config.JSONAPIRootConfiguration;
import com.koenv.jsonapi.config.user.UsersConfiguration;
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
    private JSONAPIInterface jsonapi;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private CommentedConfigurationNode rootNode;
    private CommentedConfigurationNode usersNode;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        try {
            reloadConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JSONAPIRootConfiguration rootConfiguration = SpongeConfigurationLoader.loadRoot(rootNode);

        jsonapi = new JSONAPI(this);
        jsonapi.setup(rootConfiguration);

        registerSerializers();
        registerCommands();
        registerListeners();
        registerMethods();

        try {
            reloadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        jsonapi.destroy();
        jsonapi = null;
    }

    @Override
    public void reloadUsers() throws IOException {
        reloadUserConfig();
        UsersConfiguration usersConfiguration = SpongeConfigurationLoader.loadUsersConfiguration(usersNode);

        jsonapi.getUserManager().loadConfiguration(usersConfiguration);
    }

    private void reloadConfig() throws IOException {
        checkConfigDir();

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

        rootNode = loader.load();
    }

    private void reloadUserConfig() throws IOException {
        checkConfigDir();

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

        usersNode = usersLoader.load();
    }

    private void checkConfigDir() throws IOException {
        if (!Files.exists(configDir)) {
            Files.createDirectories(configDir);
        }
    }

    private void registerCommands() {
        SpongeJSONAPICommandExecutor executor = new SpongeJSONAPICommandExecutor(this);

        CommandSpec mainCommandSpec = CommandSpec.builder()
                .description(Text.of("Main JSONAPI command"))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("arguments")))
                .executor(executor)
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

    public JSONAPIInterface getJSONAPI() {
        return jsonapi;
    }
}