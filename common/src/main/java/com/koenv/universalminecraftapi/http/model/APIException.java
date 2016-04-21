package com.koenv.universalminecraftapi.http.model;

import com.koenv.universalminecraftapi.methods.RethrowableException;

public class APIException extends RuntimeException implements RethrowableException {
    private int code;

    public APIException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
