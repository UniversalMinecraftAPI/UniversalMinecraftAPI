package com.koenv.universalminecraftapi.parser;

import com.koenv.universalminecraftapi.parser.expressions.*;

import java.util.*;

/**
 * Prints expressions, basically the reverse of {@link ExpressionParser}
 */
public final class ExpressionPrinter {
    private ExpressionPrinter() {

    }

    /**
     * Prints a sequence of expressions.
     *
     * @param expressions Expressions to print
     * @return The sequence of expressions
     */
    public static String printExpressions(List<Expression> expressions) {
        StringJoiner joiner = new StringJoiner(".");
        for (Expression expression : expressions) {
            joiner.add(printExpression(expression));
        }

        return joiner.toString();
    }

    /**
     * Prints a single expression.
     *
     * @param expression Expression to print
     * @return The printed expression
     */
    public static String printExpression(Expression expression) {
        if (expression instanceof MethodCallExpression) {
            return printMethodCallExpression((MethodCallExpression) expression);
        } else if (expression instanceof NamespaceExpression) {
            return printNamespaceExpression((NamespaceExpression) expression);
        } else if (expression instanceof StringExpression) {
            return printStringExpression((StringExpression) expression);
        } else if (expression instanceof IntegerExpression) {
            return printIntegerExpression((IntegerExpression) expression);
        } else if (expression instanceof DoubleExpression) {
            return printDoubleExpression((DoubleExpression) expression);
        } else if (expression instanceof ChainedMethodCallExpression) {
            return printChainedMethodCallExpression((ChainedMethodCallExpression) expression);
        } else if (expression instanceof MapExpression) {
            return printMapExpression((MapExpression) expression);
        } else if (expression instanceof ListExpression) {
            return printListExpression((ListExpression) expression);
        } else {
            return "undefined";
        }
    }

    /**
     * Prints a chained method call expression, by passing it to {@link #printExpressions(List)}.
     *
     * @param chainedMethodCallExpression The expression to print
     * @return The printed expression
     */
    public static String printChainedMethodCallExpression(ChainedMethodCallExpression chainedMethodCallExpression) {
        return printExpressions(chainedMethodCallExpression.getExpressions());
    }

    /**
     * Prints method call.
     *
     * @param methodCallExpression The expression to print
     * @return The printed expression
     */
    public static String printMethodCallExpression(MethodCallExpression methodCallExpression) {
        StringJoiner joiner = new StringJoiner(", ");
        methodCallExpression.getParameters().forEach(expression -> {
            joiner.add(printExpression(expression));
        });
        return methodCallExpression.getMethodName() + "(" + joiner.toString() + ")";
    }

    /**
     * Prints a map.
     *
     * @param expression The expression to print
     * @return The printed expression
     */
    public static String printMapExpression(MapExpression expression) {
        StringJoiner joiner = new StringJoiner(", ");
        List<Map.Entry<Expression, Expression>> expressions = new LinkedList<>(expression.getValue().entrySet());
        expression.getValue().forEach((key, value) -> {
            joiner.add(printExpression(key) + " = " + printExpression(value));
        });
        return "{" + joiner.toString() + "}";
    }

    /**
     * Prints a list.
     *
     * @param expression The expression to print
     * @return The printed expression
     */
    public static String printListExpression(ListExpression expression) {
        StringJoiner joiner = new StringJoiner(", ");
        expression.getValue().forEach(item -> {
            joiner.add(printExpression(item));
        });

        return "[" + joiner.toString() + "]";
    }

    /**
     * Prints a namespace expression.
     *
     * @param namespaceExpression The expression to print
     * @return The printed expression
     */
    public static String printNamespaceExpression(NamespaceExpression namespaceExpression) {
        return namespaceExpression.getName();
    }

    /**
     * Prints an integer expression.
     *
     * @param integerExpression The expression to print
     * @return The printed expression
     */
    public static String printIntegerExpression(IntegerExpression integerExpression) {
        return Integer.toString(integerExpression.getValue());
    }

    /**
     * Prints a double expression.
     *
     * @param doubleExpression The expression to print
     * @return The printed expression
     */
    public static String printDoubleExpression(DoubleExpression doubleExpression) {
        return Double.toString(doubleExpression.getValue());
    }

    /**
     * Prints a string expression.
     *
     * @param stringExpression The expression to print
     * @return The printed expression
     */
    public static String printStringExpression(StringExpression stringExpression) {
        return '"' + stringExpression.getValue() + '"';
    }
}
