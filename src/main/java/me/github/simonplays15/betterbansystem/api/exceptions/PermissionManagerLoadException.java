package me.github.simonplays15.betterbansystem.api.exceptions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * The PermissionManagerLoadException class represents an exception thrown when there is an error
 * loading the PermissionManager.
 */
public class PermissionManagerLoadException extends RuntimeException {

    /**
     * The PermissionManagerLoadException class represents an exception thrown when there is an error loading the PermissionManager.
     * <p>
     * This exception is a subclass of the RuntimeException class, which means it is an unchecked exception.
     * <p>
     * This class provides an empty constructor that can be used to create an instance of the exception without an error message.
     * It can be thrown wherever a PermissionManagerLoadException is expected to occur.
     * <p>
     * An instance of this exception can be created using the default constructor like this:
     * PermissionManagerLoadException exception = new PermissionManagerLoadException();
     *
     * @since 1.0
     */
    public PermissionManagerLoadException() {
        super();
    }

    /**
     * PermissionManagerLoadException represents an exception thrown when there is an error loading the PermissionManager.
     */
    public PermissionManagerLoadException(String message) {
        super(message);
    }

    /**
     * Constructs a PermissionManagerLoadException with the specified error message and cause.
     *
     * @param msg       the detail message (which is saved for later retrieval by the getMessage() method).
     *                  It should provide information about the cause of the exception.
     * @param throwable the cause (which is saved for later retrieval by the getCause() method).
     *                  A null value is permitted and indicates that the cause is nonexistent or unknown.
     */
    public PermissionManagerLoadException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
