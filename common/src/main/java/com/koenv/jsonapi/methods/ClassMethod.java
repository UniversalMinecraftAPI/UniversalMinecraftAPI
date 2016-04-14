package com.koenv.jsonapi.methods;

/**
 * A saved API method that operates on objects.
 */
public class ClassMethod extends AbstractMethod {
    private Class<?> operatesOn;

    public ClassMethod(Class<?> operatesOn, String name, java.lang.reflect.Method javaMethod) {
        super(name, javaMethod);
        this.operatesOn = operatesOn;
    }

    public Class<?> getOperatesOn() {
        return operatesOn;
    }
}
