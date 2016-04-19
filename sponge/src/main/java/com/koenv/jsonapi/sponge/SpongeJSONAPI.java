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
import com.koenv.jsonapi.sponge.methods.ServerMethods;
import com.koenv.jsonapi.sponge.serializer.*;
import com.koenv.jsonapi.sponge.streams.console.Log4JConsoleStream;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import spark.utils.IOUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Plugin(id = "com.koenv.jsonapi.sponge", name = "JSONAPI", version = SpongeJSONAPI.VERSION, description = "A JSON API for Sponge")
public class SpongeJSONAPI implements JSONAPIProvider {
    public static final String VERSION = "0.1-SNAPSHOT";

    private JSONAPIInterface jsonapi;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private Logger logger;

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

        try {
            Class.forName("org.apache.logging.log4j.LogManager");
            new Log4JConsoleStream(jsonapi);
        } catch (ClassNotFoundException e) {
            logger.warn("Unable to find suitable logger implementation to hook in to");
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

    @Override
    public String getJSONAPIVersion() {
        return VERSION;
    }

    @Override
    public String getPlatform() {
        return Sponge.getPlatform().getImplementation().getId();
    }

    @Override
    public String getPlatformVersion() {
        return Sponge.getPlatform().getImplementation().getVersion().orElse("UNKNOWN");
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
                .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("arguments"))))
                .executor(executor)
                .build();

        Sponge.getCommandManager().register(this, mainCommandSpec, "jsonapi");
    }

    private void registerSerializers() {
        jsonapi.getSerializerManager().registerSerializer(CatalogType.class, new CatalogTypeSerializer());
        jsonapi.getSerializerManager().registerSerializer(CommandResult.class, new CommandResultSerializer());
        jsonapi.getSerializerManager().registerSerializer(Dimension.class, new DimensionSerializer());
        jsonapi.getSerializerManager().registerSerializer(Location.class, new LocationSerializer());
        jsonapi.getSerializerManager().registerSerializer(Player.class, new PlayerSerializer());
        jsonapi.getSerializerManager().registerSerializer(Transform.class, new TransformSerializer());
        jsonapi.getSerializerManager().registerSerializer(Server.class, new ServerSerializer());
        jsonapi.getSerializerManager().registerSerializer(World.class, new WorldSerializer());
    }

    private void registerListeners() {
        Sponge.getEventManager().registerListeners(this, new ChatStreamListener(jsonapi.getStreamManager()));
    }

    private void registerMethods() {
        jsonapi.getMethodInvoker().registerMethods(PlayerMethods.class);
        jsonapi.getMethodInvoker().registerMethods(ServerMethods.class);
    }

    public JSONAPIInterface getJSONAPI() {
        return jsonapi;
    }
}