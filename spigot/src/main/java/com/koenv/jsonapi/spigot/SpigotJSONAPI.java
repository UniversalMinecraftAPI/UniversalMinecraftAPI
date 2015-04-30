package com.koenv.jsonapi.spigot;

import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.MethodInvocationException;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;
import com.koenv.jsonapi.parser.ParseException;
import com.koenv.jsonapi.parser.expressions.Expression;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotJSONAPI extends JavaPlugin {
    private ExpressionParser expressionParser;
    private MethodInvoker methodInvoker;

    @Override
    public void onEnable() {
        expressionParser = new ExpressionParser();
        methodInvoker = new MethodInvoker();

        methodInvoker.registerMethods(this);

        getCommand("jsonapi").setExecutor((sender, command, label, args) -> {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "This plugin needs at least 1 parameter.");
                return true;
            }
            String subCommand = args[0];
            if (subCommand.equals("execute") || subCommand.equals("exec")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Please specify the command.");
                    return true;
                }
                StringBuilder execStringBuilder = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    if (i == 0) {
                        continue;
                    }
                    execStringBuilder.append(args[i]);
                    execStringBuilder.append(" ");
                }
                try {
                    Expression expression = expressionParser.parse(execStringBuilder.toString());
                    Object result = methodInvoker.invokeMethod(expression);
                    sender.sendMessage(ChatColor.GREEN + String.valueOf(result));
                    return true;
                } catch (ParseException | MethodInvocationException e) {
                    sender.sendMessage(ChatColor.RED + "Failed to execute command: " + e.getMessage());
                    e.printStackTrace();
                    return true;
                }
            }
            return false;
        });
    }

    @APIMethod(namespace = "players")
    public static Player getPlayer(String name) {
        return Bukkit.getServer().getPlayer(name);
    }

    @APIMethod(operatesOn = Player.class)
    public static String getUUID(Player self) {
        return self.getUniqueId().toString();
    }
}