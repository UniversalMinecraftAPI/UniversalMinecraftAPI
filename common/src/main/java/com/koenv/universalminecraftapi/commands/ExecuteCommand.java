package com.koenv.universalminecraftapi.commands;

import com.koenv.universalminecraftapi.ChatColor;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;
import com.koenv.universalminecraftapi.methods.MethodInvocationException;
import com.koenv.universalminecraftapi.parser.ParseException;
import com.koenv.universalminecraftapi.parser.expressions.Expression;

public class ExecuteCommand extends Command {
    @Override
    public boolean onCommand(UniversalMinecraftAPIInterface uma, CommandSource source, String[] args) {
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
            Expression expression = uma.getExpressionParser().parse(execStringBuilder.toString());
            Object result = uma.getMethodInvoker().invokeMethod(expression);
            source.sendMessage(ChatColor.GREEN, uma.getSerializerManager().serialize(result).toString());
            return true;
        } catch (ParseException | MethodInvocationException e) {
            source.sendMessage(ChatColor.RED, "Failed to execute command: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public boolean hasPermission(CommandSource commandSource) {
        return commandSource.hasPermission("universalminecraftapi.command.execute");
    }

    @Override
    public String getDescription() {
        return "Execute a UniversalMinecraftAPI expression in the game itself";
    }

    @Override
    public String getUsage() {
        return "<expression...>";
    }
}
