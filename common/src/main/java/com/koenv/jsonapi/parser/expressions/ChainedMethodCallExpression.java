package com.koenv.jsonapi.parser.expressions;

import java.util.List;

public class ChainedMethodCallExpression extends Expression {
    private List<Expression> expressions;

    public ChainedMethodCallExpression(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }
}
