package com.koenv.jsonapi.methods;

public class Method {
    private String namespace;
    private String name;

    public Method(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }
}
