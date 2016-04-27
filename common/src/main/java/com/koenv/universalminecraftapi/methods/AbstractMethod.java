package com.koenv.universalminecraftapi.methods;

import com.koenv.universalminecraftapi.permissions.Permissible;

import java.lang.reflect.Method;

/**
 * A saved API method.
 */
public abstract class AbstractMethod implements Permissible {
    private String name;
    private Method javaMethod;

    public AbstractMethod(String name, Method javaMethod) {
        this.name = name;
        this.javaMethod = javaMethod;
    }

    public String getName() {
        return name;
    }

    public Method getJavaMethod() {
        return javaMethod;
    }
}
