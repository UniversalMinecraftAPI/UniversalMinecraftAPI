package com.koenv.universalminecraftapi.sponge;

import com.google.inject.Inject;
import com.koenv.universalminecraftapi.UniversalMinecraftAPI;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIProvider;
import com.koenv.universalminecraftapi.config.UniversalMinecraftAPIRootConfiguration;
import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import com.koenv.universalminecraftapi.sponge.command.SpongeUMACommandExecutor;
import com.koenv.universalminecraftapi.sponge.listeners.ChatStreamListener;
import com.koenv.universalminecraftapi.sponge.methods.PlayerMethods;
import com.koenv.universalminecraftapi.sponge.methods.ServerMethods;
import com.koenv.universalminecraftapi.sponge.serializer.*;
import com.koenv.universalminecraftapi.sponge.streams.console.Log4JConsoleStream;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
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
import java.util.concurrent.TimeUnit;

@Plugin(id = "com.koenv.universalminecraftapi.sponge", name = "UniversalMinecraftAPI", version = SpongeUniversalMinecraftAPI.VERSION, description = "A JSON API for Sponge")
public class SpongeUniversalMinecraftAPI implements UniversalMinecraftAPIProvider {
    public static final String VERSION = "0.1-SNAPSHOT";

    private UniversalMinecraftAPIInterface uma;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

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

        UniversalMinecraftAPIRootConfiguration rootConfiguration = SpongeConfigurationLoader.loadRoot(rootNode);

        uma = new UniversalMinecraftAPI(this);
        uma.setup(rootConfiguration);

        registerSerializers();
        registerCommands();
        registerListeners();
        registerMethods();

        try {
            reloadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Sponge.getScheduler()
                .createTaskBuilder()
                .execute(() -> uma.getUserManager().getApiKeyManager().cleanup())
                .interval(1, TimeUnit.MINUTES)
                .delay(1, TimeUnit.MINUTES)
                .async()
                .name(container.getId() + "-APIKey-Cleanup")
                .submit(this);

        try {
            Class.forName("org.apache.logging.log4j.LogManager");
            new Log4JConsoleStream(uma);
        } catch (ClassNotFoundException e) {
            logger.warn("Unable to find suitable logger implementation to hook in to");
        }
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        uma.destroy();
        uma = null;
    }

    @Override
    public void reloadUsers() throws IOException {
        reloadUserConfig();
        UsersConfiguration usersConfiguration = SpongeConfigurationLoader.loadUsersConfiguration(usersNode);

        uma.getUserManager().loadConfiguration(usersConfiguration);
    }

    @Override
    public String getUMAVersion() {
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
        SpongeUMACommandExecutor executor = new SpongeUMACommandExecutor(this);

        CommandSpec mainCommandSpec = CommandSpec.builder()
                .description(Text.of("Main UniversalMinecraftAPI command"))
                .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("arguments"))))
                .executor(executor)
                .build();

        Sponge.getCommandManager().register(this, mainCommandSpec, "uma", "universalminecraftapi");
    }

    private void registerSerializers() {
        uma.getSerializerManager().registerSerializer(CatalogType.class, new CatalogTypeSerializer());
        uma.getSerializerManager().registerSerializer(Dimension.class, new DimensionSerializer());
        uma.getSerializerManager().registerSerializer(Location.class, new LocationSerializer());
        uma.getSerializerManager().registerSerializer(Player.class, new PlayerSerializer());
        uma.getSerializerManager().registerSerializer(Transform.class, new TransformSerializer());
        uma.getSerializerManager().registerSerializer(Server.class, new ServerSerializer());
        uma.getSerializerManager().registerSerializer(World.class, new WorldSerializer());
    }

    private void registerListeners() {
        Sponge.getEventManager().registerListeners(this, new ChatStreamListener(uma.getStreamManager()));
    }

    private void registerMethods() {
        uma.getMethodInvoker().registerMethods(PlayerMethods.class);
        uma.getMethodInvoker().registerMethods(ServerMethods.class);
    }

    public UniversalMinecraftAPIInterface getUMA() {
        return uma;
    }
}