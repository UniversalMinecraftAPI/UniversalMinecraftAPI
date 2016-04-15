package com.koenv.jsonapi.sponge;

import com.koenv.jsonapi.ChatColor;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class SpongeUtils {
    public static TextColor getTextColor(ChatColor chatColor) {
        switch (chatColor) {
            case RED:
                return TextColors.RED;
            case GREEN:
                return TextColors.GREEN;
            case BLUE:
                return TextColors.BLUE;
            default:
                return TextColors.NONE;
        }
    }
}
