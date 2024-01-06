package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.command.BaseCommand;

public class CommandRegisterException extends RuntimeException {

    public CommandRegisterException() {
        super();
    }

    public CommandRegisterException(String message) {
        super(message);
    }

    public CommandRegisterException(BaseCommand command) {
        this("Failed to register " + command.getCommandName());
    }

    public CommandRegisterException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
