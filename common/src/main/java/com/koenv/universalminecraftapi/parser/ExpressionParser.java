package com.koenv.universalminecraftapi.parser;

import com.koenv.universalminecraftapi.parser.expressions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    protected static final Pattern STRING_PATTERN = Pattern.compile("^\"(?:\\\\.|[^\"\\\\\\{\\}])*\"");
    protected static final Pattern ALTERNATE_STRING_PATTERN = Pattern.compile("^'(?:\\\\.|[^\'\\\\\\{\\}])*'");
    protected static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(?i)true|false");
    protected static final Pattern SEPARATOR_PATTERN = Pattern.compile("^\\.");
    protected static final Pattern MAP_PATTERN = Pattern.compile("^\\{");

    /**
     * Parse expressions from a string
     *
     * @param string The expression specified as a string
     * @return The parsed expression.
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    public Expression parse(String string) throws ParseException {
        ParseContext context = new ParseContext();
        Expression expression = parseExpression(string, context).expression;
        if (context.getParenthesesCounter().count() != 0) {
            throw new ParseException("Invalid number of parentheses");
        }
        return expression;
    }

    /**
     * Parses a single expression, recursively parsing any expressions in that expression.
     *
     * @param string  The expression
     * @param context The current parse context
     * @return The parsed expression
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    protected ExpressionResult parseExpression(String string, ParseContext context) throws ParseException {
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
        matcher = ALTERNATE_STRING_PATTERN.matcher(string);
        if (matcher.find()) {
            string = matcher.replaceFirst("");
            return new ExpressionResult(string, parseStringExpression(matcher.group(0)));
        }
        matcher = MAP_PATTERN.matcher(string);
        if (matcher.find()) {
            return parseMap(string, context);
        }

        return parseChainedMethodCallExpression(string, context);
    }

    /**
     * Parses a chained method call, such as `players.getPlayer("koesie10").getUUID()`
     *
     * @param string  The expression
     * @param context The current parse context
     * @return The parsed expression
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    protected ExpressionResult parseChainedMethodCallExpression(String string, ParseContext context) throws ParseException {
        List<Expression> expressions = new ArrayList<>();
        for (int i = 0; !string.isEmpty(); i++) {
            string = string.trim();
            if (SEPARATOR_PATTERN.matcher(string).find()) {
                string = string.substring(1);
                continue;
            }
            Matcher matcher = METHOD_PATTERN.matcher(string);
            if (matcher.find()) {
                ExpressionResult expressionResult = parseMethod(string, context);
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

            if (string.startsWith(",") || string.startsWith(")") || string.startsWith("}")) {
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
     * @param string  The method expression
     * @param context The current parse context
     * @return The parsed expression
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    protected ExpressionResult parseMethod(String string, ParseContext context) throws ParseException {
        Matcher matcher = METHOD_PATTERN.matcher(string);
        if (!matcher.find()) {
            throw new ParseException("Unable to parse method " + string);
        }
        context.getParenthesesCounter().increment();
        String methodName = matcher.group(1);
        string = matcher.replaceFirst("");

        List<Expression> parameters = new ArrayList<>();

        while (!string.isEmpty()) {
            string = string.trim();
            if (string.startsWith(")")) {
                context.getParenthesesCounter().decrement();
                string = string.substring(1);
                break;
            }
            if (string.startsWith(",")) {
                string = string.substring(1);
                continue;
            }
            ExpressionResult expressionResult = parseExpression(string, context);
            parameters.add(expressionResult.expression);
            string = expressionResult.string;
        }

        return new ExpressionResult(string, new MethodCallExpression(methodName, parameters));
    }

    /**
     * Parses a map
     *
     * @param string  The map expression
     * @param context The current parse context
     * @return The parsed expression
     * @throws ParseException Thrown when the expression cannot be parsed
     */
    protected ExpressionResult parseMap(String string, ParseContext context) throws ParseException {
        Matcher matcher = MAP_PATTERN.matcher(string);
        if (!matcher.find()) {
            throw new ParseException("Unable to parse map " + string);
        }
        context.getBracesCounter().increment();
        string = string.substring(1);

        Map<Expression, Expression> map = new HashMap<>();

        Expression key = null;

        while (!string.isEmpty()) {
            string = string.trim();
            if (string.startsWith("}")) {
                context.getBracesCounter().decrement();
                string = string.substring(1);
                break;
            }
            if (string.startsWith(",")) {
                string = string.substring(1);
                continue;
            }
            if (string.startsWith("=")) {
                string = string.substring(1);
                continue;
            }
            ExpressionResult expressionResult = parseExpression(string, context);
            if (key == null) {
                key = expressionResult.expression;
            } else {
                map.put(key, expressionResult.expression);
                key = null;
            }
            string = expressionResult.string;
        }

        return new ExpressionResult(string, new MapExpression(map));
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
