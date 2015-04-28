package com.koenv.jsonapi.parser.expressions;

public class IntegerExpression extends NumberExpression {
    private int value;

    public IntegerExpression(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
