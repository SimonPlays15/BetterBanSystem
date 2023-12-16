/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.permissions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class DefaultPermissionsHandler extends PermissionsManager {

    public DefaultPermissionsHandler() {
    }

    private boolean lookUp(String playerName, String permission) {

        if (Bukkit.getPlayer(playerName) != null) {
            return Objects.requireNonNull(Bukkit.getPlayer(playerName)).hasPermission(permission);
        }

        return false;
    }

    @Override
    public boolean hasPermission(String playername, String permission) {
        return lookUp(playername, permission);
    }

    @Override
    public boolean hasPermission(Player player, String permission) {
        return lookUp(player.getName(), permission);
    }

    /**
     * @param player     {@link OfflinePlayer}
     * @param permission String
     * @return boolean
     * @apiNote Default {@link PermissionsManager} cannot look up {@link OfflinePlayer} for {@link org.bukkit.permissions.Permission} | Looking if {@link OfflinePlayer#hasPlayedBefore()} and {@link OfflinePlayer#isOp()}
     */
    @Override
    public boolean hasPermission(OfflinePlayer player, String permission) {
        return player.hasPlayedBefore() && player.isOp();
    }
}
