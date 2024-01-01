/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.exceptions;

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
