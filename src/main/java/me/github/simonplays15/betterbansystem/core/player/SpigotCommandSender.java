package me.github.simonplays15.betterbansystem.core.player;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.command.CommandSenderType;
import org.bukkit.Server;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * SpigotCommandSender is a class that represents a command sender in the Spigot server.
 * It extends the BaseCommandSender class and implements the CommandSender interface.
 * It provides methods for sending messages, checking permissions, getting the name of the sender, and determining the type of sender.
 */
public class SpigotCommandSender extends BaseCommandSender implements CommandSender {
    /**
     *
     */
    private final CommandSender base;
    /**
     *
     */
    private final CommandSenderType type;

    /**
     * SpigotCommandSender is a class that represents a command sender in the Spigot plugin for Bukkit.
     * It extends the BaseCommandSender class and provides additional functionality to identify the type of sender.
     */
    public SpigotCommandSender(CommandSender base) {
        this.base = base;
        if (base instanceof Player)
            this.type = CommandSenderType.PLAYER;
        else if (base instanceof ConsoleCommandSender)
            this.type = CommandSenderType.CONSOLE;
        else if (base instanceof BlockCommandSender)
            this.type = CommandSenderType.BLOCK;
        else
            this.type = CommandSenderType.OTHER;
    }

    /**
     * Constructor for the SpigotCommandSender class.
     * Initializes a new instance of the SpigotCommandSender class with the given base object.
     *
     * @param base The base object of type Object, representing the command sender.
     */
    public SpigotCommandSender(Object base) {
        this.base = (CommandSender) base;
        if (base instanceof Player)
            this.type = CommandSenderType.PLAYER;
        else if (base instanceof ConsoleCommandSender)
            this.type = CommandSenderType.CONSOLE;
        else if (base instanceof BlockCommandSender)
            this.type = CommandSenderType.BLOCK;
        else
            this.type = CommandSenderType.OTHER;
    }

    /**
     * Converts an object to a SpigotCommandSender.
     *
     * @param sender the object to convert
     * @return a new instance of SpigotCommandSender
     */
    @Contract("_ -> new")
    public static @NotNull SpigotCommandSender of(Object sender) {
        return new SpigotCommandSender((CommandSender) sender);
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

    /**
     * Retrieves the base CommandSender associated with this object.
     *
     * @return the base CommandSender object
     */
    public CommandSender getBase() {
        return base;
    }

    /**
     * Sends a message to the command sender with the specified string.
     * The message is prefixed with the prefix retrieved from BetterBanSystem.
     *
     * @param string the string message to send
     */
    @Override
    public void sendMessage(@NotNull String string) {
        this.base.sendMessage(BetterBanSystem.getInstance().getPrefix() + string);
    }

    /**
     * Sends a message or multiple messages to the command sender.
     *
     * @param strings the messages to be sent to the command sender
     * @throws NullPointerException if {@code strings} is {@code null}
     */
    @Override
    public void sendMessage(@NotNull String @NotNull ... strings) {
        for (String string : strings)
            this.sendMessage(string);
    }

    /**
     * Sends a message to the specified player identified by their UUID.
     *
     * @param uuid The UUID of the player to send the message to. Can be null if the message is intended for all players.
     * @param s    The message to send.
     */
    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
        this.base.sendMessage(uuid, BetterBanSystem.getInstance().getPrefix() + s);
    }

    /**
     * This method sends a message or multiple messages to the command sender.
     *
     * @param uuid    The UUID of the command sender to send the message to. Can be null for console.
     * @param strings The messages to be sent to the command sender.
     */
    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        for (String string : strings)
            this.sendMessage(uuid, string);
    }

    /**
     * Returns the server associated with this command sender.
     *
     * @return The server associated with this command sender.
     */
    @NotNull
    @Override
    public Server getServer() {
        return this.base.getServer();
    }

    /**
     * Retrieves the name of the command sender.
     *
     * @return the name of the command sender
     */
    @NotNull
    @Override
    public String getName() {
        return this.base.getName();
    }

    /**
     * Retrieves the Spigot for this command sender.
     *
     * @return the Spigot for this command sender.
     */
    @NotNull
    @Override
    public Spigot spigot() {
        return this.base.spigot();
    }

    /**
     * Determines whether the command sender has the specified permission.
     *
     * @param s the permission to check
     * @return true if the command sender has the permission, false otherwise
     */
    @Override
    public boolean isPermissionSet(@NotNull String s) {
        return this.base.isPermissionSet(s);
    }

    /**
     * Checks if the command sender has the specified permission.
     *
     * @param permission the permission to check
     * @return true if the command sender has the permission, false otherwise
     */
    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return this.base.isPermissionSet(permission);
    }

    /**
     * Checks if the command sender has the specified permission.
     *
     * @param s the permission to check
     * @return true if the command sender has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(@NotNull String s) {
        return this.base.hasPermission(s);
    }

    /**
     * Checks if the command sender has the specified permission.
     *
     * @param permission the permission to check
     * @return true if the command sender has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return this.base.hasPermission(permission);
    }

    /**
     * Adds a permission attachment to the command sender.
     *
     * @param plugin the plugin that owns the attachment
     * @param s      the permission string
     * @param b      the value of the permission
     * @return the PermissionAttachment object representing the added attachment
     */
    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return this.base.addAttachment(plugin, s, b);
    }

    /**
     * Adds a permission attachment for the specified plugin to this command sender.
     *
     * @param plugin the plugin that owns the attachment
     * @return the newly created PermissionAttachment
     */
    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return this.base.addAttachment(plugin);
    }

    /**
     * Adds a permission attachment to this command sender.
     *
     * @param plugin the plugin that owns this attachment
     * @param s      the permission string
     * @param b      the value of the permission
     * @param i      the ticks remaining for the attachment (useful for temporary permissions)
     * @return the permission attachment that was added
     * @nullable if the plugin is null or the permission attachment could not be added
     */
    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return this.base.addAttachment(plugin, s, b, i);
    }

    /**
     * Adds a permission attachment to this sender for the specified plugin with the given priority.
     *
     * @param plugin   The plugin that owns the attachment
     * @param priority The priority of the attachment
     * @return The newly created PermissionAttachment, or null if it was not possible to create one
     */
    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int priority) {
        return this.base.addAttachment(plugin, priority);
    }

    /**
     * Removes a permission attachment from the command sender.
     *
     * @param permissionAttachment the permission attachment to remove
     */
    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
        this.base.removeAttachment(permissionAttachment);
    }

    /**
     * Recalculates the permissions of the command sender.
     *
     * <p>
     * This method delegates the task of recalculating permissions to the {@link CommandSender#recalculatePermissions()} method
     * of the base command sender object.
     * </p>
     */
    @Override
    public void recalculatePermissions() {
        this.base.recalculatePermissions();
    }

    /**
     * Retrieves the effective permissions of the command sender.
     *
     * @return a Set of PermissionAttachmentInfo representing the effective permissions of the command sender
     */
    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.base.getEffectivePermissions();
    }

    /**
     * Determines whether the command sender has operator permissions.
     *
     * @return true if the command sender has operator permissions, false otherwise
     */
    @Override
    public boolean isOp() {
        return this.base.isOp();
    }

    /**
     * Sets the op status of the command sender.
     *
     * @param b the op status to set
     */
    @Override
    public void setOp(boolean b) {
        this.base.setOp(b);
    }
}
