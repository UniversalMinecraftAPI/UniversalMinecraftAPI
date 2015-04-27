package com.koenv.jsonapi.parser.expressions;

public class StringExpression extends Expression {
    private String value;

    public StringExpression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
