/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command;

import org.bukkit.Server;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.api.command.CommandSenderType;

import java.util.Set;
import java.util.UUID;

public class SpigotCommandSender extends BaseCommandSender implements CommandSender {
    private final CommandSender base;
    private final CommandSenderType type;

    public SpigotCommandSender(CommandSender base) {
        this.base = base;
        if (base instanceof Player)
            this.type = CommandSenderType.PLAYER;
        else if (base instanceof ConsoleCommandSender)
            this.type = CommandSenderType.CONSOLE;
        else if (base instanceof BlockCommandSender)
            this.type = CommandSenderType.BLOCK;
        else
            this.type = CommandSenderType.OTHER;
    }

    public static SpigotCommandSender of(CommandSender sender) {
        return new SpigotCommandSender(sender);
    }

    @Override
    public CommandSenderType getSenderType() {
        return this.type;
    }

    /**
     * @param string
     */
    @Override
    public void sendMessage(@NotNull String string) {
        this.base.sendMessage(string);
    }

    /**
     * @param strings
     */
    @Override
    public void sendMessage(@NotNull String... strings) {
        this.base.sendMessage(strings);
    }

    /**
     * @param uuid
     * @param s
     */
    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
        this.base.sendMessage(uuid, s);
    }

    /**
     * @param uuid
     * @param strings
     */
    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        this.base.sendMessage(uuid, strings);
    }

    /**
     * @return
     */
    @NotNull
    @Override
    public Server getServer() {
        return this.base.getServer();
    }

    /**
     * @return
     */
    @NotNull
    @Override
    public String getName() {
        return this.base.getName();
    }

    /**
     * @return
     */
    @NotNull
    @Override
    public Spigot spigot() {
        return this.base.spigot();
    }

    /**
     * @param s
     * @return
     */
    @Override
    public boolean isPermissionSet(@NotNull String s) {
        return this.base.isPermissionSet(s);
    }

    /**
     * @param permission
     * @return
     */
    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return this.base.isPermissionSet(permission);
    }

    /**
     * @param s
     * @return
     */
    @Override
    public boolean hasPermission(@NotNull String s) {
        return this.base.hasPermission(s);
    }

    /**
     * @param permission
     * @return
     */
    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return this.base.hasPermission(permission);
    }

    /**
     * @param plugin
     * @param s
     * @param b
     * @return
     */
    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return this.base.addAttachment(plugin, s, b);
    }

    /**
     * @param plugin
     * @return
     */
    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return this.base.addAttachment(plugin);
    }

    /**
     * @param plugin
     * @param s
     * @param b
     * @param i
     * @return
     */
    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return this.base.addAttachment(plugin, s, b, i);
    }

    /**
     * @param plugin
     * @param i
     * @return
     */
    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return this.base.addAttachment(plugin, i);
    }

    /**
     * @param permissionAttachment
     */
    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
        this.base.removeAttachment(permissionAttachment);
    }

    /**
     *
     */
    @Override
    public void recalculatePermissions() {
        this.base.recalculatePermissions();
    }

    /**
     * @return
     */
    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.base.getEffectivePermissions();
    }

    /**
     * @return
     */
    @Override
    public boolean isOp() {
        return this.base.isOp();
    }

    /**
     * @param b
     */
    @Override
    public void setOp(boolean b) {
        this.base.setOp(b);
    }
}
