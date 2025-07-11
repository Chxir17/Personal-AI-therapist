package com.aitherapist.aitherapist.domain.enums;

public enum Consts {
    START_MESSAGE("Welcome to our application!"),
    UNKNOWN_COMMAND("Unknown command. Please try again.");

    private final String message;

    Consts(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
