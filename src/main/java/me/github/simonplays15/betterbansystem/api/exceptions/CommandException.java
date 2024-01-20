package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * Exception that is thrown when a command encounters an error or exception.
 */
public class CommandException extends RuntimeException {

    /**
     * Exception that is thrown when a command encounters an error or exception.
     */
    public CommandException() {
        super();
    }

    /**
     * Exception that is thrown when a command encounters an error or exception.
     */
    public CommandException(String message) {
        super(message);
    }

    /**
     * Constructs a new CommandException with the specified detail message and cause.
     *
     * @param msg       the detail message (which is saved for later retrieval by the getMessage() method).
     * @param throwable the cause (which is saved for later retrieval by the getCause() method). A null value is permitted, and indicates that the cause is nonexistent or unknown
     */
    public CommandException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
