package com.koenv.jsonapi.parser.exceptions;

import com.koenv.jsonapi.parser.ParseException;

public class MethodParseException extends ParseException {
    public MethodParseException(String method) {
        super("Unable to parse method " + method);
    }
}
