package com.algonix.server.exception;

public class DuplicateResourceException  extends RuntimeException {

    private final String message;

    public DuplicateResourceException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
