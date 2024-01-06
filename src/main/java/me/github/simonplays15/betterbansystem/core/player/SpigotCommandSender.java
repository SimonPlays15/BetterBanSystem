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

public class SpigotCommandSender extends BaseCommandSender implements CommandSender {
    private final CommandSender base;
    private final CommandSenderType type;

    /**
     * Constructs a new SpigotCommandSender object with the given base CommandSender.
     *
     * @param base The base CommandSender object to wrap with SpigotCommandSender.
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
     * Creates a new SpigotCommandSender object from a CommandSender object.
     *
     * @param sender the CommandSender object to convert
     * @return a new SpigotCommandSender object
     */
    @Contract("_ -> new")
    public static @NotNull SpigotCommandSender of(Object sender) {
        return new SpigotCommandSender((CommandSender) sender);
    }

    /**
     * Returns the type of the command sender.
     *
     * @return The CommandSenderType of the command sender.
     */
    @Override
    public CommandSenderType getSenderType() {
        return this.type;
    }

    public CommandSender getBase() {
        return base;
    }

    /**
     * Sends a message to the command sender.
     * The message will be preceded with the prefix set in BetterBanSystem.
     *
     * @param string the message to be sent
     */
    @Override
    public void sendMessage(@NotNull String string) {
        this.base.sendMessage(BetterBanSystem.getInstance().getPrefix() + string);
    }

    /**
     * Sends a message to the recipient(s).
     *
     * @param strings the message(s) to be sent
     */
    @Override
    public void sendMessage(@NotNull String @NotNull ... strings) {
        for (String string : strings)
            this.sendMessage(string);
    }

    /**
     * Sends a message to the player with the given UUID.
     * If the UUID is null, the message is sent to the console.
     *
     * @param uuid The UUID of the player to send the message to. Can be null for console.
     * @param s    The message to send.
     */
    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
        this.base.sendMessage(uuid, BetterBanSystem.getInstance().getPrefix() + s);
    }

    /**
     * Sends a message to a player identified by the specified UUID.
     *
     * @param uuid    the UUID of the player to send the message to
     * @param strings the messages to be sent
     */
    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        for (String string : strings)
            this.sendMessage(uuid, string);
    }

    /**
     * Retrieves the server object associated with this SpigotCommandSender.
     *
     * @return a Server object representing the server.
     */
    @NotNull
    @Override
    public Server getServer() {
        return this.base.getServer();
    }

    /**
     * Returns the name of the object.
     *
     * @return the name of the object.
     */
    @NotNull
    @Override
    public String getName() {
        return this.base.getName();
    }

    /**
     * Returns the Spigot object associated with this instance.
     *
     * @return the Spigot object
     * @since 1.0.0
     */
    @NotNull
    @Override
    public Spigot spigot() {
        return this.base.spigot();
    }

    /**
     * Checks if a specific permission is set.
     *
     * @param s The permission to check.
     * @return {@code true} if the permission is set, otherwise {@code false}.
     */
    @Override
    public boolean isPermissionSet(@NotNull String s) {
        return this.base.isPermissionSet(s);
    }

    /**
     * Checks if the specified permission is set for this command sender.
     *
     * @param permission the permission to check
     * @return {@code true} if the permission is set, {@code false} otherwise
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
     * @param permission The permission to check
     * @return true if the command sender has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return this.base.hasPermission(permission);
    }

    /**
     * Adds a permission attachment to the command sender.
     *
     * @param plugin the plugin that is adding the attachment
     * @param s      the permission to add
     * @param b      the value of the permission
     * @return the created PermissionAttachment
     */
    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return this.base.addAttachment(plugin, s, b);
    }

    /**
     * Adds a permission attachment for the given plugin.
     *
     * @param plugin The plugin for which the permission attachment is added.
     * @return The created PermissionAttachment.
     */
    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return this.base.addAttachment(plugin);
    }

    /**
     * Adds a permission attachment to the command sender.
     *
     * @param plugin the plugin the attachment is for
     * @param s      the name of the attachment
     * @param b      if the attachment should be automatically removed when the plugin is disabled
     * @param i      the desired priority of the attachment
     * @return the created PermissionAttachment
     */
    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return this.base.addAttachment(plugin, s, b, i);
    }

    /**
     * Adds a permission attachment for the specified plugin and priority level.
     *
     * @param plugin The plugin that adds the attachment
     * @param i      The priority level of the attachment
     * @return The created PermissionAttachment, or null if the addition failed
     */
    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return this.base.addAttachment(plugin, i);
    }

    /**
     * Removes a PermissionAttachment from the command sender.
     *
     * @param permissionAttachment the PermissionAttachment to remove
     */
    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
        this.base.removeAttachment(permissionAttachment);
    }

    /**
     * Recalculates the permissions of the current object.
     * This method overrides the recalculatePermissions() method of the base class.
     * It delegates the responsibility to recalculate the permissions to the base object.
     */
    @Override
    public void recalculatePermissions() {
        this.base.recalculatePermissions();
    }

    /**
     * Retrieves the effective permissions for the object.
     *
     * @return a set of PermissionAttachmentInfo representing the effective permissions
     * @throws NullPointerException if the base object is null
     */
    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.base.getEffectivePermissions();
    }

    /**
     * Checks if the user executing the method is an operator.
     *
     * @return true if the user is an operator, false otherwise.
     */
    @Override
    public boolean isOp() {
        return this.base.isOp();
    }

    /**
     * Sets the operation status of the Base object.
     *
     * @param b the new operation status, true for enabled and false for disabled.
     */
    @Override
    public void setOp(boolean b) {
        this.base.setOp(b);
    }
}
