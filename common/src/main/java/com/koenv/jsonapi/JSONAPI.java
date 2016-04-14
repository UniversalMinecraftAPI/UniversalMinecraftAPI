package com.koenv.jsonapi;

import com.koenv.jsonapi.commands.CommandManager;
import com.koenv.jsonapi.commands.CreateApiDocCommand;
import com.koenv.jsonapi.commands.ExecuteCommand;
import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.http.JSONAPIWebServer;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;
import com.koenv.jsonapi.serializer.DefaultSerializers;
import com.koenv.jsonapi.serializer.SerializerManager;

/**
 * The main JSONAPI delegate which must be called in implementations.
 */
public class JSONAPI implements JSONAPIInterface {
    private JSONAPIProvider provider;

    private JSONAPIConfiguration configuration;

    private ExpressionParser expressionParser;
    private MethodInvoker methodInvoker;
    private CommandManager commandManager;
    private SerializerManager serializerManager;

    private JSONAPIWebServer webServer;

    public JSONAPI(JSONAPIProvider provider) {
        this.provider = provider;
    }

    @Override
    public void setup(JSONAPIConfiguration configuration) {
        this.configuration = configuration;

        expressionParser = new ExpressionParser();
        methodInvoker = new MethodInvoker();
        commandManager = new CommandManager(this);
        serializerManager = new SerializerManager();
        DefaultSerializers.register(serializerManager);

        webServer = new JSONAPIWebServer(this, configuration, serializerManager);

        methodInvoker.registerMethods(this);
        methodInvoker.registerMethods(provider);

        commandManager.registerCommand(new String[]{"exec", "execute"}, new ExecuteCommand());
        commandManager.registerCommand(new String[]{"createapidoc", "create_api_doc"}, new CreateApiDocCommand());

        webServer.start();
    }

    @Override
    public ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    @Override
    public MethodInvoker getMethodInvoker() {
        return methodInvoker;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }
}