/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.permissions;

import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;

import java.lang.reflect.Method;
import java.util.UUID;

public class CloudNetPermissionsHandler extends PermissionsManager {

    private final Method getUserMethod;
    private final Object INSTANCE;

    public CloudNetPermissionsHandler() throws Exception {
        try {
            Class<?> cloudnetPermManagement = Class.forName("de.dytanic.cloudnet.ext.cloudperms.CloudPermissionsManagement");
            getUserMethod = cloudnetPermManagement.getMethod("getUser", UUID.class);
            INSTANCE = cloudnetPermManagement.getMethod("getInstance").invoke(null);
        } catch (NoClassDefFoundError | NoSuchMethodException ex) {
            GlobalLogger.getLogger().error("Failed to load CloudNet Manager.");
            throw new Exception(ex);
        }
    }

    /**
     * @param playername String
     * @param permission String
     * @return boolean
     * @implSpec {@link UUIDFetcher}
     * @apiNote Converts PlayerName into {@link UUID} 'cause CloudNet only wants uuids <.< | Fetching with {@link UUIDFetcher}
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
