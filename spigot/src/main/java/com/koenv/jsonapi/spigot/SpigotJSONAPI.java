package com.koenv.jsonapi.spigot;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.spigot.listeners.ChatStreamListener;
import com.koenv.jsonapi.spigot.methods.PlayerMethods;
import com.koenv.jsonapi.spigot.serializer.PlayerSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotJSONAPI extends JavaPlugin implements JSONAPIProvider {
    private JSONAPI jsonapi;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        SpigotConfigurationLoader loader = new SpigotConfigurationLoader();
        JSONAPIConfiguration config = loader.load(getConfig());

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