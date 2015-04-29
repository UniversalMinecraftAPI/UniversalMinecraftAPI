package com.koenv.jsonapi.parser.expressions;

public class DoubleExpression extends NumberExpression {
    private double value;

    public DoubleExpression(double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }
}
