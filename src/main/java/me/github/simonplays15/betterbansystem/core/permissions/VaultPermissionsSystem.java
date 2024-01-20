package me.github.simonplays15.betterbansystem.core.permissions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.PermissionManagerLoadException;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * The VaultPermissionsSystem class is a concrete implementation of the PermissionsManager abstract class.
 * It provides functionality for managing permissions using the Vault permissions system.
 */
public class VaultPermissionsSystem extends PermissionsManager {

    /**
     * The vaultPerms variable represents a specific permission handler used by the VaultPermissionsSystem class.
     * It is a private final field of type Permission.
     *
     * <p>
     * The Permission class is a class from the Bukkit API that provides functionality for managing permissions.
     * </p>
     *
     * <p>
     * The vaultPerms variable is only accessible within the VaultPermissionsSystem class and is initialized within its constructor.
     * </p>
     *
     * <p>
     * Example usage:
     * </p>
     * <pre>
     * PermissionsManager manager = new VaultPermissionsSystem();
     * Permission permission =
     */
    private final Permission vaultPerms;

    /**
     * The VaultPermissionsSystem class is a concrete implementation of the PermissionsManager abstract class.
     * It provides functionality for managing permissions using the Vault permissions system.
     */
    public VaultPermissionsSystem() {
        super(PermissionsHandlerType.VAULT);
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            throw new PermissionManagerLoadException("Vault permission system not found");
        }
        vaultPerms = rsp.getProvider();
    }

    /**
     * Performs a look-up to determine if a player has a specific permission.
     *
     * @param playerName The name of the player.
     * @param permission The permission to check.
     * @return true if the player has the permission, false otherwise.
     */
    private boolean lookUp(String playerName, String permission) {
        vaultPerms.has(Bukkit.getPlayer(playerName), permission);
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
     * Checks if the specified player has the given permission.
     *
     * @param player     The player to check the permission for. Must not be null.
     * @param permission The permission to check. Must not be null.
     * @return {@code true} if the player has the permission, {@code false} otherwise.
     */
    public boolean hasPermission(Player player, String permission) {
        return lookUp(player.getName(), permission);
    }

    /**
     * Checks if the given player has the specified permission.
     *
     * @param player The player to check permission for. Must not be null.
     * @return True if the player has the permission, false otherwise.
     */
    public boolean hasPermission(OfflinePlayer player) {
        return player.hasPlayedBefore() && player.isOp();
    }

}