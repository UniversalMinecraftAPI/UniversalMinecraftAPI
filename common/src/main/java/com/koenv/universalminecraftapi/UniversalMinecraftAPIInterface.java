package com.koenv.universalminecraftapi;

import com.koenv.universalminecraftapi.commands.CommandManager;
import com.koenv.universalminecraftapi.config.UniversalMinecraftAPIRootConfiguration;
import com.koenv.universalminecraftapi.http.RequestHandler;
import com.koenv.universalminecraftapi.http.RestRequestHandler;
import com.koenv.universalminecraftapi.http.rest.RestHandler;
import com.koenv.universalminecraftapi.methods.MethodInvoker;
import com.koenv.universalminecraftapi.parser.ExpressionParser;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.streams.StreamManager;
import com.koenv.universalminecraftapi.users.UserManager;

/**
 * The interface of the UniversalMinecraftAPI, so it can be used in tests.
 */
public interface UniversalMinecraftAPIInterface {
    /**
     * Sets up the UniversalMinecraftAPIInterface by creating all required objects etc. Should usually be called when the implementation is enabled.
     */
    void setup(UniversalMinecraftAPIRootConfiguration configuration);

    /**
     * Call when the UniversalMinecraftAPI is unloaded
     */
    void destroy();

    /**
     * @return The currently in use {@link UniversalMinecraftAPIProvider}
     */
    UniversalMinecraftAPIProvider getProvider();

    /**
     * @return The currently in use {@link ExpressionParser}
     */
    ExpressionParser getExpressionParser();

    /**
     * @return The currently in use {@link MethodInvoker}
     */
    MethodInvoker getMethodInvoker();

    /**
     * @return The currently in use {@link RestHandler}
     */
    RestHandler getRestHandler();

    /**
     * @return The currently in use {@link CommandManager}
     */
    CommandManager getCommandManager();

    /**
     * @return The currently in use {@link SerializerManager}
     */
    SerializerManager getSerializerManager();

    /**
     * @return The currently in use {@link UniversalMinecraftAPIRootConfiguration}
     */
    UniversalMinecraftAPIRootConfiguration getConfiguration();

    /**
     * @return The currently in use {@link RequestHandler}
     */
    RequestHandler getRequestHandler();

    /**
     * @return The currently in use {@link RestRequestHandler}
     */
    RestRequestHandler getRestRequestHandler();

    /**
     * @return The currently in use {@link StreamManager}
     */
    StreamManager getStreamManager();

    /**
     * @return The currently in use {@link UserManager}
     */
    UserManager getUserManager();
}
