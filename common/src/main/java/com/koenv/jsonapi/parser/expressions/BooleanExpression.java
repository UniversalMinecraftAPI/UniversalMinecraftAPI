package com.koenv.jsonapi.parser.expressions;

/**
 * Boolean expression, such as `true` or `false`.
 */
public class BooleanExpression extends ValueExpression {
    private boolean value;

    public BooleanExpression(boolean value) {
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }
}
