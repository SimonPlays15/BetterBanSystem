/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.command;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.files.BasePluginDescription;
import we.github.mcdevstudios.betterbansystem.api.files.ResourceFile;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.api.permissions.PermissionsManager;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;

import java.io.InvalidObjectException;
import java.util.Map;

public abstract class BaseCommand {

    private final String commandName;
    private final PermissionsManager manager;
    private String permission;
    private String description;
    private String usage;
    private String label;

    public BaseCommand(String commandName) {
        this.manager = BetterBanSystem.getInstance().getPermissionsManager();
        commandName = commandName.toLowerCase();
        // TODO (Core.getDataFolder())
        BasePluginDescription pluginDescriptionFile;
        try {
            pluginDescriptionFile = new BasePluginDescription(new ResourceFile(BetterBanSystem.getInstance().getDataFolder()).getResource("plugin.yml"));
        } catch (InvalidObjectException e) {
            GlobalLogger.getLogger().error("Failed to load pluginDescription file (plugin.yml)", e);
            throw new RuntimeException(e);
        }
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

    public boolean testPermission(@NotNull BaseCommandSender sender) {
        if (sender.isConsole()) {
            return true;
        }
        return manager.hasPermission(sender.getName(), this.permission);
    }

    public boolean testPermission(BaseCommandSender sender, String permission) {
        if (sender.isConsole()) {
            return true;
        }
        return manager.hasPermission(sender.getName(), permission);
    }

    public abstract boolean runCommand(BaseCommandSender sender, String[] args);

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

    public void sendUsage(BaseCommandSender sender) {
        //TODO
        sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + getUsage());
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

    public PermissionsManager getManager() {
        return manager;
    }
}
