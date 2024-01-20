package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * The NotImplementedException class is a subclass of RuntimeException,
 * which is thrown to indicate that the code is not implemented yet.
 */
public class NotImplementedException extends RuntimeException {

    /**
     * Exception thrown to indicate that code is not implemented yet.
     */
    public NotImplementedException() {
        super("Code is not implemented yet");
    }

    /**
     * Constructs a new {@code NotImplementedException} with the specified detail message.
     *
     * @param msg the detail message
     */
    public NotImplementedException(String msg) {
        super(msg);
    }

}
