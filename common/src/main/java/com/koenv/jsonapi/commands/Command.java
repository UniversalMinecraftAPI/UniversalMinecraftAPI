package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.JSONAPIInterface;

/**
 * A subcommand of the main JSONAPI command, such as /jsonapi execute
 */
public abstract class Command {
    /**
     * Called when a command is entered.
     *
     * @param jsonapi       The main class of the current implementation.
     * @param commandSource The sender of the command
     * @param args          The args, excluding the first subcommand arg.
     *                      For example, `/jsonapi exec this that and more` gives `{"this", "that", "and", "more"}`
     * @return Whether the command succeeded
     */
    public abstract boolean onCommand(JSONAPIInterface jsonapi, CommandSource commandSource, String[] args);

    public boolean hasPermission(CommandSource commandSource) {
        return true;
    }
}
