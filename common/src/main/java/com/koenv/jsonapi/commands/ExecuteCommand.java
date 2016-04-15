package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.JSONAPIInterface;
import com.koenv.jsonapi.methods.MethodInvocationException;
import com.koenv.jsonapi.parser.ParseException;
import com.koenv.jsonapi.parser.expressions.Expression;

public class ExecuteCommand extends Command {
    @Override
    public boolean onCommand(JSONAPIInterface jsonapi, CommandSource source, String[] args) {
        if (args.length < 1) {
            source.sendMessage(ChatColor.RED, "Please specify the expression.");
            return true;
        }
        StringBuilder execStringBuilder = new StringBuilder();
        for (String arg : args) {
            execStringBuilder.append(arg);
            execStringBuilder.append(" ");
        }
        try {
            Expression expression = jsonapi.getExpressionParser().parse(execStringBuilder.toString());
            Object result = jsonapi.getMethodInvoker().invokeMethod(expression);
            source.sendMessage(ChatColor.GREEN, jsonapi.getSerializerManager().serialize(result).toString());
            return true;
        } catch (ParseException | MethodInvocationException e) {
            source.sendMessage(ChatColor.RED, "Failed to execute command: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public boolean hasPermission(CommandSource commandSource) {
        return commandSource.hasPermission("jsonapi.command.execute");
    }

    @Override
    public String getDescription() {
        return "Execute a JSONAPI expression in the game itself";
    }

    @Override
    public String getUsage() {
        return "<expression...>";
    }
}
