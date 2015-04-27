package com.koenv.jsonapi.parser.exceptions;

public class ParameterParseException extends ParseException {
    public ParameterParseException(String parameter) {
        super("Unable to parse parameter: " + parameter);
    }
}
