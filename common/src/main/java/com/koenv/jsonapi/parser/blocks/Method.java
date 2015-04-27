package com.koenv.jsonapi.parser.blocks;

import com.koenv.jsonapi.parser.parameters.Parameter;

import java.util.List;

public class Method extends Block {
    private String methodName;
    private List<Parameter> parameters;

    public Method(String methodName, List<Parameter> parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
}
