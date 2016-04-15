package com.koenv.jsonapi.spigot;

import com.koenv.jsonapi.ChatColor;

public class SpigotUtils {
    public static org.bukkit.ChatColor getChatColor(ChatColor chatColor) {
        switch (chatColor) {
            case RED:
                return org.bukkit.ChatColor.RED;
            case GREEN:
                return org.bukkit.ChatColor.GREEN;
            case BLUE:
                return org.bukkit.ChatColor.BLUE;
            default:
                return org.bukkit.ChatColor.WHITE;
        }
    }
}
