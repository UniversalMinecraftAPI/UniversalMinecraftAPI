package com.koenv.jsonapi.sponge;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.commands.CommandSource;
import org.spongepowered.api.text.Texts;

public class SpongeCommandSource implements CommandSource {
    private org.spongepowered.api.util.command.CommandSource commandSource;

    public SpongeCommandSource(org.spongepowered.api.util.command.CommandSource commandSource) {
        this.commandSource = commandSource;
    }

    @Override
    public void sendMessage(String text) {
        commandSource.sendMessage(Texts.of(text));
    }

    @Override
    public void sendMessage(ChatColor color, String text) {
        commandSource.sendMessage(Texts.builder(text).color(SpongeUtils.getTextColor(color)).build());
    }
}
