/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.permissions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import we.github.mcdevstudios.betterbansystem.BetterBanSystem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class PermissionsExHandler extends PermissionsManager {

    private final Method permissionsExHasMethod;

    public PermissionsExHandler() throws Exception {
        try {
            Class<?> permissionsExClass = Class.forName("ru.tehkode.permissions.bukkit.PermissionsEx");
            permissionsExHasMethod = permissionsExClass.getMethod("has", String.class, String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            BetterBanSystem.getGlobalLogger().error("Failed to load PermissionsEX Manager. Is the plugin enabled?", e);
            throw new Exception(e);
        }
    }

    private boolean lookUp(String playername, String permission) {
        try {
            return (boolean) permissionsExHasMethod.invoke(null, playername, permission);
        } catch (IllegalAccessException | InvocationTargetException e) {
            BetterBanSystem.getGlobalLogger().error("Failed to invoke permissionsEx \"has\" method. Falling back to default Player#hasPermission Method", e);
            if (Bukkit.getPlayer(playername) != null) {
                return Objects.requireNonNull(Bukkit.getPlayer(playername)).hasPermission(permission);
            } else {
                BetterBanSystem.getGlobalLogger().warn("Failed to get online player:", playername);
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(String playername, String permission) {
        return lookUp(playername, permission);
    }

    @Override
    public boolean hasPermission(Player player, String permission) {
        return hasPermission(player.getName(), permission);
    }

    @Override
    public boolean hasPermission(OfflinePlayer player, String permission) {
        return hasPermission(player.getName(), permission);
    }
}
