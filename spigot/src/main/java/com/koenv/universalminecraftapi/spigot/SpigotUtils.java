package com.koenv.universalminecraftapi.spigot;

import com.koenv.universalminecraftapi.ChatColor;

public class SpigotUtils {
    public static org.bukkit.ChatColor getChatColor(ChatColor chatColor) {
        try {
            return org.bukkit.ChatColor.valueOf(chatColor.name());
        } catch (IllegalArgumentException e) {
            return org.bukkit.ChatColor.WHITE;
        }
    }
}
