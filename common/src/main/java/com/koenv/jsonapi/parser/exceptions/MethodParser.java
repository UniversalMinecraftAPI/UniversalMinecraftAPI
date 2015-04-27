package com.koenv.jsonapi.parser.exceptions;

import com.koenv.jsonapi.parser.ParseException;
import com.koenv.jsonapi.parser.blocks.Block;
import com.koenv.jsonapi.parser.blocks.Method;
import com.koenv.jsonapi.parser.parameters.IntegerParameter;
import com.koenv.jsonapi.parser.parameters.Parameter;
import com.koenv.jsonapi.parser.parameters.StringParameter;
import com.koenv.jsonapi.parser.blocks.ThisVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodParser {
    private static final Pattern METHOD_PATTERN = Pattern.compile("^(\\w+) *\\(([^\\)]*)\\)$");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^([0-9]+)$");
    private static final Pattern STRING_PATTERN = Pattern.compile("^\"(?:\\\\.|[^\"\\\\])*\"$");

    public List<Block> parse(String method) throws ParseException {
        List<Block> blocks = new ArrayList<>();
        for (String part : method.split("\\.")) {
            if (METHOD_PATTERN.matcher(part).matches()) {
                blocks.add(parseMethod(part));
            } else if (part.equalsIgnoreCase("this")) {
                blocks.add(new ThisVariable());
            } else {
                throw new RuntimeException("Invalid method: " + part);
            }
        }

        return blocks;
    }

    private Method parseMethod(String string) throws MethodParseException, ParameterParseException {
        Matcher matcher = METHOD_PATTERN.matcher(string);
        if (!matcher.matches()) {
            throw new MethodParseException(string);
        }
        String methodName = matcher.group(1);
        String parametersString = matcher.group(2);

        List<Parameter> parameters = new ArrayList<>();

        for (String parameterString : parametersString.split(",")) {
            if (parameterString.trim().isEmpty()) {
                continue;
            }
            parameters.add(parseParameter(parameterString));
        }

        return new Method(methodName, parameters);
    }

    private Parameter parseParameter(String string) throws ParameterParseException {
        if (INTEGER_PATTERN.matcher(string).matches()) {
            return new IntegerParameter(Integer.parseInt(string));
        } else if (STRING_PATTERN.matcher(string).matches()) {
            return new StringParameter(string.substring(1, string.length() - 1).replaceAll("\\\\", ""));
        } else {
            throw new ParameterParseException(string);
        }
    }
}
