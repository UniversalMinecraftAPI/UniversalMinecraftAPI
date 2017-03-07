package com.koenv.universalminecraftapi.sponge.methods;

import com.koenv.universalminecraftapi.http.rest.RestOperation;
import com.koenv.universalminecraftapi.http.rest.RestPath;
import com.koenv.universalminecraftapi.http.rest.RestResource;
import com.koenv.universalminecraftapi.methods.APIMethod;
import com.koenv.universalminecraftapi.methods.APINamespace;
import com.koenv.universalminecraftapi.permissions.RequiresPermission;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;

@APINamespace("players")
public class PlayerMethods {
    @APIMethod
    @RestResource("players/list")
    @RequiresPermission("players.get")
    public static Collection<Player> getPlayers() {
        return Sponge.getServer().getOnlinePlayers();
    }

    @APIMethod
    @RestResource("players/:name")
    @RequiresPermission("players.get")
    public static Player getPlayer(@RestPath("name") String name) {
        return Sponge.getServer().getPlayer(name).orElse(null);
    }

    @APIMethod(operatesOn = Player.class)
    @RestOperation(value = Player.class, path = "uuid")
    @RequiresPermission("players.uuid")
    public static String getUUID(Player self) {
        return self.getUniqueId().toString();
    }
}
