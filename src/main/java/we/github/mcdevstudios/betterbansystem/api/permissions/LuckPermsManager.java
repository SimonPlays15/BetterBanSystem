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

public class LuckPermsManager extends PermissionsManager {

    private final Method luckPermsGetUserManagerMethod;
    private final Method luckPermsUserManagerCheckPermissionMethod;

    public LuckPermsManager() throws Exception {
        try {
            Class<?> luckPermsClass = Class.forName("net.luckperms.api.LuckPerms");
            luckPermsGetUserManagerMethod = luckPermsClass.getMethod("getUserManager");
            Class<?> userManagerClass = Class.forName("net.luckperms.api.UserManager");
            luckPermsUserManagerCheckPermissionMethod = userManagerClass.getMethod("checkPermission", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            BetterBanSystem.getGlobalLogger().error("Failed to load LuckPerms Manager. Is the plugin enabled?", e);
            throw new Exception(e);
        }
    }

    private boolean lookUp(String playername, String permission) {
        try {
            Object userManager = luckPermsGetUserManagerMethod.invoke(null);
            Object user = userManager.getClass().getMethod("getUser", String.class).invoke(userManager, playername);

            return (boolean) luckPermsUserManagerCheckPermissionMethod.invoke(user, permission);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            BetterBanSystem.getGlobalLogger().error("Failed to invoke luckperms \"has\" method. Falling back to default Player#hasPermission Method", e);
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

    public boolean hasPermission(Player player, String permission) {
        return hasPermission(player.getName(), permission);
    }

    public boolean hasPermission(OfflinePlayer player, String permission) {
        return hasPermission(player.getName(), permission);
    }
}
