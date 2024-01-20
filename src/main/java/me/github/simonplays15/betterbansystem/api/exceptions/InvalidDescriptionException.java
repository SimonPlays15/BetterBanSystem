package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * Represents an exception that is thrown when a description is invalid.
 * This exception is a subclass of RuntimeException and does not need to be declared or caught explicitly.
 */
public class InvalidDescriptionException extends RuntimeException {
    /**
     * Represents an exception that is thrown when a description is invalid.
     * This exception is a subclass of RuntimeException and does not need to be declared or caught explicitly.
     */
    public InvalidDescriptionException() {
        super();
    }

    /**
     * Exception indicating an invalid description.
     */
    public InvalidDescriptionException(String message) {
        super(message);
    }

    /**
     * InvalidDescriptionException is thrown when the description provided is invalid.
     * It is a subclass of RuntimeException.
     */
    public InvalidDescriptionException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
