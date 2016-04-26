package com.koenv.universalminecraftapi.http.rest;

public class RestException extends Exception {

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

}
