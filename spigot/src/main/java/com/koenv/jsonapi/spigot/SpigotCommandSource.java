package com.koenv.jsonapi.spigot;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.commands.CommandSource;
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
}
