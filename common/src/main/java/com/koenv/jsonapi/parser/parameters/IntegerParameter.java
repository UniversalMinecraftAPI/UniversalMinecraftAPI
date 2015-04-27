package com.koenv.jsonapi.parser.parameters;

public class IntegerParameter extends Parameter {
    private int value;

    public IntegerParameter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
