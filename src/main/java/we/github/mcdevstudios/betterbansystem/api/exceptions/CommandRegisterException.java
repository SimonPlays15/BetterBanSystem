/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.exceptions;

import we.github.mcdevstudios.betterbansystem.api.command.BaseCommand;

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
