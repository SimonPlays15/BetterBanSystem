package me.github.simonplays15.betterbansystem.core.command;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.CommandException;
import me.github.simonplays15.betterbansystem.api.exceptions.InvalidDescriptionException;
import me.github.simonplays15.betterbansystem.api.files.BasePluginDescription;
import me.github.simonplays15.betterbansystem.api.runtimeservice.RuntimeService;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.permissions.PermissionsManager;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final List<String> aliases;
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
        this.usage = (String) commandData.getOrDefault("usage", "/<command>");
        this.aliases = (List<String>) commandData.getOrDefault("aliases", new ArrayList<>());
        this.label = commandName;
    }

    /**
     * Tests the permission of a command sender.
     *
     * @param sender The command sender to test the permission for.
     * @return True if the sender has permission, false otherwise.
     */
    public boolean testPermission(@NotNull BaseCommandSender sender) {
        return this.testPermission(sender, this.permission);
    }

    /**
     * Retrieves the list of aliases for this command.
     *
     * @return The list of aliases.
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Check if the provided sender has the given permission.
     *
     * @param sender     The BaseCommandSender instance.
     * @param permission The permission string to check.
     * @return True if the sender has the permission, false otherwise.
     */
    public boolean testPermission(@NotNull BaseCommandSender sender, @NotNull String permission) {
        if (permission.isEmpty())
            return true;
        if (sender.isConsole()) {
            return true;
        }
        return manager.hasPermission(sender.getName(), permission);
    }

    /**
     * Executes the command.
     *
     * @param sender the command sender.
     * @param args   the command arguments.
     * @return {@code true} if the command was executed successfully, {@code false} otherwise.
     * @throws CommandException if an error occurs during command execution.
     */
    public abstract boolean runCommand(BaseCommandSender sender, String[] args) throws CommandException;

    /**
     * Returns a list of tab completions for the given command sender and arguments.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     * @return The list of tab completions.
     */
    public List<String> onTabComplete(BaseCommandSender sender, String @NotNull [] args) {
        if (!this.testPermission(sender, this.getPermission()))
            return new ArrayList<>();
        if (args.length >= 1) {
            List<String> a = new ArrayList<>();
            if (RuntimeService.isSpigot()) {
                for (org.bukkit.entity.Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
                    a.add(onlinePlayer.getName());
                }
            } else if (RuntimeService.isBungeeCord()) {
                for (net.md_5.bungee.api.connection.ProxiedPlayer onlinePlayer : net.md_5.bungee.api.ProxyServer.getInstance().getPlayers()) {
                    a.add(onlinePlayer.getName());
                }
            }
            return a;
        }
        return new ArrayList<>();
    }

    /**
     * Returns the name of the command.
     *
     * @return The name of the command.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Retrieves the permission associated with this command.
     *
     * @return The permission associated with this command.
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the permission required to use this command.
     *
     * @param permission The permission string to set. It should follow the format "plugin.permission".
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Returns the usage of the command.
     *
     * @return The usage of the command.
     */
    public String getUsage() {
        return usage.replaceAll("<command>", this.label);
    }

    /**
     * Sets the usage message for the command.
     *
     * @param usage The usage message to be set.
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Sends the usage information of the command to the specified BaseCommandSender.
     *
     * @param sender The BaseCommandSender to send the usage information to.
     */
    public void sendUsage(@NotNull BaseCommandSender sender) {
        sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + getUsage());
    }

    /**
     * Sets the label for the command.
     *
     * @param label The label to set for the command.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the description of the command.
     *
     * @return The description of the command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the command.
     *
     * @param description The new description of the command.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the PermissionsManager object associated with this BaseCommand instance.
     *
     * @return The PermissionsManager object.
     */
    public PermissionsManager getPermManager() {
        return manager;
    }


    /**
     * Compares this {@code BaseCommand} object with the specified object for equality.
     *
     * @param o the object to compare with
     * @return {@code true} if the specified object is equal to this {@code BaseCommand}; {@code false} otherwise
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseCommand that = (BaseCommand) o;
        return Objects.equals(commandName, that.commandName) && Objects.equals(manager, that.manager) && Objects.equals(aliases, that.aliases) && Objects.equals(permission, that.permission) && Objects.equals(description, that.description) && Objects.equals(usage, that.usage) && Objects.equals(label, that.label);
    }

    /**
     * Computes the hash code for the object.
     *
     * @return the hash code value for this command
     */
    @Override
    public int hashCode() {
        return Objects.hash(commandName, manager, aliases, permission, description, usage, label);
    }

    @Override
    public String toString() {
        return "BaseCommand{" +
                "commandName='" + commandName + '\'' +
                ", manager=" + manager +
                ", aliases=" + aliases +
                ", permission='" + permission + '\'' +
                ", description='" + description + '\'' +
                ", usage='" + usage + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
