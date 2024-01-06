package me.github.simonplays15.betterbansystem.core.permissions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BungeeCordDefaultHandler extends PermissionsManager {

    public BungeeCordDefaultHandler() {
        super(PermissionsHandlerType.BUNGEECORD);
    }

    private boolean lookUp(String playerName, String permission) {

        if (ProxyServer.getInstance().getPlayer(playerName) != null) {
            return Objects.requireNonNull(ProxyServer.getInstance().getPlayer(playerName)).hasPermission(permission);
        }

        return false;
    }

    /**
     * @param playername String
     * @param permission String
     * @return boolean
     */
    @Override
    public boolean hasPermission(String playername, String permission) {
        return lookUp(playername, permission);
    }

    public boolean hasPermission(@NotNull ProxiedPlayer player, String permission) {
        return hasPermission(player.getName(), permission);
    }
}
