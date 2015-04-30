package com.koenv.jsonapi.parser.expressions;

import java.util.List;

/**
 * Chained method call expression, such as `players.getPlayer("koesie10").getUUID()`.
 */
public class ChainedMethodCallExpression extends Expression {
    private List<Expression> expressions;

    public ChainedMethodCallExpression(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }
}
