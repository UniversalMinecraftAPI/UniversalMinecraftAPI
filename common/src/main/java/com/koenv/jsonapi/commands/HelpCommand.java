package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.JSONAPIInterface;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class HelpCommand extends Command {
    @Override
    public boolean onCommand(JSONAPIInterface jsonapi, CommandSource commandSource, String[] args) {
        commandSource.sendMessage("JSONAPI Help");
        commandSource.sendMessage(ChatColor.GREEN, "===================");
        Map<Command, List<Map.Entry<String, Command>>> commands = jsonapi.getCommandManager().getCommands().entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue));

        for (List<Map.Entry<String, Command>> cmd : commands.values()) {
            Command command = cmd.get(0).getValue();

            if (!command.hasPermission(commandSource)) {
                continue;
            }

            StringJoiner aliasJoiner = new StringJoiner("|");
            cmd.stream().map(Map.Entry::getKey).forEach(aliasJoiner::add);

            commandSource.sendMessage(ChatColor.AQUA, "/jsonapi " + aliasJoiner.toString() + " " + command.getUsage());
            commandSource.sendMessage(command.getDescription());
            commandSource.sendMessage(ChatColor.GREEN, "-------------------");
        }

        return true;
    }

    @Override
    public boolean hasPermission(CommandSource commandSource) {
        return commandSource.hasPermission("jsonapi.help");
    }

    @Override
    public String getDescription() {
        return "Show this help page";
    }

    @Override
    public String getUsage() {
        return "";
    }
}
