package com.koenv.jsonapi.methods;

public class MethodAccessDeniedException extends RuntimeException {
    public MethodAccessDeniedException() {
    }

    public MethodAccessDeniedException(String message) {
        super(message);
    }

    public MethodAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodAccessDeniedException(Throwable cause) {
        super(cause);
    }

    public MethodAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
