package com.koenv.universalminecraftapi.parser.expressions;

/**
 * A string expression, such as `"test"`.
 */
public class StringExpression extends ValueExpression {
    private String value;

    public StringExpression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
