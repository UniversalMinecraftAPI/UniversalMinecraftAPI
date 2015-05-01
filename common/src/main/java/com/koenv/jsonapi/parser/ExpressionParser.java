package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.expressions.*;
import com.koenv.jsonapi.util.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses expressions
 */
public class ExpressionParser {
    protected static final Pattern NAMESPACE_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*");
    protected static final Pattern METHOD_PATTERN = Pattern.compile("^(\\w+)\\s*\\(");
    protected static final Pattern INTEGER_PATTERN = Pattern.compile("^-?[0-9]+");
    protected static final Pattern DOUBLE_PATTERN = Pattern.compile("^-?[0-9]+\\.[0-9]+");
    protected static final Pattern STRING_PATTERN = Pattern.compile("^\"(?:\\\\.|[^\"\\\\])*\"");
    protected static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(?i)true|false");
    protected static final Pattern SEPARATOR_PATTERN = Pattern.compile("^\\.");

    /**
     * Parse expressions from a string
     *
     * @param string The expression specified as a string
     * @return The parsed expression.
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    public Expression parse(String string) throws ParseException {
        Counter bracketsCounter = new Counter();
        Expression expression = parseExpression(string, bracketsCounter).expression;
        if (bracketsCounter.count() != 0) {
            throw new ParseException("Invalid number of parentheses");
        }
        return expression;
    }

    /**
     * Parses a single expression, recursively parsing any expressions in that expression.
     *
     * @param string          The expression
     * @param bracketsCounter A counter for the number of brackets
     * @return The parsed expression
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    protected ExpressionResult parseExpression(String string, Counter bracketsCounter) throws ParseException {
        string = string.trim();
        Matcher matcher = BOOLEAN_PATTERN.matcher(string);
        if (matcher.find()) {
            string = matcher.replaceFirst("");
            return new ExpressionResult(string, parseBooleanExpression(matcher.group(0)));
        }
        matcher = DOUBLE_PATTERN.matcher(string);
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

        return parseChainedMethodCallExpression(string, bracketsCounter);
    }

    /**
     * Parses a chained method call, such as `players.getPlayer("koesie10").getUUID()`
     *
     * @param string          The expression
     * @param bracketsCounter A counter for the number of brackets
     * @return The parsed expression
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    protected ExpressionResult parseChainedMethodCallExpression(String string, Counter bracketsCounter) throws ParseException {
        List<Expression> expressions = new ArrayList<>();
        for (int i = 0; !string.isEmpty(); i++) {
            string = string.trim();
            if (SEPARATOR_PATTERN.matcher(string).find()) {
                string = string.substring(1);
                continue;
            }
            Matcher matcher = METHOD_PATTERN.matcher(string);
            if (matcher.find()) {
                ExpressionResult expressionResult = parseMethod(string, bracketsCounter);
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

    /**
     * Parses a namespace.
     *
     * @param string The namespace expression, such as `players`
     * @return The parsed expression.
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    protected NamespaceExpression parseNamespace(String string) throws ParseException {
        return new NamespaceExpression(string);
    }

    /**
     * Parses a method.
     *
     * @param string          The method expression
     * @param bracketsCounter A counter for the number of brackets
     * @return The parsed expression
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    protected ExpressionResult parseMethod(String string, Counter bracketsCounter) throws ParseException {
        Matcher matcher = METHOD_PATTERN.matcher(string);
        if (!matcher.find()) {
            throw new ParseException("Unable to parse method " + string);
        }
        bracketsCounter.increment();
        String methodName = matcher.group(1);
        string = matcher.replaceFirst("");

        List<Expression> parameters = new ArrayList<>();

        while (!string.isEmpty()) {
            string = string.trim();
            if (string.startsWith(")")) {
                bracketsCounter.decrement();
                string = string.substring(1);
                break;
            }
            if (string.startsWith(",")) {
                string = string.substring(1);
                continue;
            }
            ExpressionResult expressionResult = parseExpression(string, bracketsCounter);
            parameters.add(expressionResult.expression);
            string = expressionResult.string;
        }

        return new ExpressionResult(string, new MethodCallExpression(methodName, parameters));
    }

    /**
     * Parses an integer expression.
     *
     * @param string Integer expression, such as `21`
     * @return The parsed expression
     */
    protected IntegerExpression parseIntegerExpression(String string) {
        return new IntegerExpression(Integer.parseInt(string));
    }

    /**
     * Parses a string expression.
     *
     * @param string String expression, such as `"test"`
     * @return The parsed expression
     */
    protected StringExpression parseStringExpression(String string) {
        return new StringExpression(string.substring(1, string.length() - 1).replaceAll("\\\\", ""));
    }

    /**
     * Parses a double expression.
     *
     * @param string Double expression, such as `12.67`
     * @return The parsed expression
     */
    protected DoubleExpression parseDoubleExpression(String string) {
        return new DoubleExpression(Double.parseDouble(string));
    }

    /**
     * Parses a boolean expression.
     *
     * @param string Boolean expression, `true` will yield true, everything else will yield false
     * @return The parsed expression
     */
    protected BooleanExpression parseBooleanExpression(String string) {
        return new BooleanExpression(string.equalsIgnoreCase("true"));
    }

    /**
     * A result of the expression.
     */
    protected class ExpressionResult {
        /**
         * The remaining string of the expression.
         */
        String string;
        /**
         * The last parsed expression.
         */
        Expression expression;

        ExpressionResult(String string, Expression expression) {
            this.string = string;
            this.expression = expression;
        }
    }
}
