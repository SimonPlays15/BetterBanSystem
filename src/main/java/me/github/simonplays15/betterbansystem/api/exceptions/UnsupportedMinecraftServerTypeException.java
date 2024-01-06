package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

public class UnsupportedMinecraftServerTypeException extends RuntimeException {

    public UnsupportedMinecraftServerTypeException() {
        super();
    }

    public UnsupportedMinecraftServerTypeException(String message) {
        super(message);
    }

    public UnsupportedMinecraftServerTypeException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
