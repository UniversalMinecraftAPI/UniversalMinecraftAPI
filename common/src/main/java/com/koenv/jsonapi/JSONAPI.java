package com.koenv.jsonapi;

import com.koenv.jsonapi.commands.CommandManager;
import com.koenv.jsonapi.commands.ExecuteCommand;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;

public class JSONAPI {
    private JSONAPIProvider provider;
    private ExpressionParser expressionParser;
    private MethodInvoker methodInvoker;
    private CommandManager commandManager;

    public JSONAPI(JSONAPIProvider provider) {
        this.provider = provider;
    }

    public void setup() {
        expressionParser = new ExpressionParser();
        methodInvoker = new MethodInvoker();
        commandManager = new CommandManager(this);

        methodInvoker.registerMethods(this);
        methodInvoker.registerMethods(provider);

        commandManager.registerCommand(new String[]{"exec", "execute"}, new ExecuteCommand());
    }

    public ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    public MethodInvoker getMethodInvoker() {
        return methodInvoker;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}