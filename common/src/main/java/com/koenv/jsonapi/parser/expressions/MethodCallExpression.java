package com.koenv.jsonapi.parser.expressions;

import java.util.List;

public class MethodCallExpression extends Expression {
    private String methodName;
    private List<Expression> parameters;

    public MethodCallExpression(String methodName, List<Expression> parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Expression> getParameters() {
        return parameters;
    }
}
