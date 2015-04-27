package com.koenv.jsonapi.methods.exception;

public class MethodInvocationException extends Exception {
    public MethodInvocationException() {
    }

    public MethodInvocationException(String message) {
        super(message);
    }

    public MethodInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodInvocationException(Throwable cause) {
        super(cause);
    }

    public MethodInvocationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
