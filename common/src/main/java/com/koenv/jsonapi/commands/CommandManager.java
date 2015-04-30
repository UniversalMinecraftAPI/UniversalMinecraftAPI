package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.JSONAPIInterface;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private JSONAPIInterface jsonapi;
    private Map<String, Command> commands = new HashMap<>();

    public CommandManager(JSONAPIInterface jsonapi) {
        this.jsonapi = jsonapi;
    }

    public void handle(Command command, CommandSource source, String[] args) {
        command.onCommand(jsonapi, source, args);
    }

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

    public void registerCommand(String[] aliases, Command command) {
        for (String alias : aliases) {
            commands.put(alias, command);
        }
    }

    public void registerCommand(String name, Command command) {
        registerCommand(new String[]{name}, command);
    }
}
