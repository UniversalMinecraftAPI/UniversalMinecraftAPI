package com.koenv.jsonapi.parser.expressions;

public class NamespaceExpression extends Expression {
    private String name;

    public NamespaceExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
