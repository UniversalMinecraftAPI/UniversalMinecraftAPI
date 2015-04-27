package com.koenv.jsonapi.parser.exceptions;

import com.koenv.jsonapi.parser.ParseException;

public class ParameterParseException extends ParseException {
    public ParameterParseException(String parameter) {
        super("Unable to parse parameter: " + parameter);
    }
}
