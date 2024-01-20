package me.github.simonplays15.betterbansystem.core.warn;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.List;
import java.util.UUID;

/**
 * The IWarnEntry interface represents a warning entry.
 */
public interface IWarnEntry {
    /**
     * Generates a new UUID.
     *
     * @return The generated UUID.
     */
    UUID uuid();

    /**
     * Returns the name of the warning entry.
     *
     * @return the name of the warning entry
     */
    String name();

    /**
     * Retrieves the list of warn entries.
     *
     * @return the list of warn entries
     */
    List<Warn> warns();

    /**
     * Adds a warning to the warn entry.
     *
     * @param warn the warning to be added
     */
    void addWarn(Warn warn);

    /**
     * Removes a warning with the specified ID.
     *
     * @param id the ID of the warning to remove
     * @return true if the warning is successfully removed, false otherwise
     */
    boolean removeWarn(int id);
}
