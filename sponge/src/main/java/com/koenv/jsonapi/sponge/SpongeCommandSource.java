package com.koenv.jsonapi.sponge;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.commands.CommandSource;
import org.spongepowered.api.text.Text;

public class SpongeCommandSource implements CommandSource {
    private org.spongepowered.api.command.CommandSource commandSource;

    public SpongeCommandSource(org.spongepowered.api.command.CommandSource commandSource) {
        this.commandSource = commandSource;
    }

    @Override
    public void sendMessage(String text) {
        commandSource.sendMessage(Text.of(text));
    }

    @Override
    public void sendMessage(ChatColor color, String text) {
        commandSource.sendMessage(Text.builder(text).color(SpongeUtils.getTextColor(color)).build());
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSource.hasPermission(permission);
    }
}
