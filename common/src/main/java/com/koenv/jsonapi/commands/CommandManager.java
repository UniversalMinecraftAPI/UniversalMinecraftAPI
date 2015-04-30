package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.JSONAPIInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all subcommands
 */
public class CommandManager {
    private JSONAPIInterface jsonapi;
    private Map<String, Command> commands = new HashMap<>();

    public CommandManager(JSONAPIInterface jsonapi) {
        this.jsonapi = jsonapi;
    }

    /**
     * Handle an already existing command. Does nothing else than call {@link Command#onCommand(JSONAPIInterface, CommandSource, String[])}
     *
     * @param command The command to execute
     * @param source  The sender of the command
     * @param args    The args, as described in {@link Command#onCommand(JSONAPIInterface, CommandSource, String[])}
     * @see Command#onCommand(JSONAPIInterface, CommandSource, String[])
     */
    public void handle(Command command, CommandSource source, String[] args) {
        command.onCommand(jsonapi, source, args);
    }

    /**
     * Parse and handle a command
     *
     * @param source The sender of the command
     * @param input  The input send by the user, excluding the start command.
     *               For example, `/jsonapi exec this that` would be `{"this", "that"}`
     */
    public void handle(CommandSource source, String[] input) {
        if (input.length < 1) {
            source.sendMessage(ChatColor.RED, "Invalid command.");
            return;
        }
        String command = input[0];
        String[] args = new String[input.length - 1];
        System.arraycopy(input, 1, args, 0, input.length - 1);
        if (commands.containsKey(command)) {
            handle(commands.get(command), source, args);
            return;
        }

        source.sendMessage(ChatColor.RED, "Command not found");
    }

    /**
     * Registers a command
     *
     * @param aliases All aliases that can be used to execute this command
     * @param command The command to register
     */
    public void registerCommand(String[] aliases, Command command) {
        for (String alias : aliases) {
            commands.put(alias, command);
        }
    }

    /**
     * Registers a command with only one alias
     *
     * @param name    The alias that this command can be called with
     * @param command The command to register
     */
    public void registerCommand(String name, Command command) {
        registerCommand(new String[]{name}, command);
    }
}
