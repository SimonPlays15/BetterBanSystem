package me.github.simonplays15.betterbansystem.core.mute;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.Date;
import java.util.UUID;

/**
 * The IMuteEntry interface represents a mute entry in a system.
 * It provides methods to retrieve various details about the mute entry.
 */
public interface IMuteEntry {

    /**
     * Generates a new universally unique identifier (UUID).
     *
     * @return a new UUID
     */
    UUID uuid();

    /**
     * Retrieves the name of the mute entry.
     *
     * @return The name of the mute entry.
     */
    String name();

    /**
     * Returns the source of the mute entry.
     *
     * @return the source of the mute entry
     */
    String source();

    /**
     * Returns the date when the mute entry was created.
     *
     * @return the date when the mute entry was created
     */
    Date created();

    /**
     * Retrieves the expiration information of the mute entry.
     *
     * @return The expiration information of the mute entry.
     */
    Object expires();

    /**
     * Retrieves the reason for the mute entry.
     *
     * @return The reason for the mute entry.
     */
    String reason();

}
