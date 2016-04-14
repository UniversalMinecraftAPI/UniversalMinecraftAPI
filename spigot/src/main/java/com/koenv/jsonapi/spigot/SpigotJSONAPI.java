package com.koenv.jsonapi.spigot;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.methods.APIMethod;
import org.bukkit.Bukkit;
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

        getCommand("jsonapi").setExecutor((sender, command, label, args) -> {
            jsonapi.getCommandManager().handle(new SpigotCommandSource(sender), args);
            return true;
        });
    }

    @APIMethod(namespace = "players")
    public static Player getPlayer(String name) {
        return Bukkit.getServer().getPlayer(name);
    }

    @APIMethod(operatesOn = Player.class)
    public static String getUUID(Player self) {
        return self.getUniqueId().toString();
    }
}