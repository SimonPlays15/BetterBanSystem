package me.github.simonplays15.betterbansystem.core.permissions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.PermissionManagerLoadException;
import me.github.simonplays15.betterbansystem.api.uuid.UUIDFetcher;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * The CloudNetPermissionsHandler class is a subclass of PermissionsManager that provides
 * a permissions handler implementation for the CloudNet system.
 */
public class CloudNetPermissionsHandler extends PermissionsManager {

    /**
     * The getUserMethod variable stores a reference to the 'getUser' method of the 'CloudPermissionsManagement' class.
     * This method is used to retrieve a user object based on a given UUID.
     *
     * @see CloudNetPermissionsHandler#getUserMethod
     */
    private final Method getUserMethod;
    /**
     * The INSTANCE variable represents an instance of the CloudPermissionsManagement class.
     * It is used as a handler for managing permissions in the CloudNet system.
     * <p>
     * It is a private final variable of type Object.
     */
    private final Object INSTANCE;

    /**
     * The CloudNetPermissionsHandler class is a subclass of PermissionsManager that provides
     * a permissions handler implementation for the CloudNet system.
     */
    public CloudNetPermissionsHandler() throws PermissionManagerLoadException {
        super(PermissionsHandlerType.CLOUDNET);
        try {
            Class<?> cloudnetPermManagement = Class.forName("de.dytanic.cloudnet.ext.cloudperms.CloudPermissionsManagement");
            getUserMethod = cloudnetPermManagement.getMethod("getUser", UUID.class);
            INSTANCE = cloudnetPermManagement.getMethod("getInstance").invoke(null);
        } catch (NoClassDefFoundError | NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                 InvocationTargetException ex) {
            GlobalLogger.getLogger().error("Failed to load CloudNet Manager.");
            throw new PermissionManagerLoadException(ex.getMessage());
        }
    }

    /**
     * Checks if the given player has the specified permission.
     *
     * @param playername the name of the player
     * @param permission the permission to check
     * @return true if the player has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(String playername, String permission) {
        try {
            Object cloudNetPlayer = getUserMethod.invoke(INSTANCE, UUIDFetcher.getUUID(playername));
            Method hasPermissionMethod = cloudNetPlayer.getClass().getMethod("hasPermission", String.class);

            return (boolean) hasPermissionMethod.invoke(cloudNetPlayer, permission);
        } catch (Exception ex) {
            GlobalLogger.getLogger().error("Failed to invoke CloudNet hasPermission method. Return false for safety reasons", ex);
        }
        return false;
    }
}
