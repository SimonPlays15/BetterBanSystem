package me.github.simonplays15.betterbansystem.core.permissions;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.PermissionManagerLoadException;
import me.github.simonplays15.betterbansystem.api.uuid.UUIDFetcher;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * The LuckPermsManager class is responsible for managing permissions using the LuckPerms plugin.
 * It extends the PermissionsManager class which provides common functionality for managing permissions.
 */
public class LuckPermsManager extends PermissionsManager {

    /**
     * The LuckPerms variable represents an instance of the LuckPerms plugin.
     * It is used by the LuckPermsManager class to manage permissions using the LuckPerms plugin.
     */
    private final LuckPerms luckPerms;

    /**
     * The LuckPermsManager class is responsible for managing permissions using the LuckPerms plugin.
     * It extends the PermissionsManager class which provides common functionality for managing permissions.
     *
     * @throws PermissionManagerLoadException if there is an error loading the LuckPermsManager
     */
    public LuckPermsManager() throws PermissionManagerLoadException {
        super(PermissionsHandlerType.LUCKPERMS);
        try {
            Class.forName("net.luckperms.api.LuckPerms");
        } catch (ClassNotFoundException e) {
            GlobalLogger.getLogger().error("Failed to load LuckPerms Manager. Is the plugin enabled?");
            throw new PermissionManagerLoadException(e.getMessage());
        }
        luckPerms = LuckPermsProvider.get();

    }

    /**
     * Checks if the specified player has the given permission.
     *
     * @param playername the name of the player to check
     * @param permission the permission to check for
     * @return true if the player has the permission, false otherwise
     */
    private boolean lookUp(String playername, String permission) {
        UserManager userManager = luckPerms.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(Objects.requireNonNull(UUIDFetcher.getUUID(playername)));
        User user = userFuture.join();

        return user.getNodes(NodeType.PERMISSION).contains(PermissionNode.builder(permission).build());
    }

    /**
     * Checks if a player has a certain permission.
     *
     * @param playername The name of the player.
     * @param permission The permission to check for.
     * @return True if the player has the specified permission, false otherwise.
     */
    @Override
    public boolean hasPermission(String playername, String permission) {
        return lookUp(playername, permission);
    }

    /**
     * Checks whether a player has the specified permission.
     *
     * @param player     the player to check permissions for
     * @param permission the permission to check for
     * @return true if the player has the permission, false otherwise
     */
    public boolean hasPermission(Player player, String permission) {
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.getUser(player.getUniqueId());
        if (user == null) {
            GlobalLogger.getLogger().error("Failed to load user from LuckPerms UserManager", new NullPointerException("user is null"));
            return false;
        }
        return user.getNodes(NodeType.PERMISSION).contains(PermissionNode.builder(permission).build());
    }

    /**
     * Checks whether the specified player has the given permission.
     *
     * @param player     The OfflinePlayer to check permissions for
     * @param permission The permission to check
     * @return true if the player has the permission, false otherwise
     */
    public boolean hasPermission(OfflinePlayer player, String permission) {
        UserManager userManager = luckPerms.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(player.getUniqueId());
        User user = userFuture.join();

        return user.getNodes(NodeType.PERMISSION).contains(PermissionNode.builder(permission).build());
    }
}
