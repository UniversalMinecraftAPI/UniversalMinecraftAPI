package com.koenv.jsonapi;

import com.koenv.jsonapi.commands.CommandManager;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;

/**
 * The interface of the JSONAPI, so it can be used in tests.
 */
public interface JSONAPIInterface {
    /**
     * Sets up the JSONAPIInterface by creating all required objects etc. Should usually be called when the implementation is enabled.
     */
    void setup();

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
}
