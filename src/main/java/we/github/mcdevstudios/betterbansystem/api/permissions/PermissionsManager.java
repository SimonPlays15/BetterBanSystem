/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.permissions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.runtimeservice.RuntimeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PermissionsManager {

    static List<PermissionsHandlerType> availableTypes = new ArrayList<>();

    static {
        Collections.addAll(availableTypes, PermissionsHandlerType.values());
    }

    @Contract(pure = true)
    public PermissionsManager() {
    }

    /**
     * @param type {@link PermissionsHandlerType}
     * @return {@link PermissionsManager}
     */
    public static PermissionsManager getHandler(@NotNull PermissionsHandlerType type) throws Exception {
        PermissionsManager manager = null;

        switch (type) {
            case LUCKPERMS -> manager = new LuckPermsManager();
            case SPIGOT -> manager = new SpigotPermissionsHandler();
            case CLOUDNET -> manager = new CloudNetPermissionsHandler();
            case BUNGEECORD -> manager = new BungeeCordDefaultHandler();
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
            } catch (Exception ex) {
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

}
