/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.exceptions;

public class CommandException extends RuntimeException {

    public CommandException() {
        super();
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
