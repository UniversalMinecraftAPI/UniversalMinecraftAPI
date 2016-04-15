package com.koenv.jsonapi.spigot;

import com.koenv.jsonapi.ChatColor;

public class SpigotUtils {
    public static org.bukkit.ChatColor getChatColor(ChatColor chatColor) {
        try {
            return org.bukkit.ChatColor.valueOf(chatColor.name());
        } catch (IllegalArgumentException e) {
            return org.bukkit.ChatColor.WHITE;
        }
    }
}
