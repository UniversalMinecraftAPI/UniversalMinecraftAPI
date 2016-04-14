package com.koenv.jsonapi;

import com.koenv.jsonapi.commands.CommandManager;
import com.koenv.jsonapi.commands.CreateApiDocCommand;
import com.koenv.jsonapi.commands.ExecuteCommand;
import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.http.JSONAPIWebServer;
import com.koenv.jsonapi.http.RequestHandler;
import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.Invoker;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;
import com.koenv.jsonapi.serializer.DefaultSerializers;
import com.koenv.jsonapi.serializer.SerializerManager;

/**
 * The main JSONAPI delegate which must be called in implementations.
 */
public class JSONAPI implements JSONAPIInterface {
    private static JSONAPI INSTANCE;

    public static JSONAPI getInstance() {
        return INSTANCE;
    }

    private JSONAPIProvider provider;

    private JSONAPIConfiguration configuration;

    private ExpressionParser expressionParser;
    private MethodInvoker methodInvoker;
    private CommandManager commandManager;
    private SerializerManager serializerManager;
    private RequestHandler requestHandler;

    private JSONAPIWebServer webServer;

    public JSONAPI(JSONAPIProvider provider) {
        this.provider = provider;
        INSTANCE = this;
    }

    @Override
    public void setup(JSONAPIConfiguration configuration) {
        this.configuration = configuration;

        expressionParser = new ExpressionParser();
        methodInvoker = new MethodInvoker();
        commandManager = new CommandManager(this);
        serializerManager = new SerializerManager();
        DefaultSerializers.register(serializerManager);

        requestHandler = new RequestHandler(getExpressionParser(), getMethodInvoker());

        webServer = new JSONAPIWebServer(this);

        methodInvoker.registerMethods(this);
        methodInvoker.registerMethods(provider);

        commandManager.registerCommand(new String[]{"exec", "execute"}, new ExecuteCommand());
        commandManager.registerCommand(new String[]{"createapidoc", "create_api_doc"}, new CreateApiDocCommand());

        webServer.start();
    }

    @Override
    public void destroy() {
        webServer.stop();
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

    @Override
    public SerializerManager getSerializerManager() {
        return serializerManager;
    }

    @Override
    public JSONAPIConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    @APIMethod(namespace = "jsonapi")
    public static String getInvoker(Invoker invoker) {
        return invoker.toString();
    }
}