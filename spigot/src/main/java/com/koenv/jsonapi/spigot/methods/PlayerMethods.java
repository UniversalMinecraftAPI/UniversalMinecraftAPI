package com.koenv.jsonapi.spigot.methods;

import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.APINamespace;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@APINamespace("players")
public class PlayerMethods {
    @APIMethod
    public static Player getPlayer(String name) {
        return Bukkit.getServer().getPlayer(name);
    }

    @APIMethod(operatesOn = Player.class)
    public static String getUUID(Player self) {
        return self.getUniqueId().toString();
    }
}
