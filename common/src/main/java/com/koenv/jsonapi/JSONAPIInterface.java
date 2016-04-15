package com.koenv.jsonapi;

import com.koenv.jsonapi.commands.CommandManager;
import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.http.RequestHandler;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.streams.StreamManager;
import com.koenv.jsonapi.users.UserManager;

/**
 * The interface of the JSONAPI, so it can be used in tests.
 */
public interface JSONAPIInterface {
    /**
     * Sets up the JSONAPIInterface by creating all required objects etc. Should usually be called when the implementation is enabled.
     */
    void setup(JSONAPIConfiguration configuration);

    /**
     * Call when the JSONAPI is unloaded
     */
    void destroy();

    /**
     * @return The currently in use {@link ExpressionParser}
     */
    ExpressionParser getExpressionParser();

    /**
     * @return The currently in use {@link MethodInvoker}
     */
    MethodInvoker getMethodInvoker();

    /**
     * @return The currently in use {@link CommandManager}
     */
    CommandManager getCommandManager();

    /**
     * @return The currently in use {@link SerializerManager}
     */
    SerializerManager getSerializerManager();

    /**
     * @return The currently in use {@link JSONAPIConfiguration}
     */
    JSONAPIConfiguration getConfiguration();

    /**
     * @return The currently in use {@link RequestHandler}
     */
    RequestHandler getRequestHandler();

    /**
     * @return The currently in use {@link StreamManager}
     */
    StreamManager getStreamManager();

    /**
     * @return The currently in use {@link UserManager}
     */
    UserManager getUserManager();
}
