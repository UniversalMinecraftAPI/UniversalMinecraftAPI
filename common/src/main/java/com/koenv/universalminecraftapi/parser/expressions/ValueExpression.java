package com.koenv.universalminecraftapi.parser.expressions;

/**
 * A simple value expression with a constant value.
 */
public abstract class ValueExpression extends Expression {
    public abstract Object getValue();
}
