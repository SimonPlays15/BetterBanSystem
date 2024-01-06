package we.github.mcdevstudios.betterbansystem.core.permissions;

/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.PermissionManagerLoadException;
import we.github.mcdevstudios.betterbansystem.api.runtimeservice.RuntimeService;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PermissionsManager {

    static List<PermissionsHandlerType> availableTypes = new ArrayList<>();

    static {
        Collections.addAll(availableTypes, PermissionsHandlerType.values());
    }

    public PermissionsHandlerType handlerType;

    @Contract(pure = true)
    public PermissionsManager(PermissionsHandlerType handlerType) {
        this.handlerType = handlerType;
    }

    /**
     * @param type {@link PermissionsHandlerType}
     * @return {@link PermissionsManager}
     */
    public static PermissionsManager getHandler(@NotNull PermissionsHandlerType type) throws PermissionManagerLoadException {
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
     * @return Available {@link PermissionsManager} | If nothing is available:<br>{@link RuntimeService#isBungeeCord()} ? {@link BungeeCordDefaultHandler}<br>{@link RuntimeService#isSpigot()} ? {@link SpigotPermissionsHandler}
     * @see PermissionsHandlerType
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
     * @param playername String
     * @param permission String
     * @return boolean
     */
    public abstract boolean hasPermission(String playername, String permission);

    @Override
    public String toString() {
        return "PermissionsManager{}";
    }
}
