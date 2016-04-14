package com.koenv.jsonapi.methods;

import java.lang.reflect.Method;

/**
 * A saved API method.
 */
public abstract class AbstractMethod {
    private String name;
    private Method javaMethod;
    private boolean invokerPassed;

    public AbstractMethod(String name, Method javaMethod, boolean invokerPassed) {
        this.name = name;
        this.javaMethod = javaMethod;
        this.invokerPassed = invokerPassed;
    }

    public String getName() {
        return name;
    }

    public Method getJavaMethod() {
        return javaMethod;
    }

    public boolean isInvokerPassed() {
        return invokerPassed;
    }
}
