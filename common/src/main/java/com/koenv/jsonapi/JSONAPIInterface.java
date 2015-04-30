package com.koenv.jsonapi;

import com.koenv.jsonapi.commands.CommandManager;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;

/**
 * The interface of the JSONAPI, so it can be used in tests.
 */
public interface JSONAPIInterface {
    void setup();

    ExpressionParser getExpressionParser();

    MethodInvoker getMethodInvoker();

    CommandManager getCommandManager();
}
