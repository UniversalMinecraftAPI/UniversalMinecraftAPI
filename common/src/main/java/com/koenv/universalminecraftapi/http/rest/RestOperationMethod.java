package com.koenv.universalminecraftapi.http.rest;

import java.lang.reflect.Method;

public class RestOperationMethod implements IRestMethod {
    private Class<?> operatesOn;
    private String path;
    private RestMethod restMethod;
    private Method javaMethod;

    public RestOperationMethod(Class<?> operatesOn, String path, RestMethod restMethod, Method javaMethod) {
        this.operatesOn = operatesOn;
        this.path = path;
        this.restMethod = restMethod;
        this.javaMethod = javaMethod;
    }

    public Class<?> getOperatesOn() {
        return operatesOn;
    }

    public String getPath() {
        return path;
    }

    public RestMethod getRestMethod() {
        return restMethod;
    }

    public Method getJavaMethod() {
        return javaMethod;
    }
}
