package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;

/**
 * An abstract representation of a command sender (source), to be implemented by each implementation
 */
public interface CommandSource {
    /**
     * Send a non-styled message
     *
     * @param text Message to send
     */
    void sendMessage(String text);

    /**
     * Send a colored message
     *
     * @param color Color of the message
     * @param text  Message to send
     */
    void sendMessage(ChatColor color, String text);

    /**
     * Checks whether this command source has a permission
     *
     * @param permission Permission string
     * @return Whether this command source has the permission
     */
    boolean hasPermission(String permission);
}
