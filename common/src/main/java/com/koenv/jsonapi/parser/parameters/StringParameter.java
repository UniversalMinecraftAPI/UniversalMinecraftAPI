package com.koenv.jsonapi.parser.parameters;

public class StringParameter extends Parameter {
    private String value;

    public StringParameter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
