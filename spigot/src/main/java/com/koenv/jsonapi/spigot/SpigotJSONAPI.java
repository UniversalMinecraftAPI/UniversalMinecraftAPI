package com.koenv.jsonapi.spigot;

import com.google.common.base.Charsets;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIInterface;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.config.JSONAPIRootConfiguration;
import com.koenv.jsonapi.config.user.UsersConfiguration;
import com.koenv.jsonapi.spigot.listeners.ChatStreamListener;
import com.koenv.jsonapi.spigot.methods.PlayerMethods;
import com.koenv.jsonapi.spigot.serializer.PlayerSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SpigotJSONAPI extends JavaPlugin implements JSONAPIProvider {
    private JSONAPIInterface jsonapi;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        JSONAPIRootConfiguration config = null;
        try {
            config = SpigotConfigurationLoader.load(getConfig());
        } catch (InvalidConfigurationException e) {
            getLogger().severe("Invalid configuration, shutting down");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        jsonapi = new JSONAPI(this);
        jsonapi.setup(config);

        registerSerializers();
        registerListeners();
        registerMethods();

        try {
            reloadUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getCommand("jsonapi").setExecutor((sender, command, label, args) -> {
            jsonapi.getCommandManager().handle(new SpigotCommandSource(sender), args);
            return true;
        });
    }

    @Override
    public void onDisable() {
        if (jsonapi != null) {
            jsonapi.destroy();
        }
        jsonapi = null;
    }

    @Override
    public void reloadUsers() throws Exception {
        UsersConfiguration configuration = SpigotConfigurationLoader.loadUsersConfiguration(getUserConfig());

        jsonapi.getUserManager().loadConfiguration(configuration);
    }

    @Override
    public String getJSONAPIVersion() {
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
        jsonapi.getSerializerManager().registerSerializer(Player.class, new PlayerSerializer());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatStreamListener(jsonapi.getStreamManager()), this);
    }

    private void registerMethods() {
        jsonapi.getMethodInvoker().registerMethods(PlayerMethods.class);
    }
}