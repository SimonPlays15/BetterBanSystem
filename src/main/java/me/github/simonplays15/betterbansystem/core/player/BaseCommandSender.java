package me.github.simonplays15.betterbansystem.core.player;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.command.CommandSenderType;

import java.util.UUID;

/**
 * The BaseCommandSender class is an abstract class that represents a command sender. It provides methods for sending messages,
 * checking permissions, getting the name of the sender, and determining the type of sender.
 */
public abstract class BaseCommandSender {

    /**
     * The BaseCommandSender class is an abstract class that represents a command sender. It provides methods for sending messages,
     * checking permissions, getting the name of the sender, and determining the type of sender.
     */
    public BaseCommandSender() {
    }

    /**
     * Sends a message to the command sender.
     *
     * @param string the message to be sent
     */
    public abstract void sendMessage(String string);

    /**
     * Sends a message or multiple messages to the command sender.
     *
     * @param strings the messages to be sent to the command sender
     */
    public abstract void sendMessage(String... strings);

    /**
     * Sends a message to the player with the given UUID.
     * If the UUID is null, the message is sent to the console.
     *
     * @param uuid   The UUID of the player to send the message to. Can be null for console.
     * @param string The message to send.
     */
    public abstract void sendMessage(UUID uuid, String string);

    /**
     * Sends a message to the specified UUID recipient with the provided string messages.
     *
     * @param uuid    the UUID of the recipient
     * @param strings the string message(s) to send
     */
    public abstract void sendMessage(UUID uuid, String... strings);

    /**
     * Checks if the command sender has the specified permission.
     *
     * @param permission the permission to check
     * @return true if the command sender has the permission, false otherwise
     */
    public abstract boolean hasPermission(String permission);

    /**
     * Retrieves the name of the command sender.
     *
     * @return the name of the command sender
     */
    public abstract String getName();

    /**
     * Retrieves the type of the command sender.
     *
     * @return The CommandSenderType representing the type of the command sender.
     */
    public abstract CommandSenderType getSenderType();

    /**
     * Checks if the command sender is a console.
     *
     * @return true if the command sender is a console, false otherwise.
     */
    public boolean isConsole() {
        return this.getSenderType() == CommandSenderType.CONSOLE;
    }

    /**
     * Determines whether the command sender is a player.
     *
     * @return true if the command sender is a player, otherwise false.
     */
    public boolean isPlayer() {
        return this.getSenderType() == CommandSenderType.PLAYER;
    }

    /**
     * Checks if the sender type is OTHER.
     *
     * @return true if the sender type is OTHER, false otherwise.
     */
    public boolean isOther() {
        return this.getSenderType() == CommandSenderType.OTHER;
    }

    /**
     * Checks if the command sender is a block.
     *
     * @return true if the command sender is a block, false otherwise.
     */
    public boolean isBlock() {
        return this.getSenderType() == CommandSenderType.BLOCK;
    }

}
