package me.github.simonplays15.betterbansystem.core.command;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * Enumeration representing the type of command sender.
 */
public enum CommandSenderType {
    /**
     * Represents a player in the game.
     *
     * <p>
     * The PLAYER enum value is used to identify the command sender type as a player.
     * </p>
     */
    PLAYER,
    /**
     * Represents the console as the command sender.
     */
    CONSOLE,
    /**
     * Represents a command sender type other than player, console, or block.
     */
    OTHER,
    /**
     * Represents the BLOCK command sender type.
     */
    BLOCK
}
