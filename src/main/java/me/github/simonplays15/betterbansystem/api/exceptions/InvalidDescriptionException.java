package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

public class InvalidDescriptionException extends RuntimeException {
    public InvalidDescriptionException() {
        super();
    }

    public InvalidDescriptionException(String message) {
        super(message);
    }

    public InvalidDescriptionException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
