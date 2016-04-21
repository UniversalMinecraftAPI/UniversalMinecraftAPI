package com.koenv.universalminecraftapi.parser.expressions;

import java.util.List;

/**
 * Chained method call expression, such as `players.getPlayer("Koen").getUUID()`.
 */
public class ChainedMethodCallExpression extends Expression {
    private List<Expression> expressions;

    public ChainedMethodCallExpression(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChainedMethodCallExpression that = (ChainedMethodCallExpression) o;

        return expressions != null ? expressions.equals(that.expressions) : that.expressions == null;

    }

    @Override
    public int hashCode() {
        return expressions != null ? expressions.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ChainedMethodCallExpression{" +
                "expressions=" + expressions +
                '}';
    }
}
