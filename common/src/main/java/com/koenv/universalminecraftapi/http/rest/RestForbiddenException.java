package com.koenv.universalminecraftapi.http.rest;

public class RestForbiddenException extends RestException {
    public RestForbiddenException(String message) {
        super(message);
    }

    public RestForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
