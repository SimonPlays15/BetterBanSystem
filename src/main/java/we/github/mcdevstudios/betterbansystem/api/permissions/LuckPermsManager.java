/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.permissions;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import we.github.mcdevstudios.betterbansystem.api.exceptions.PermissionManagerLoadException;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LuckPermsManager extends PermissionsManager {

    private final LuckPerms luckPerms;

    public LuckPermsManager() throws PermissionManagerLoadException {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
        } catch (ClassNotFoundException e) {
            GlobalLogger.getLogger().error("Failed to load LuckPerms Manager. Is the plugin enabled?");
            throw new PermissionManagerLoadException(e.getMessage());
        }
        luckPerms = LuckPermsProvider.get();

    }

    private boolean lookUp(String playername, String permission) {
        UserManager userManager = luckPerms.getUserManager();
        if (UUIDFetcher.getUUID(playername) != null) {
            CompletableFuture<User> userFuture = userManager.loadUser(Objects.requireNonNull(UUIDFetcher.getUUID(playername)));
            User user = userFuture.join();

            return user.getNodes(NodeType.PERMISSION).contains(PermissionNode.builder(permission).build());
        }
        return false;
    }

    @Override
    public boolean hasPermission(String playername, String permission) {
        return lookUp(playername, permission);
    }

    public boolean hasPermission(Player player, String permission) {
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.getUser(player.getUniqueId());
        if (user == null) {
            GlobalLogger.getLogger().error("Failed to load user from LuckPerms UserManager", new NullPointerException("user is null"));
            return false;
        }
        return user.getNodes(NodeType.PERMISSION).contains(PermissionNode.builder(permission).build());
    }

    public boolean hasPermission(OfflinePlayer player, String permission) {
        UserManager userManager = luckPerms.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(player.getUniqueId());
        User user = userFuture.join();

        return user.getNodes(NodeType.PERMISSION).contains(PermissionNode.builder(permission).build());
    }
}
