package com.koenv.universalminecraftapi.commands;

import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;

/**
 * A subcommand of the main UniversalMinecraftAPI command, such as /universalminecraftapi execute
 */
public abstract class Command {
    /**
     * Called when a command is entered.
     *
     * @param uma           The main class of the current implementation.
     * @param commandSource The sender of the command
     * @param args          The args, excluding the first subcommand arg.
     *                      For example, `/universalminecraftapi exec this that and more` gives `{"this", "that", "and", "more"}`
     * @return Whether the command succeeded
     */
    public abstract boolean onCommand(UniversalMinecraftAPIInterface uma, CommandSource commandSource, String[] args);

    public boolean hasPermission(CommandSource commandSource) {
        return true;
    }

    public abstract String getDescription();

    public abstract String getUsage();
}
