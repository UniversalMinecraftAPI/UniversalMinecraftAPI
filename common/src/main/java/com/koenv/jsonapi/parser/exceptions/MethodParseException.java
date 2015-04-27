package com.koenv.jsonapi.parser.exceptions;

public class MethodParseException extends ParseException {
    public MethodParseException(String method) {
        super("Unable to parse method " + method);
    }
}
