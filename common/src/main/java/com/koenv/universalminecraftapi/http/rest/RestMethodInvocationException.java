package com.koenv.universalminecraftapi.http.rest;

public class RestMethodInvocationException extends RestException {
    public RestMethodInvocationException(String message) {
        super(message);
    }

    public RestMethodInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
