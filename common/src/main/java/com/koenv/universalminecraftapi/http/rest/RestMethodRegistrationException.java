package com.koenv.universalminecraftapi.http.rest;

import java.lang.reflect.Method;

public class RestMethodRegistrationException extends RuntimeException {
    private Method method;

    public RestMethodRegistrationException(String message, Method method) {
        super(method.getName() + ": " + message);
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }
}
