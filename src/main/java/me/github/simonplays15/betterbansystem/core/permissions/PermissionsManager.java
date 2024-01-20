package me.github.simonplays15.betterbansystem.core.permissions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.PermissionManagerLoadException;
import me.github.simonplays15.betterbansystem.api.runtimeservice.RuntimeService;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The PermissionsManager class is an abstract class that provides a base implementation for managing permissions.
 * It contains methods for retrieving and checking permissions for different players and handlers.
 */
public abstract class PermissionsManager {

    /**
     * The availableTypes variable is a static List of PermissionsHandlerType objects.
     * It stores the available types of permissions handlers that can be used in the system.
     *
     * <p>
     * The List is initialized with an empty ArrayList, and additional types can be added to it as needed.
     * </p>
     *
     * <p>
     * The PermissionsHandlerType enum represents the different types of permissions handlers.
     * Each type has its own functionality for managing permissions in the system.
     * </p>
     *
     * <p>
     * Example usage:
     * {@code availableTypes.add(PermissionsHandlerType.SPIGOT);}
     * </p>
     */
    static List<PermissionsHandlerType> availableTypes = new ArrayList<>();

    static {
        Collections.addAll(availableTypes, PermissionsHandlerType.values());
    }

    /**
     * The handlerType field represents the type of permissions handler used by the PermissionsManager class.
     *
     * <p>
     * The PermissionsHandlerType enum is used to identify and select the appropriate handler for managing permissions in the system.
     * The handlerType field is an instance of the PermissionsHandlerType enum, which allows the system to identify and select the
     * appropriate permissions handler.
     * </p>
     *
     * <p>
     * The available types of permissions handlers are defined in the PermissionsHandlerType enum, which includes SPIGOT, LUCKPERMS,
     * BUNGEECORD, CLOUDNET, and VAULT. The DEFAULT_PERMISSION_HANDLING type is also available as a fallback option.
     * </p>
     *
     * <p>
     * Example usage:
     * </p>
     * <pre>
     * PermissionsManager manager = new PermissionsManager(PermissionsHandlerType.SPIGOT);
     * PermissionsHandlerType type = manager.getHandlerType();
     * if (type == PermissionsHandlerType.SPIGOT) {
     *     // SPIGOT handler code
     * } else if (type == PermissionsHandlerType.LUCKPERMS) {
     *     // LUCKPERMS handler code
     * } else if (type == PermissionsHandlerType.BUNGEECORD) {
     *     // BUNGEECORD handler code
     * } else if (type == PermissionsHandlerType.CLOUDNET) {
     *     // CLOUDNET handler code
     * } else if (type == PermissionsHandlerType.VAULT) {
     *     // VAULT handler code
     * } else if (type == PermissionsHandlerType.DEFAULT_PERMISSION_HANDLING) {
     *     // DEFAULT_PERMISSION_HANDLING handler code
     * }
     * </pre>
     *
     * <p>
     * For more information about each permissions handler type, refer to the documentation of the PermissionsHandlerType enum.
     * </p>
     *
     * @see PermissionsHandlerType
     */
    public PermissionsHandlerType handlerType;

    /**
     * The PermissionsManager class is responsible for managing permissions in the system.
     * It uses different types of permissions handlers, defined by the PermissionsHandlerType enum,
     * to perform various operations such as checking if a player has a permission.
     *
     * <p>
     * The PermissionsHandlerType enum represents the available types of permissions handlers.
     * Each type is used to identify and select the appropriate handler for managing permissions.
     * </p>
     *
     * @see PermissionsHandlerType
     */
    @Contract(pure = true)
    public PermissionsManager(PermissionsHandlerType handlerType) {
        this.handlerType = handlerType;

        GlobalLogger.getLogger().info("Using the following PermissionSystem: " + this.handlerType.name());

    }

    /**
     * Returns the PermissionsManager instance for the specified PermissionsHandlerType.
     *
     * @param type The type of permissions handler. Must not be null.
     * @return The PermissionsManager instance for the specified type.
     * @throws PermissionManagerLoadException if there is an error loading the PermissionsManager.
     */
    public static @NotNull PermissionsManager getHandler(@NotNull PermissionsHandlerType type) throws PermissionManagerLoadException {
        PermissionsManager manager = null;
        try {
            switch (type) {
                case LUCKPERMS -> manager = new LuckPermsManager();
                case SPIGOT -> manager = new SpigotPermissionsHandler();
                case CLOUDNET -> manager = new CloudNetPermissionsHandler();
                case BUNGEECORD -> manager = new BungeeCordDefaultHandler();
                case VAULT -> manager = new VaultPermissionsSystem();
            }
        } catch (PermissionManagerLoadException ex) {
            GlobalLogger.getLogger().error("Failed to load the " + type.name() + "PermissionManager class. Using the default permissionshandler now.");
            manager = (RuntimeService.isSpigot() ? new SpigotPermissionsHandler() : new BungeeCordDefaultHandler());
        }

        if (manager == null) {
            GlobalLogger.getLogger().error("Failed to load any of the available Permission Systems.", new RuntimeException("No permission system has been found!"));
            manager = new PermissionsManager(PermissionsHandlerType.DEFAULT_PERMISSION_HANDLING) {
                @Override
                public boolean hasPermission(String playername, String permission) {
                    return false;
                }
            };
        }
        return manager;
    }

    /**
     * Retrieves the available PermissionsManager instance based on the available types.
     * If a suitable handler cannot be found, a default handler is returned based on the runtime environment.
     *
     * @return The available PermissionsManager instance
     * @throws PermissionManagerLoadException If there is an error loading the PermissionManager
     */
    public static @NotNull PermissionsManager getAvailableManager() {
        List<PermissionsHandlerType> toRemove = new ArrayList<>();
        for (PermissionsHandlerType type : availableTypes) {
            if (type == PermissionsHandlerType.SPIGOT || type == PermissionsHandlerType.BUNGEECORD)
                continue;
            try {
                return getHandler(type);
            } catch (PermissionManagerLoadException ex) {
                toRemove.add(type);
            }
        }
        for (PermissionsHandlerType permissionsHandlerType : toRemove) {
            availableTypes.remove(permissionsHandlerType);
        }
        if (RuntimeService.isBungeeCord()) {
            return new BungeeCordDefaultHandler();
        }
        return new SpigotPermissionsHandler();
    }

    /**
     * This method checks if the specified player has the given permission.
     *
     * @param playername The name of the player.
     * @param permission The permission to check.
     * @return True if the player has the permission, false otherwise.
     */
    public abstract boolean hasPermission(String playername, String permission);

    /**
     * Returns a string representation of the PermissionsManager object.
     * <p>
     * This method overrides the toString() method from the Object class.
     * <p>
     * The string representation of the PermissionsManager object is "PermissionsManager{}".
     *
     * @return a string representation of the PermissionsManager object
     */
    @Override
    public String toString() {
        return "PermissionsManager{}";
    }
}
