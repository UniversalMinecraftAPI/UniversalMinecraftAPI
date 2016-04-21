package com.koenv.universalminecraftapi.spigot;

import com.koenv.universalminecraftapi.ChatColor;
import com.koenv.universalminecraftapi.commands.CommandSource;
import org.bukkit.command.CommandSender;

public class SpigotCommandSource implements CommandSource {
    private CommandSender commandSender;

    public SpigotCommandSource(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Override
    public void sendMessage(String text) {
        this.commandSender.sendMessage(text);
    }

    @Override
    public void sendMessage(ChatColor color, String text) {
        this.commandSender.sendMessage(SpigotUtils.getChatColor(color) + text);
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }
}
