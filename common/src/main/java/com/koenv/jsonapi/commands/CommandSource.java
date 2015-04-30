package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;

public interface CommandSource {
    void sendMessage(String text);

    void sendMessage(ChatColor color, String text);
}
