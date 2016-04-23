package com.koenv.universalminecraftapi;

import com.koenv.universalminecraftapi.commands.*;
import com.koenv.universalminecraftapi.config.UniversalMinecraftAPIRootConfiguration;
import com.koenv.universalminecraftapi.http.RequestHandler;
import com.koenv.universalminecraftapi.http.UniversalMinecraftAPIWebServer;
import com.koenv.universalminecraftapi.methods.MethodInvoker;
import com.koenv.universalminecraftapi.parser.ExpressionParser;
import com.koenv.universalminecraftapi.serializer.DefaultSerializers;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.streams.StreamManager;
import com.koenv.universalminecraftapi.streams.StreamMethods;
import com.koenv.universalminecraftapi.users.UserManager;
import com.koenv.universalminecraftapi.users.UserMethods;
import com.koenv.universalminecraftapi.users.encoders.PlainTextEncoder;
import com.koenv.universalminecraftapi.util.GenericServerMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main UniversalMinecraftAPI delegate which must be called in implementations.
 */
public class UniversalMinecraftAPI implements UniversalMinecraftAPIInterface {
    private static UniversalMinecraftAPI INSTANCE;

    public static UniversalMinecraftAPIInterface getInstance() {
        return INSTANCE;
    }

    private static final Logger logger = LoggerFactory.getLogger(UniversalMinecraftAPI.class);

    private UniversalMinecraftAPIProvider provider;

    private UniversalMinecraftAPIRootConfiguration configuration;

    private ExpressionParser expressionParser;
    private MethodInvoker methodInvoker;
    private CommandManager commandManager;
    private SerializerManager serializerManager;
    private RequestHandler requestHandler;
    private StreamManager streamManager;
    private UserManager userManager;

    private UniversalMinecraftAPIWebServer webServer;

    public UniversalMinecraftAPI(UniversalMinecraftAPIProvider provider) {
        this.provider = provider;
        INSTANCE = this;
    }

    @Override
    public void setup(UniversalMinecraftAPIRootConfiguration configuration) {
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

        webServer = new UniversalMinecraftAPIWebServer(this);

        methodInvoker.registerMethods(this);
        methodInvoker.registerMethods(provider);
        methodInvoker.registerMethods(StreamMethods.class);
        methodInvoker.registerMethods(UserMethods.class);
        methodInvoker.registerMethods(UniversalMinecraftAPIMethods.class);
        methodInvoker.registerMethods(GenericServerMethods.class);

        commandManager.registerCommand(new String[]{"exec", "execute"}, new ExecuteCommand());
        commandManager.registerCommand(new String[]{"createapidoc", "create_api_doc"}, new CreateApiDocCommand());
        commandManager.registerCommand(new String[]{"reload"}, new ReloadCommand());
        commandManager.registerCommand(new String[]{"help"}, new HelpCommand());

        try {
            webServer.start();
        } catch (Exception e) {
            logger.error("Failed to start web server", e);
        }
    }

    @Override
    public void destroy() {
        webServer.stop();
    }

    @Override
    public UniversalMinecraftAPIProvider getProvider() {
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
    public UniversalMinecraftAPIRootConfiguration getConfiguration() {
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