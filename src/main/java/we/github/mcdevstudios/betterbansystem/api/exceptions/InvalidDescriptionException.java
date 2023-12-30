/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.exceptions;

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
