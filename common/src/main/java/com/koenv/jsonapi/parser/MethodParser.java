package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.expressions.*;
import com.koenv.jsonapi.util.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodParser {
    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*");
    private static final Pattern METHOD_PATTERN = Pattern.compile("^(\\w+)\\s*\\(");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[0-9]+");
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("^[0-9]+\\.[0-9]+");
    private static final Pattern STRING_PATTERN = Pattern.compile("^\"(?:\\\\.|[^\"\\\\])*\"");
    private static final Pattern SEPARATOR_PATTERN = Pattern.compile("^\\.");

    public Expression parse(String string) throws ParseException {
        Counter counter = new Counter();
        Expression expression = parseExpression(string, counter).expression;
        if (counter.count() != 0) {
            throw new ParseException("Invalid number of parentheses");
        }
        return expression;
    }

    private ExpressionResult parseExpression(String string, Counter counter) throws ParseException {
        string = string.trim();
        Matcher matcher = DOUBLE_PATTERN.matcher(string);
        if (matcher.find()) {
            string = matcher.replaceFirst("");
            return new ExpressionResult(string, parseDoubleExpression(matcher.group(0)));
        }
        matcher = INTEGER_PATTERN.matcher(string);
        if (matcher.find()) {
            string = matcher.replaceFirst("");
            return new ExpressionResult(string, parseIntegerExpression(matcher.group(0)));
        }
        matcher = STRING_PATTERN.matcher(string);
        if (matcher.find()) {
            string = matcher.replaceFirst("");
            return new ExpressionResult(string, parseStringExpression(matcher.group(0)));
        }

        return parseChainedMethodCallExpression(string, counter);
    }

    public ExpressionResult parseChainedMethodCallExpression(String string, Counter counter) throws ParseException {
        List<Expression> expressions = new ArrayList<>();
        for (int i = 0; !string.isEmpty(); i++) {
            string = string.trim();
            if (SEPARATOR_PATTERN.matcher(string).find()) {
                string = string.substring(1);
                continue;
            }
            Matcher matcher = METHOD_PATTERN.matcher(string);
            if (matcher.find()) {
                ExpressionResult expressionResult = parseMethod(string, counter);
                expressions.add(expressionResult.expression);
                string = expressionResult.string;
                continue;
            }

            if (i == 0) {
                Matcher namespaceMatcher = NAMESPACE_PATTERN.matcher(string);
                if (namespaceMatcher.find()) {
                    expressions.add(parseNamespace(namespaceMatcher.group(0)));
                    string = string.substring(namespaceMatcher.group(0).length());
                    continue;
                }
            }

            if (string.startsWith(",")) {
                break;
            }

            if (string.startsWith(")")) {
                break;
            }

            throw new ParseException("Invalid expression: " + string);
        }

        if (expressions.size() == 1 && expressions.get(0) instanceof NamespaceExpression) {
            throw new ParseException("Namespace without method: " + string);
        }

        return new ExpressionResult(string, new ChainedMethodCallExpression(expressions));
    }

    private NamespaceExpression parseNamespace(String string) throws ParseException {
        return new NamespaceExpression(string);
    }

    private ExpressionResult parseMethod(String string, Counter counter) throws ParseException {
        Matcher matcher = METHOD_PATTERN.matcher(string);
        if (!matcher.find()) {
            throw new ParseException("Unable to parse method " + string);
        }
        counter.increment();
        String methodName = matcher.group(1);
        string = matcher.replaceFirst("");

        List<Expression> parameters = new ArrayList<>();

        while (!string.isEmpty()) {
            string = string.trim();
            if (string.startsWith(")")) {
                counter.decrement();
                string = string.substring(1);
                break;
            }
            if (string.startsWith(",")) {
                string = string.substring(1);
                continue;
            }
            ExpressionResult expressionResult = parseExpression(string, counter);
            parameters.add(expressionResult.expression);
            string = expressionResult.string;
        }

        return new ExpressionResult(string, new MethodCallExpression(methodName, parameters));
    }

    private IntegerExpression parseIntegerExpression(String string) {
        return new IntegerExpression(Integer.parseInt(string));
    }

    private StringExpression parseStringExpression(String string) {
        return new StringExpression(string.substring(1, string.length() - 1).replaceAll("\\\\", ""));
    }

    private DoubleExpression parseDoubleExpression(String string) {
        return new DoubleExpression(Double.parseDouble(string));
    }

    private class ExpressionResult {
        String string;
        Expression expression;

        ExpressionResult(String string, Expression expression) {
            this.string = string;
            this.expression = expression;
        }
    }
}
