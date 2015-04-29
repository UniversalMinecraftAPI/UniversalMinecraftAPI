package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.expressions.*;

import java.util.List;

public final class MethodPrinter {
    private MethodPrinter() {

    }

    public static String printExpressions(List<Expression> expressions) {
        StringBuilder builder = new StringBuilder();
        for (Expression expression : expressions) {
            builder.append(printExpression(expression));
            builder.append(".");
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

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
        } else {
            return "undefined";
        }
    }

    public static String printChainedMethodCallExpression(ChainedMethodCallExpression chainedMethodCallExpression) {
        return printExpressions(chainedMethodCallExpression.getExpressions());
    }

    public static String printMethodCallExpression(MethodCallExpression methodCallExpression) {
        StringBuilder builder = new StringBuilder();
        builder.append(methodCallExpression.getMethodName());
        builder.append('(');
        for (int i = 0; i < methodCallExpression.getParameters().size(); i++) {
            builder.append(printExpression(methodCallExpression.getParameters().get(i)));
            if (i != methodCallExpression.getParameters().size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(')');
        return builder.toString();
    }

    public static String printNamespaceExpression(NamespaceExpression namespaceExpression) {
        return namespaceExpression.getName();
    }

    public static String printIntegerExpression(IntegerExpression integerExpression) {
        return Integer.toString(integerExpression.getValue());
    }

    public static String printDoubleExpression(DoubleExpression doubleExpression) {
        return Double.toString(doubleExpression.getValue());
    }

    public static String printStringExpression(StringExpression stringExpression) {
        return '"' + stringExpression.getValue() + '"';
    }
}
