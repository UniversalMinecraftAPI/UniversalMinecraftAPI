package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.expressions.*;

import java.util.List;

public final class MethodPrinter {
    private MethodPrinter() {

    }

    public static String printBlocks(List<Expression> expressions) {
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
            return printStringParameter((StringExpression) expression);
        } else if (expression instanceof IntegerExpression) {
            return printIntegerParameter((IntegerExpression) expression);
        } else {
            return "undefined";
        }
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

    public static String printIntegerParameter(IntegerExpression integerParameter) {
        return Integer.toString(integerParameter.getValue());
    }

    public static String printStringParameter(StringExpression stringParameter) {
        return '"' + stringParameter.getValue() + '"';
    }
}
