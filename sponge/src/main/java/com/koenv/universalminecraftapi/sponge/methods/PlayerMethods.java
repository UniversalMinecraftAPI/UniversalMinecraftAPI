package com.koenv.universalminecraftapi.sponge.methods;

import com.koenv.universalminecraftapi.http.rest.RestOperation;
import com.koenv.universalminecraftapi.http.rest.RestPath;
import com.koenv.universalminecraftapi.http.rest.RestResource;
import com.koenv.universalminecraftapi.methods.APIMethod;
import com.koenv.universalminecraftapi.methods.APINamespace;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

@APINamespace("players")
public class PlayerMethods {
    @APIMethod
    @RestResource("players/:name")
    public static Player getPlayer(@RestPath("name") String name) {
        return Sponge.getServer().getPlayer(name).orElse(null);
    }

    @APIMethod(operatesOn = Player.class)
    @RestOperation(value = Player.class, path = "uuid")
    public static String getUUID(Player self) {
        return self.getUniqueId().toString();
    }
}
