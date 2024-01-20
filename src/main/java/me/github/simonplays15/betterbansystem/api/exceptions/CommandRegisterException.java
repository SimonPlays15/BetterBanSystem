package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.command.BaseCommand;

/**
 * Custom exception thrown when an error occurs while registering a command.
 */
public class CommandRegisterException extends RuntimeException {

    /**
     * This exception is thrown when an error occurs while registering a command.
     */
    public CommandRegisterException() {
        super();
    }

    /**
     *
     */
    public CommandRegisterException(String message) {
        super(message);
    }

    /**
     * Exception thrown when a command fails to register.
     */
    public CommandRegisterException(BaseCommand command) {
        this("Failed to register " + command.getCommandName());
    }

    /**
     * This exception is thrown when there is an error registering a command.
     */
    public CommandRegisterException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
