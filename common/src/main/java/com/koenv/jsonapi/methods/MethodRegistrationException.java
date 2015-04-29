package com.koenv.jsonapi.methods;

public class MethodRegistrationException extends RuntimeException {
    public MethodRegistrationException() {
    }

    public MethodRegistrationException(String message) {
        super(message);
    }

    public MethodRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodRegistrationException(Throwable cause) {
        super(cause);
    }

    public MethodRegistrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
