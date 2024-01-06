package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

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
