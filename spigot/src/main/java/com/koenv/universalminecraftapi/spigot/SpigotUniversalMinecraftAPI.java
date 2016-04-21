package com.koenv.universalminecraftapi.spigot;

import com.google.common.base.Charsets;
import com.koenv.universalminecraftapi.UniversalMinecraftAPI;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIProvider;
import com.koenv.universalminecraftapi.config.UniversalMinecraftAPIRootConfiguration;
import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import com.koenv.universalminecraftapi.spigot.listeners.ChatStreamListener;
import com.koenv.universalminecraftapi.spigot.methods.PlayerMethods;
import com.koenv.universalminecraftapi.spigot.serializer.PlayerSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SpigotUniversalMinecraftAPI extends JavaPlugin implements UniversalMinecraftAPIProvider {
    private UniversalMinecraftAPIInterface uma;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        UniversalMinecraftAPIRootConfiguration config = null;
        try {
            config = SpigotConfigurationLoader.load(getConfig());
        } catch (InvalidConfigurationException e) {
            getLogger().severe("Invalid configuration, shutting down");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        uma = new UniversalMinecraftAPI(this);
        uma.setup(config);

        registerSerializers();
        registerListeners();
        registerMethods();

        try {
            reloadUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getCommand("universalminecraftapi").setExecutor((sender, command, label, args) -> {
            uma.getCommandManager().handle(new SpigotCommandSource(sender), args);
            return true;
        });
    }

    @Override
    public void onDisable() {
        if (uma != null) {
            uma.destroy();
        }
        uma = null;
    }

    @Override
    public void reloadUsers() throws Exception {
        UsersConfiguration configuration = SpigotConfigurationLoader.loadUsersConfiguration(getUserConfig());

        uma.getUserManager().loadConfiguration(configuration);
    }

    @Override
    public String getUMAVersion() {
        return getDescription().getVersion();
    }

    @Override
    public String getPlatform() {
        return getServer().getName();
    }

    @Override
    public String getPlatformVersion() {
        return getServer().getVersion();
    }

    public FileConfiguration getUserConfig() {
        File usersConfigFile = new File(getDataFolder(), "users.yml");

        if (!usersConfigFile.exists()) {
            saveResource("users.yml", false);
        }

        FileConfiguration usersConfig = YamlConfiguration.loadConfiguration(usersConfigFile);

        final InputStream defConfigStream = getResource("users.yml");
        if (defConfigStream != null) {
            usersConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        } else {
            getLogger().warning("Unable to load default configuration");
        }

        return usersConfig;
    }

    private void registerSerializers() {
        uma.getSerializerManager().registerSerializer(Player.class, new PlayerSerializer());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatStreamListener(uma.getStreamManager()), this);
    }

    private void registerMethods() {
        uma.getMethodInvoker().registerMethods(PlayerMethods.class);
    }
}