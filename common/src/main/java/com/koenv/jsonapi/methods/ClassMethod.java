package com.koenv.jsonapi.methods;


public class ClassMethod extends Method {
    private Class<?> operatesOn;

    public ClassMethod(Class<?> operatesOn, String name, java.lang.reflect.Method javaMethod) {
        super(name, javaMethod);
        this.operatesOn = operatesOn;
    }

    public Class<?> getOperatesOn() {
        return operatesOn;
    }
}
