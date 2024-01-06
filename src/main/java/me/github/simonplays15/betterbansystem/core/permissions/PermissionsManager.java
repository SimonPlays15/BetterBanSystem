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

public abstract class PermissionsManager {

    static List<PermissionsHandlerType> availableTypes = new ArrayList<>();

    static {
        Collections.addAll(availableTypes, PermissionsHandlerType.values());
    }

    public PermissionsHandlerType handlerType;

    @Contract(pure = true)
    public PermissionsManager(PermissionsHandlerType handlerType) {
        this.handlerType = handlerType;

        GlobalLogger.getLogger().info("Using the following PermissionSystem: " + this.handlerType.name());

    }

    /**
     * @param type {@link PermissionsHandlerType}
     * @return {@link PermissionsManager}
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
