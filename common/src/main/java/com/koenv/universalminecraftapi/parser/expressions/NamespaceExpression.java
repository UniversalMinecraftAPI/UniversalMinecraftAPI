package com.koenv.universalminecraftapi.parser.expressions;

/**
 * A namespace expression, such as `players`.
 */
public class NamespaceExpression extends Expression {
    private String name;

    public NamespaceExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
