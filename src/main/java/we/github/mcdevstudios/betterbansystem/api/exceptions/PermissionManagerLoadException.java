/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.exceptions;

public class PermissionManagerLoadException extends RuntimeException {

    public PermissionManagerLoadException() {
        super();
    }

    public PermissionManagerLoadException(String message) {
        super(message);
    }

    public PermissionManagerLoadException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
