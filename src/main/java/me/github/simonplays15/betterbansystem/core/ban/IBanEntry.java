package me.github.simonplays15.betterbansystem.core.ban;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.Date;
import java.util.UUID;

/**
 * IBanEntry represents a ban entry in a ban list.
 */
public interface IBanEntry {

    /**
     * Generates a new random UUID.
     *
     * @return A new UUID.
     */
    UUID uuid();

    /**
     * Returns the name associated with this ban entry.
     *
     * @return The name associated with this ban entry.
     */
    String name();

    /**
     * Retrieves the source of the ban entry.
     *
     * @return The source of the ban entry as a string.
     */
    String source();

    /**
     * Returns the date when the ban entry was created.
     *
     * @return The date when the ban entry was created.
     */
    Date created();

    /**
     * Retrieves the expiration date of the ban entry.
     *
     * @return The expiration date of the ban entry.
     */
    Object expires();

    /**
     * Retrieves the reason associated with this ban entry.
     *
     * @return The reason for the ban entry.
     */
    String reason();

}
