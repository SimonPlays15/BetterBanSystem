package me.github.simonplays15.betterbansystem.core.permissions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * The PermissionsHandlerType enum represents the available types of permissions handlers.
 * It is used to identify and select the appropriate handler for managing permissions in the system.
 */
public enum PermissionsHandlerType {

    /**
     * The SPIGOT constant represents the SPIGOT permissions handler type.
     *
     * <p>
     * The SPIGOT permissions handler is used to manage permissions in a SPIGOT server environment.
     * It provides functionality for creating, modifying, and deleting permissions, as well as
     * checking if a player has a specific permission.
     * </p>
     *
     * <p>
     * The SPIGOT permissions handler is one of the available types of permissions handlers in the system.
     * The types are defined in the PermissionsHandlerType enum, which allows for the selection of the appropriate handler.
     * </p>
     *
     * @see PermissionsHandlerType
     */
    SPIGOT,
    /**
     * LUCKPERMS is a class representing the LuckPerms permissions handler.
     * LuckPerms is a permissions management plugin for various Minecraft server platforms.
     * It provides an extensive and flexible system for managing permissions and groups.
     * <p>
     * Usage:
     * Use the static methods and fields provided by the LUCKPERMS class to interact with LuckPerms.
     * <p>
     * Example:
     * ```
     * LUCKPERMS.initialize();
     * LUCKPERMS.createGroup("admins");
     * LUCKPERMS.addPermission("admins", "example.permission");
     * ```
     * <p>
     * Dependencies:
     * LUCKPERMS requires the LuckPerms plugin to be installed on the Minecraft server.
     * <p>
     * More information:
     * For more information about LuckPerms, please refer to the LuckPerms documentation.
     */
    LUCKPERMS,
    /**
     * The BUNGEECORD variable represents the BungeeCord permissions handler type.
     * <p>
     * BungeeCord is a popular proxy server software for Minecraft. It has its own permissions system
     * which can be used to manage permissions for players across multiple connected servers.
     * <p>
     * The BUNGEECORD variable is an instance of the PermissionsHandlerType enum, which allows the system
     * to identify and select the BungeeCord permissions handler.
     * <p>
     * Example usage:
     * PermissionsHandlerType permissionsHandler = BUNGEECORD;
     * System.out.println("Using permissions handler: " + permissionsHandler);
     */
    BUNGEECORD,
    /**
     * CLOUDNET is a constant representing the "CLOUDNET" permissions handler type.
     * It is one of the available types of permissions handlers in the system.
     *
     * @see PermissionsHandlerType
     */
    CLOUDNET,
    /**
     * VAULT is a class in the PermissionsHandlerType enum that represents a permissions handler
     * for managing permissions in the system.
     * <p>
     * It is one of the available types of permissions handlers, along with SPIGOT, LUCKPERMS, BUNGEECORD,
     * CLOUDNET, and DEFAULT_PERMISSION_HANDLING.
     * <p>
     * VAULT can be used to identify and select the appropriate handler for managing permissions within a system.
     */
    VAULT,
    /**
     *
     */
    DEFAULT_PERMISSION_HANDLING

}
