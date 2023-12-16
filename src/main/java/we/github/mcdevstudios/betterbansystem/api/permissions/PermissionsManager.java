/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.permissions;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import we.github.mcdevstudios.betterbansystem.BetterBanSystem;

public abstract class PermissionsManager {

    public PermissionsManager() {
    }

    /**
     * @param type {@link PermissionsHandlerType}
     * @return {@link PermissionsManager}
     */
    public static PermissionsManager getHandler(PermissionsHandlerType type) throws Exception {
        PermissionsManager manager = null;
        switch (type) {
            case LUCKPERMS -> manager = new LuckPermsManager();
            case PERMISSIONSEX -> manager = new PermissionsExHandler();
            case DEFAULT -> manager = new DefaultPermissionsHandler();
        }

        return manager;
    }

    public static PermissionsManager getAvailableManager() {
        try {
            return new PermissionsExHandler();
        } catch (Exception ex) {
            BetterBanSystem.getGlobalLogger().debug("Failed to load PermissionsExHandler ... trying LuckPerms now", ex.getCause());
        }

        try {
            return new LuckPermsManager();
        } catch (Exception ex) {
            BetterBanSystem.getGlobalLogger().debug("Failed to load LuckPermsManager ... falling back to default PermissionsHandler", ex.getCause());
        }

        return new DefaultPermissionsHandler();
    }

    /**
     * @param playername String
     * @param permission String
     * @return boolean
     */
    public abstract boolean hasPermission(String playername, String permission);

    /**
     * @param player     {@link Player}
     * @param permission String
     * @return boolean
     */
    public abstract boolean hasPermission(Player player, String permission);

    /**
     * @param player     {@link OfflinePlayer}
     * @param permission String
     * @return boolean
     */
    public abstract boolean hasPermission(OfflinePlayer player, String permission);

}
