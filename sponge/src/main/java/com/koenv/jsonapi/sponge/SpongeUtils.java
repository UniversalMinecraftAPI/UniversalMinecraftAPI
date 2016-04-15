package com.koenv.jsonapi.sponge;

import com.koenv.jsonapi.ChatColor;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class SpongeUtils {
    public static TextColor getTextColor(ChatColor chatColor) {
        switch (chatColor) {
            case AQUA:
                return TextColors.AQUA;
            case BLACK:
                return TextColors.BLACK;
            case BLUE:
                return TextColors.BLUE;
            case DARK_AQUA:
                return TextColors.DARK_AQUA;
            case DARK_BLUE:
                return TextColors.DARK_BLUE;
            case DARK_GRAY:
                return TextColors.DARK_GRAY;
            case DARK_GREEN:
                return TextColors.DARK_GREEN;
            case DARK_PURPLE:
                return TextColors.DARK_PURPLE;
            case DARK_RED:
                return TextColors.DARK_RED;
            case GOLD:
                return TextColors.GOLD;
            case GRAY:
                return TextColors.GRAY;
            case GREEN:
                return TextColors.GREEN;
            case LIGHT_PURPLE:
                return TextColors.LIGHT_PURPLE;
            case RED:
                return TextColors.RED;
            case WHITE:
                return TextColors.WHITE;
            case YELLOW:
                return TextColors.YELLOW;
            default:
                return TextColors.NONE;
        }
    }
}
