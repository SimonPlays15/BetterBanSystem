/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.command;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.api.exceptions.InvalidDescriptionException;
import we.github.mcdevstudios.betterbansystem.api.files.BasePluginDescription;
import we.github.mcdevstudios.betterbansystem.api.permissions.PermissionsManager;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseCommand {

    /**
     * The name of the command.
     */
    private final String commandName;
    /**
     * Represents a private final variable for managing permissions.
     *
     * @see BetterBanSystem
     */
    private final PermissionsManager manager;
    /**
     * Represents the permission associated with a command.
     */
    private String permission;
    /**
     * private variable to store the description.
     */
    private String description;
    /**
     * Returns the usage information of the command.
     */
    private String usage;
    /**
     * Represents the label of a command.
     * The label is a string that identifies a command.
     * It can be used to match a command with its corresponding functionality.
     */
    private String label;

    /**
     * Represents a base command.
     * Constructor initializes the command properties based on the provided command name.
     *
     * @param commandName The name of the command.
     * @throws InvalidDescriptionException If the command name is not found in the plugin.yml (PluginDescriptionFile).
     */
    public BaseCommand(String commandName) {
        this.manager = BetterBanSystem.getInstance().getPermissionsManager();
        commandName = commandName.toLowerCase();
        BasePluginDescription pluginDescriptionFile = BetterBanSystem.getInstance().getPluginDescription();
        if (!pluginDescriptionFile.getCommands().containsKey(commandName)) {
            throw new InvalidDescriptionException("Failed to find command " + commandName + " inside the plugin.yml (PluginDescriptionFile)");
        }
        Map<String, Object> commandData = pluginDescriptionFile.getCommands().get(commandName.toLowerCase());
        this.commandName = commandName;
        this.permission = (String) commandData.getOrDefault("permission", "");
        this.description = (String) commandData.getOrDefault("description", "Default Description");
        this.usage = (String) commandData.getOrDefault("usage", "/<command> [arguments...]");
        this.label = commandName;
    }

    /**
     * Tests the permission of a given command sender.
     *
     * @param sender The CommandSender to test the permission for. Must not be null.
     * @return true if the command sender has the required permission, otherwise false.
     */
    public boolean testPermission(@NotNull BaseCommandSender sender) {
        return this.testPermission(sender, this.permission);
    }

    /**
     * This method tests if a given sender has a specific permission.
     *
     * @param sender     The CommandSender
     * @param permission The permission to check
     * @return true if the sender has the permission, false otherwise
     */
    public boolean testPermission(@NotNull BaseCommandSender sender, String permission) {
        if (permission.isEmpty())
            return true;
        if (sender.isConsole()) {
            return true;
        }
        return manager.hasPermission(sender.getName(), permission);
    }

    /**
     * @param sender The CommandSender {@link BaseCommandSender}
     * @param args   the argument of the command
     * @return boolean if command was successfully or not
     * @throws CommandException if an error inside a command class occurred
     */
    public abstract boolean runCommand(BaseCommandSender sender, String[] args) throws CommandException;

    /**
     * @param sender The CommandSender {@link BaseCommandSender}
     * @param args   the arguments of the commands
     * @return a {@link List<String>}
     */
    public List<String> onTabComplete(BaseCommandSender sender, String[] args) {
        return new ArrayList<>();
    }

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

    public PermissionsManager getPermManager() {
        return manager;
    }

    @Override
    public String toString() {
        return "BaseCommand{" +
                "commandName='" + commandName + '\'' +
                ", manager=" + manager +
                ", permission='" + permission + '\'' +
                ", description='" + description + '\'' +
                ", usage='" + usage + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
