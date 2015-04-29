package com.koenv.jsonapi.methods;

public class NamespacedMethod extends Method {
    private String namespace;

    public NamespacedMethod(String namespace, String name, java.lang.reflect.Method javaMethod) {
        super(name, javaMethod);
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }
}
