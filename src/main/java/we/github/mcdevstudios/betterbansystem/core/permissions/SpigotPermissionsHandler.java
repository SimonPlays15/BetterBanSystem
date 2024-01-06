package we.github.mcdevstudios.betterbansystem.core.permissions;

/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SpigotPermissionsHandler extends PermissionsManager {

    public SpigotPermissionsHandler() {
        super(PermissionsHandlerType.SPIGOT);
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

    public boolean hasPermission(Player player, String permission) {
        return lookUp(player.getName(), permission);
    }

    /**
     * @param player {@link OfflinePlayer}
     * @return boolean
     * @apiNote Default {@link PermissionsManager} cannot look up {@link OfflinePlayer} for {@link org.bukkit.permissions.Permission} | Looking if {@link OfflinePlayer#hasPlayedBefore()} and {@link OfflinePlayer#isOp()}
     */
    public boolean hasPermission(OfflinePlayer player) {
        return player.hasPlayedBefore() && player.isOp();
    }
}
