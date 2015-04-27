package com.koenv.jsonapi.parser.exceptions;

public class NamespaceParseException extends ParseException {
    public NamespaceParseException(String namespace) {
        super("Failed to parse namespace " + namespace);
    }
}
