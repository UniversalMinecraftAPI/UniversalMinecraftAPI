package com.koenv.jsonapi.methods;

public class Method {
    private String name;
    private java.lang.reflect.Method javaMethod;

    public Method(String name, java.lang.reflect.Method javaMethod) {
        this.name = name;
        this.javaMethod = javaMethod;
    }

    public String getName() {
        return name;
    }

    public java.lang.reflect.Method getJavaMethod() {
        return javaMethod;
    }
}
