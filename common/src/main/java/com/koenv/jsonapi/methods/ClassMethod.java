package com.koenv.jsonapi.methods;

import java.lang.reflect.Method;

/**
 * A saved API method that operates on objects.
 */
public class ClassMethod extends AbstractMethod {
    private Class<?> operatesOn;

    public ClassMethod(Class<?> operatesOn, String name, Method javaMethod, boolean invokerPassed) {
        super(name, javaMethod, invokerPassed);
        this.operatesOn = operatesOn;
    }

    public Class<?> getOperatesOn() {
        return operatesOn;
    }
}
