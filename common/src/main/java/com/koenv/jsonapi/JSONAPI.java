package com.koenv.jsonapi;

import com.koenv.jsonapi.commands.*;
import com.koenv.jsonapi.config.JSONAPIRootConfiguration;
import com.koenv.jsonapi.http.JSONAPIWebServer;
import com.koenv.jsonapi.http.RequestHandler;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;
import com.koenv.jsonapi.serializer.DefaultSerializers;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.streams.StreamManager;
import com.koenv.jsonapi.streams.StreamMethods;
import com.koenv.jsonapi.users.UserManager;
import com.koenv.jsonapi.users.UserMethods;
import com.koenv.jsonapi.users.encoders.PlainTextEncoder;
import com.koenv.jsonapi.util.GenericServerMethods;

/**
 * The main JSONAPI delegate which must be called in implementations.
 */
public class JSONAPI implements JSONAPIInterface {
    private static JSONAPI INSTANCE;

    public static JSONAPIInterface getInstance() {
        return INSTANCE;
    }

    private JSONAPIProvider provider;

    private JSONAPIRootConfiguration configuration;

    private ExpressionParser expressionParser;
    private MethodInvoker methodInvoker;
    private CommandManager commandManager;
    private SerializerManager serializerManager;
    private RequestHandler requestHandler;
    private StreamManager streamManager;
    private UserManager userManager;

    private JSONAPIWebServer webServer;

    public JSONAPI(JSONAPIProvider provider) {
        this.provider = provider;
        INSTANCE = this;
    }

    @Override
    public void setup(JSONAPIRootConfiguration configuration) {
        this.configuration = configuration;

        expressionParser = new ExpressionParser();
        methodInvoker = new MethodInvoker();
        commandManager = new CommandManager(this);

        serializerManager = new SerializerManager();
        DefaultSerializers.register(serializerManager);

        requestHandler = new RequestHandler(getExpressionParser(), getMethodInvoker());
        streamManager = new StreamManager();

        userManager = new UserManager();
        userManager.registerEncoder(new PlainTextEncoder());

        webServer = new JSONAPIWebServer(this);

        methodInvoker.registerMethods(this);
        methodInvoker.registerMethods(provider);
        methodInvoker.registerMethods(StreamMethods.class);
        methodInvoker.registerMethods(UserMethods.class);
        methodInvoker.registerMethods(JSONAPIMethods.class);
        methodInvoker.registerMethods(GenericServerMethods.class);

        commandManager.registerCommand(new String[]{"exec", "execute"}, new ExecuteCommand());
        commandManager.registerCommand(new String[]{"createapidoc", "create_api_doc"}, new CreateApiDocCommand());
        commandManager.registerCommand(new String[]{"reload"}, new ReloadCommand());
        commandManager.registerCommand(new String[]{"help"}, new HelpCommand());

        try {
            webServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        webServer.stop();
    }

    @Override
    public JSONAPIProvider getProvider() {
        return provider;
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
    public JSONAPIRootConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    @Override
    public StreamManager getStreamManager() {
        return streamManager;
    }

    @Override
    public UserManager getUserManager() {
        return userManager;
    }
}