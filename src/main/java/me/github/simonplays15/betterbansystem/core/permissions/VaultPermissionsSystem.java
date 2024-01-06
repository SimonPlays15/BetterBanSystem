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

public class VaultPermissionsSystem extends PermissionsManager {

    private final Permission vaultPerms;

    public VaultPermissionsSystem() {
        super(PermissionsHandlerType.VAULT);
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            throw new PermissionManagerLoadException("Vault permission system not found");
        }
        vaultPerms = rsp.getProvider();
    }

    private boolean lookUp(String playerName, String permission) {
        vaultPerms.has(Bukkit.getPlayer(playerName), permission);
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