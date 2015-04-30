package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.methods.MethodInvocationException;
import com.koenv.jsonapi.parser.ParseException;
import com.koenv.jsonapi.parser.expressions.Expression;

public class ExecuteCommand extends Command {
    @Override
    public boolean onCommand(JSONAPI jsonapi, CommandSource source, String[] args) {
        if (args.length < 1) {
            source.sendMessage(ChatColor.RED, "Please specify the expression.");
            return true;
        }
        StringBuilder execStringBuilder = new StringBuilder();
        for (String arg : args) {
            execStringBuilder.append(arg);
            execStringBuilder.append(" ");
        }
        System.out.println(execStringBuilder.toString());
        try {
            Expression expression = jsonapi.getExpressionParser().parse(execStringBuilder.toString());
            Object result = jsonapi.getMethodInvoker().invokeMethod(expression);
            source.sendMessage(ChatColor.GREEN, String.valueOf(result));
            return true;
        } catch (ParseException | MethodInvocationException e) {
            source.sendMessage(ChatColor.RED, "Failed to execute command: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
}
