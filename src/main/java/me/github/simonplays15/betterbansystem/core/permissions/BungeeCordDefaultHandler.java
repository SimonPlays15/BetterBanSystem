package me.github.simonplays15.betterbansystem.core.permissions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * BungeeCordDefaultHandler is a class that extends PermissionsManager and represents the default permissions handler for BungeeCord platforms.
 */
public class BungeeCordDefaultHandler extends PermissionsManager {

    /**
     * BungeeCordDefaultHandler is a class that extends PermissionsManager and represents the default permissions handler for BungeeCord platforms.
     */
    public BungeeCordDefaultHandler() {
        super(PermissionsHandlerType.BUNGEECORD);
    }

    /**
     * Looks up whether the specified player has the given permission.
     *
     * @param playerName the name of the player
     * @param permission the permission to check
     * @return true if the player has the permission, false otherwise
     */
    private boolean lookUp(String playerName, String permission) {

        if (ProxyServer.getInstance().getPlayer(playerName) != null) {
            return Objects.requireNonNull(ProxyServer.getInstance().getPlayer(playerName)).hasPermission(permission);
        }

        return false;
    }

    /**
     * Checks if the specified player has the given permission.
     *
     * @param playername the name of the player
     * @param permission the permission to check
     * @return true if the player has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(String playername, String permission) {
        return lookUp(playername, permission);
    }

    /**
     * Checks if a player has a specific permission.
     *
     * @param player     The player to check the permission for. Cannot be null.
     * @param permission The permission to check. Cannot be null.
     * @return True if the player has the permission, false otherwise.
     */
    public boolean hasPermission(@NotNull ProxiedPlayer player, String permission) {
        return hasPermission(player.getName(), permission);
    }
}
