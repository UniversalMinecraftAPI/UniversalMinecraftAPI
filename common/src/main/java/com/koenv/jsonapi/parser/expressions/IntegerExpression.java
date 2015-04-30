package com.koenv.jsonapi.parser.expressions;

/**
 * An integer expression, such as `21`.
 */
public class IntegerExpression extends NumberExpression {
    private int value;

    public IntegerExpression(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
