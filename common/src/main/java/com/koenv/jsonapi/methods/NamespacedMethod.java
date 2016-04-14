package com.koenv.jsonapi.methods;

import java.lang.reflect.Method;

/**
 * A saved API method that does not operate on objects.
 */
public class NamespacedMethod extends AbstractMethod {
    private String namespace;

    public NamespacedMethod(String namespace, String name, Method javaMethod) {
        super(name, javaMethod);
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }
}
