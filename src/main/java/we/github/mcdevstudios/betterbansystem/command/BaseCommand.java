/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.utils.ChatUtils;

import java.util.Map;

public abstract class BaseCommand {

    private final String commandName;
    private String permission;
    private String description;
    private String usage;
    private String label;

    public BaseCommand(String commandName) {
        commandName = commandName.toLowerCase();
        PluginDescriptionFile pluginDescriptionFile = BetterBanSystem.getInstance().getDescription();
        if (!pluginDescriptionFile.getCommands().containsKey(commandName)) {
            throw new NullPointerException("Failed to find command " + commandName + " inside the plugin.yml (PluginDescriptionFile)");
        }
        Map<String, Object> commandData = pluginDescriptionFile.getCommands().get(commandName.toLowerCase());
        this.commandName = commandName;
        this.permission = (String) commandData.getOrDefault("permission", "");
        this.description = (String) commandData.getOrDefault("description", "Default Description");
        this.usage = (String) commandData.getOrDefault("usage", "/<command> [arguments...]");
        this.label = commandName;
    }

    public boolean testPermission(@NotNull CommandSender sender) {
        if (permission == null || permission.isEmpty())
            return true;
        return sender.hasPermission(permission);
    }

    public boolean testPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public String getCommandName() {
        return commandName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getUsage() {
        return usage.replaceAll("<command>", this.label);
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatUtils.getPrefix() + getUsage());
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
