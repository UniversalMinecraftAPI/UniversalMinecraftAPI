package com.koenv.jsonapi.spigot;

import com.google.common.base.Charsets;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.spigot.listeners.ChatStreamListener;
import com.koenv.jsonapi.spigot.methods.PlayerMethods;
import com.koenv.jsonapi.spigot.serializer.PlayerSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SpigotJSONAPI extends JavaPlugin implements JSONAPIProvider {
    private JSONAPI jsonapi;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        File usersConfigFile = new File(getDataFolder(), "users.yml");

        if (!usersConfigFile.exists()) {
            saveResource("users.yml", false);
        }
        
        FileConfiguration usersConfig = YamlConfiguration.loadConfiguration(usersConfigFile);

        final InputStream defConfigStream = getResource("users.yml");
        if (defConfigStream == null) {
            return;
        }

        usersConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));

        SpigotConfigurationLoader loader = new SpigotConfigurationLoader();
        JSONAPIConfiguration config = loader.load(getConfig(), usersConfig);

        jsonapi = new JSONAPI(this);
        jsonapi.setup(config);

        registerSerializers();
        registerListeners();
        registerMethods();

        getCommand("jsonapi").setExecutor((sender, command, label, args) -> {
            jsonapi.getCommandManager().handle(new SpigotCommandSource(sender), args);
            return true;
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        jsonapi.destroy();
        jsonapi = null;
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