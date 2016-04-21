package com.koenv.universalminecraftapi.sponge.methods;

import com.koenv.universalminecraftapi.methods.APIMethod;
import com.koenv.universalminecraftapi.methods.APINamespace;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

@APINamespace("players")
public class PlayerMethods {
    @APIMethod
    public static Player getPlayer(String name) {
        return Sponge.getServer().getPlayer(name).orElse(null);
    }

    @APIMethod(operatesOn = Player.class)
    public static String getUUID(Player self) {
        return self.getUniqueId().toString();
    }
}
