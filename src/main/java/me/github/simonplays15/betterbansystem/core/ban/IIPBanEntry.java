package me.github.simonplays15.betterbansystem.core.ban;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.Date;

/**
 * The interface IIPBanEntry represents an entry in an IP ban list.
 */
public interface IIPBanEntry {

    /**
     * Retrieves the IP address associated with the ban entry.
     *
     * @return The IP address as a string.
     */
    String ip();

    /**
     * Returns the source of the IP ban entry.
     *
     * @return the source of the IP ban entry
     */
    String source();

    /**
     * Returns the date when the entry was created.
     *
     * @return the date when the entry was created
     */
    Date created();

    /**
     * Returns the expiration date/time of the ban entry.
     *
     * @return The expiration date/time of the ban entry.
     */
    Object expires();

    /**
     * Returns the reason associated with this IP ban entry.
     *
     * @return the reason
     */
    String reason();

}
