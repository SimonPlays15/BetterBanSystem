package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * Thrown when an unsupported Minecraft server type is encountered.
 */
public class UnsupportedMinecraftServerTypeException extends RuntimeException {

    /**
     * Exception thrown when an unsupported Minecraft server type is encountered.
     */
    public UnsupportedMinecraftServerTypeException() {
        super();
    }

    /**
     * Constructs a new UnsupportedMinecraftServerTypeException with the specified detail message.
     *
     * @param message the detail message
     */
    public UnsupportedMinecraftServerTypeException(String message) {
        super(message);
    }

    /**
     * Thrown to indicate that an unsupported Minecraft server type has been encountered.
     */
    public UnsupportedMinecraftServerTypeException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
