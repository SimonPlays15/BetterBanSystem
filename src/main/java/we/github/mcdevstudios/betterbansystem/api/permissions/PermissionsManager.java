/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.permissions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.api.runtimeservice.RuntimeService;

import java.util.List;

public abstract class PermissionsManager {

    private static List<PermissionsHandlerType> availableTypes = List.of(PermissionsHandlerType.values());

    @Contract(pure = true)
    public PermissionsManager() {
    }

    /**
     * @param type {@link PermissionsHandlerType}
     * @return {@link PermissionsManager}
     */
    public static PermissionsManager getHandler(PermissionsHandlerType type) throws Exception {
        if (type == PermissionsHandlerType.BUNGEECORD && RuntimeService.isBungeeCord()) {
            return new BungeeCordDefaultHandler();
        }
        PermissionsManager manager = null;
        switch (type) {
            case LUCKPERMS -> manager = new LuckPermsManager();
            case PERMISSIONSEX -> manager = new PermissionsExHandler();
            case SPIGOT -> manager = new SpigotPermissionsHandler();
            case CLOUDNET -> new CloudNetPermissionsHandler();
        }

        return manager;
    }

    /**
     * @return Available {@link PermissionsManager} | If nothing is available:<br>{@link RuntimeService#isBungeeCord()} ? {@link BungeeCordDefaultHandler}<br>{@link RuntimeService#isSpigot()} ? {@link SpigotPermissionsHandler}
     * @see PermissionsHandlerType
     */
    public static @Nullable PermissionsManager getAvailableManager() {
        for (PermissionsHandlerType type : availableTypes) {
            GlobalLogger.getLogger().debug("Trying " + type.name() + " as PermissionManager...");
            try {
                GlobalLogger.getLogger().debug("Trying " + type.name() + " as PermissionManager...OK");
                return getHandler(type);
            } catch (Exception ex) {
                GlobalLogger.getLogger().debug("Trying " + type.name() + " as PermissionManager...FAILED");
                availableTypes.remove(type);
                return getAvailableManager();
            }
        }
        availableTypes = List.of(PermissionsHandlerType.values());
        if (RuntimeService.isBungeeCord()) {
            return new BungeeCordDefaultHandler();
        }
        return new SpigotPermissionsHandler();
    }

    /**
     * @param playername String
     * @param permission String
     * @return boolean
     */
    public abstract boolean hasPermission(String playername, String permission);

}
