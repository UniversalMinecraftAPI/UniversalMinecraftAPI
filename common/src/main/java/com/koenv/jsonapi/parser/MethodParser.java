package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.exceptions.MethodParseException;
import com.koenv.jsonapi.parser.exceptions.NamespaceParseException;
import com.koenv.jsonapi.parser.exceptions.ParameterParseException;
import com.koenv.jsonapi.parser.exceptions.ParseException;
import com.koenv.jsonapi.parser.expressions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodParser {
    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");
    private static final Pattern METHOD_PATTERN = Pattern.compile("^(\\w+)\\s*\\((.*)\\)$");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^([0-9]+)$");
    private static final Pattern STRING_PATTERN = Pattern.compile("^\"(?:\\\\.|[^\"\\\\])*\"$");

    public List<Expression> parse(String string) throws ParseException {
        return parseChainedMethodCall(string).getExpressions();
    }

    public ChainedMethodCallExpression parseChainedMethodCall(String string) throws ParseException {
        List<Expression> expressions = new ArrayList<>();
        for (String part : string.split("\\.")) {
            expressions.add(parseExpression(part));
        }

        return new ChainedMethodCallExpression(expressions);
    }

    private Expression parseExpression(String string) throws ParseException {
        if (INTEGER_PATTERN.matcher(string).matches()) {
            return parseIntegerExpression(string);
        } else if (STRING_PATTERN.matcher(string).matches()) {
            return parseStringExpression(string);
        } else if (NAMESPACE_PATTERN.matcher(string).matches()) {
            return parseNamespace(string);
        } else if (METHOD_PATTERN.matcher(string).matches()) {
            return parseMethod(string);
        } else {
            throw new ParseException("Invalid expression: " + string);
        }
    }

    private NamespaceExpression parseNamespace(String string) throws NamespaceParseException {
        return new NamespaceExpression(string);
    }

    private MethodCallExpression parseMethod(String string) throws ParseException {
        Matcher matcher = METHOD_PATTERN.matcher(string);
        if (!matcher.matches()) {
            throw new MethodParseException(string);
        }
        String methodName = matcher.group(1);
        String parametersString = matcher.group(2);

        List<Expression> parameters = new ArrayList<>();

        for (String parameterString : parametersString.split(",")) {
            if (parameterString.trim().isEmpty()) {
                continue;
            }
            parameters.add(parseParameter(parameterString));
        }

        return new MethodCallExpression(methodName, parameters);
    }

    private IntegerExpression parseIntegerExpression(String string) {
        return new IntegerExpression(Integer.parseInt(string));
    }

    private StringExpression parseStringExpression(String string) {
        return new StringExpression(string.substring(1, string.length() - 1).replaceAll("\\\\", ""));
    }

    private Expression parseParameter(String string) throws ParseException {
        if (INTEGER_PATTERN.matcher(string).matches()) {
            return parseIntegerExpression(string);
        } else if (STRING_PATTERN.matcher(string).matches()) {
            return parseStringExpression(string);
        } else if (METHOD_PATTERN.matcher(string).matches()) {
            return parseChainedMethodCall(string);
        } else {
            throw new ParameterParseException(string);
        }
    }
}
