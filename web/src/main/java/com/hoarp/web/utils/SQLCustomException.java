package com.hoarp.web.utils;

public class SQLCustomException extends RuntimeException {
    private String message;

    public SQLCustomException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
