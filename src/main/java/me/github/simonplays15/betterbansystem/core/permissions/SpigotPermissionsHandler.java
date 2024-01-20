package me.github.simonplays15.betterbansystem.core.permissions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * The SpigotPermissionsHandler class is a subclass of PermissionsManager that provides a specific implementation
 * for managing permissions using the Spigot permissions system.
 */
public class SpigotPermissionsHandler extends PermissionsManager {

    /**
     * The SpigotPermissionsHandler class is a subclass of PermissionsManager that provides a specific implementation
     * for managing permissions using the Spigot permissions system.
     */
    public SpigotPermissionsHandler() {
        super(PermissionsHandlerType.SPIGOT);
    }

    /**
     * Checks if a player has the given permission.
     *
     * @param playerName The name of the player.
     * @param permission The permission to check.
     * @return {@code true} if the player has the permission, {@code false} otherwise.
     */
    private boolean lookUp(String playerName, String permission) {

        if (Bukkit.getPlayer(playerName) != null) {
            return Objects.requireNonNull(Bukkit.getPlayer(playerName)).hasPermission(permission);
        }

        return false;
    }

    /**
     * Checks if the specified player has the given permission.
     *
     * @param playername The name of the player.
     * @param permission The permission to check.
     * @return True if the player has the permission, false otherwise.
     */
    @Override
    public boolean hasPermission(String playername, String permission) {
        return lookUp(playername, permission);
    }

    /**
     * Checks if the given player has the specified permission.
     *
     * @param player     The player whose permission is being checked. Must not be null.
     * @param permission The permission to check. Must not be null.
     * @return true if the player has the permission, false otherwise.
     */
    public boolean hasPermission(Player player, String permission) {
        return lookUp(player.getName(), permission);
    }

    /**
     * This method checks if the specified player has the given permission.
     *
     * @param player The player for whom to check the permission.
     * @return True if the player has the permission, false otherwise.
     */
    public boolean hasPermission(OfflinePlayer player) {
        return player.hasPlayedBefore() && player.isOp();
    }
}
