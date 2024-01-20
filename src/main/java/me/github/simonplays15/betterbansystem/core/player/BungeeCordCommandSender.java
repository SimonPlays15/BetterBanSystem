package me.github.simonplays15.betterbansystem.core.player;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.NotImplementedException;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.command.CommandSenderType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

/**
 * The BungeeCordCommandSender class extends the BaseCommandSender class and represents a BungeeCord command sender.
 * It provides methods for sending messages, checking permissions, and getting the name and type of the sender.
 */
public class BungeeCordCommandSender extends BaseCommandSender implements CommandSender {

    /**
     *
     */
    private final CommandSender base;
    /**
     *
     */
    private final CommandSenderType type;

    /**
     * The BungeeCordCommandSender class represents a command sender in the BungeeCord API. It extends the BaseCommandSender abstract class.
     */
    public BungeeCordCommandSender(CommandSender sender) {
        this.base = sender;
        if (sender instanceof ProxiedPlayer)
            this.type = CommandSenderType.PLAYER;
        else
            this.type = CommandSenderType.CONSOLE;
    }

    /**
     * BungeeCordCommandSender represents a command sender in the BungeeCord API. It extends the BaseCommandSender class and provides methods for sending messages, checking permissions
     * , getting the name of the sender, and determining the type of sender.
     */
    public BungeeCordCommandSender(Object sender) {
        this.base = (CommandSender) sender;
        if (sender instanceof ProxiedPlayer)
            this.type = CommandSenderType.PLAYER;
        else
            this.type = CommandSenderType.CONSOLE;
    }


    /**
     * Creates a BungeeCordCommandSender object from the given sender.
     *
     * @param sender the CommandSender object
     * @return the BungeeCordCommandSender object
     * @throws IllegalArgumentException if the sender type is not supported
     */
    public static @NotNull BungeeCordCommandSender of(Object sender) {
        if (sender instanceof CommandSender)
            return new BungeeCordCommandSender((CommandSender) sender);

        throw new IllegalArgumentException("Unsupported sender type: " + sender.getClass().getName());
    }

    /**
     * Sends multiple messages to the command sender.
     *
     * @param strings the messages to be sent
     * @throws IllegalArgumentException if any of the messages is null
     */
    @Override
    public void sendMessages(String @NotNull ... strings) {
        for (String string : strings) {
            this.base.sendMessage(TextComponent.fromLegacyText(BetterBanSystem.getInstance().getPrefix() + string));
        }
    }

    /**
     * Overrides the sendMessage method of the BaseCommandSender class to send multiple messages using BaseComponent.
     *
     * @param baseComponents the BaseComponent messages to be sent
     */
    @Override
    public void sendMessage(BaseComponent... baseComponents) {
        for (BaseComponent baseComponent : baseComponents) {
            this.sendMessage(baseComponent);
        }
    }

    /**
     * Sends a message to the command sender.
     *
     * @param baseComponent the BaseComponent to be sent as a message
     * @since version 1.0
     */
    @Override
    public void sendMessage(BaseComponent baseComponent) {
        TextComponent a = new TextComponent();
        a.setText(BetterBanSystem.getInstance().getPrefix() + baseComponent.toLegacyText());
        this.base.sendMessage(a);
    }

    /**
     * Retrieves the groups associated with the command sender.
     *
     * @return a Collection of strings representing the groups associated with the command sender
     */
    @Override
    public Collection<String> getGroups() {
        return this.base.getGroups();
    }

    /**
     * Adds the specified groups to the command sender.
     *
     * @param strings the groups to be added
     */
    @Override
    public void addGroups(String... strings) {
        this.base.addGroups(strings);
    }

    /**
     * Removes the specified groups from the command sender.
     *
     * @param strings the groups to be removed
     */
    @Override
    public void removeGroups(String... strings) {
        this.base.removeGroups(strings);
    }

    /**
     * Sets a permission for the command sender.
     *
     * @param permission the permission to be set
     * @param value      true if the permission is granted, false if it is denied
     */
    @Override
    public void setPermission(String permission, boolean value) {
        this.base.setPermission(permission, value);
    }

    /**
     * Retrieves the permissions of the command sender.
     *
     * @return a Collection of strings representing the permissions of the command sender
     */
    @Override
    public Collection<String> getPermissions() {
        return this.base.getPermissions();
    }

    /**
     * Sends a message to the command sender.
     *
     * @param string the message to be sent
     */
    @Override
    public void sendMessage(String string) {
        this.base.sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + string));
    }

    /**
     * Sends a message or multiple messages to the command sender.
     *
     * @param strings the messages to be sent to the command sender
     */
    @Override
    public void sendMessage(String... strings) {
        this.sendMessages(strings);
    }

    /**
     * Sends a message to the player with the given UUID.
     * If the UUID is null, the message is sent to the console.
     *
     * @param uuid   The UUID of the player to send the message to. Can be null for console.
     * @param string The message to send.
     */
    @Override
    public void sendMessage(UUID uuid, String string) {
        throw new NotImplementedException("Not supported inside the BungeeCord API");
    }

    /**
     * Sends a message or multiple messages to the command sender.
     *
     * @param uuid    the UUID of the recipient
     * @param strings the string message(s) to send
     */
    @Override
    public void sendMessage(UUID uuid, String... strings) {
        throw new NotImplementedException("Not supported inside the BungeeCord API");
    }

    /**
     * Checks if the command sender has the specified permission.
     *
     * @param permission the permission to check
     * @return true if the command sender has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(String permission) {
        return this.base.hasPermission(permission);
    }

    /**
     * Retrieves the name of the command sender.
     *
     * @return the name of the command sender
     */
    @Override
    public String getName() {
        return this.base.getName();
    }

    /**
     * Retrieves the type of the command sender.
     *
     * @return The CommandSenderType representing the type of the command sender.
     */
    @Override
    public CommandSenderType getSenderType() {
        return this.type;
    }
}
