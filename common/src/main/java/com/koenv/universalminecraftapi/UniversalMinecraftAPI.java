package com.koenv.universalminecraftapi;

import com.koenv.universalminecraftapi.commands.*;
import com.koenv.universalminecraftapi.config.UniversalMinecraftAPIRootConfiguration;
import com.koenv.universalminecraftapi.http.RequestHandler;
import com.koenv.universalminecraftapi.http.RestRequestHandler;
import com.koenv.universalminecraftapi.http.UniversalMinecraftAPIWebServer;
import com.koenv.universalminecraftapi.http.rest.RestHandler;
import com.koenv.universalminecraftapi.http.rest.RestMethodRegistrationException;
import com.koenv.universalminecraftapi.methods.MethodInvoker;
import com.koenv.universalminecraftapi.methods.MethodRegistrationException;
import com.koenv.universalminecraftapi.parser.ExpressionParser;
import com.koenv.universalminecraftapi.reflection.ParameterConverterManager;
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
    private ParameterConverterManager parameterConverterManager;
    private MethodInvoker methodInvoker;
    private RestHandler restHandler;
    private CommandManager commandManager;
    private SerializerManager serializerManager;
    private RequestHandler requestHandler;
    private RestRequestHandler restRequestHandler;
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
        parameterConverterManager = new ParameterConverterManager();
        methodInvoker = new MethodInvoker(parameterConverterManager);
        restHandler = new RestHandler(parameterConverterManager);
        commandManager = new CommandManager(this);

        serializerManager = new SerializerManager();
        DefaultSerializers.register(serializerManager);

        requestHandler = new RequestHandler(getExpressionParser(), getMethodInvoker());
        restRequestHandler = new RestRequestHandler(restHandler);
        streamManager = new StreamManager();

        userManager = new UserManager();
        userManager.registerEncoder(new PlainTextEncoder());

        webServer = new UniversalMinecraftAPIWebServer(this);

        registerMethods(this);
        registerMethods(provider);
        registerMethods(StreamMethods.class);
        registerMethods(UserMethods.class);
        registerMethods(UniversalMinecraftAPIMethods.class);
        registerMethods(GenericServerMethods.class);

        commandManager.registerCommand(new String[]{"exec", "execute"}, new ExecuteCommand());
        commandManager.registerCommand(new String[]{"createapidoc", "create_api_doc", "cad"}, new CreateApiDocCommand());
        commandManager.registerCommand(new String[]{"reload"}, new ReloadCommand());
        commandManager.registerCommand(new String[]{"help"}, new HelpCommand());

        try {
            webServer.start();
        } catch (Exception e) {
            logger.error("Failed to start web server", e);
        }
    }

    @Override
    public void registerMethods(Class<?> clazz) throws MethodRegistrationException, RestMethodRegistrationException {
        methodInvoker.registerMethods(clazz);
        restHandler.registerClass(clazz);
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
    public RestHandler getRestHandler() {
        return restHandler;
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
    public RestRequestHandler getRestRequestHandler() {
        return restRequestHandler;
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