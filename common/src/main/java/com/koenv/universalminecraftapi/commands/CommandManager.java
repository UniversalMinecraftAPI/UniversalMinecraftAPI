package com.koenv.universalminecraftapi.commands;

import com.koenv.universalminecraftapi.ChatColor;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all subcommands
 */
public class CommandManager {
    private UniversalMinecraftAPIInterface uma;
    private Map<String, Command> commands = new HashMap<>();

    public CommandManager(UniversalMinecraftAPIInterface uma) {
        this.uma = uma;
    }

    /**
     * Handle an already existing command. Does nothing else than call {@link Command#onCommand(UniversalMinecraftAPIInterface, CommandSource, String[])}
     *
     * @param command The command to execute
     * @param source  The sender of the command
     * @param args    The args, as described in {@link Command#onCommand(UniversalMinecraftAPIInterface, CommandSource, String[])}
     * @see Command#onCommand(UniversalMinecraftAPIInterface, CommandSource, String[])
     */
    public void handle(Command command, CommandSource source, String[] args) {
        command.onCommand(uma, source, args);
    }

    /**
     * Parse and handle a command
     *
     * @param source The sender of the command
     * @param input  The input send by the user, excluding the start command.
     *               For example, `/universalminecraftapi exec this that` would be `{"this", "that"}`
     */
    public void handle(CommandSource source, String[] input) {
        if (input.length < 1) {
            showHelp(source);
            
            source.sendMessage(ChatColor.RED, "Invalid command.");
            return;
        }
        String command = input[0];
        String[] args = new String[input.length - 1];
        System.arraycopy(input, 1, args, 0, input.length - 1);
        if (commands.containsKey(command)) {
            Command commandObject = commands.get(command);
            if (!commandObject.hasPermission(source)) {
                source.sendMessage(ChatColor.RED, "You don't have permission.");
                return;
            }
            handle(commandObject, source, args);
            return;
        }

        showHelp(source);

        source.sendMessage(ChatColor.RED, "Command not found");
    }

    private void showHelp(CommandSource source) {
        Command helpCommand = commands.get("help");
        if (helpCommand != null && helpCommand.hasPermission(source)) {
            handle(helpCommand, source, new String[]{});
        }
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

    /**
     * Returns all registered commands
     *
     * @return An unmodifiable map of all registered commands in which a value can be seen multiple times
     */
    public Map<String, Command> getCommands() {
        return Collections.unmodifiableMap(commands);
    }
}
