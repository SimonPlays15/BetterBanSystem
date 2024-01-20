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

/**
 * The BaseCommand class represents a base command that can be executed by a command sender.
 * Subclasses should extend this abstract class to implement their own specific commands.
 */
public abstract class BaseCommand {

    /**
     * Represents a variable manager that holds an instance of PermissionsManager.
     * The manager is declared as private final and cannot be modified once set.
     */
    private final PermissionsManager manager;
    /**
     * Represents a list of aliases associated with a command.
     * Aliases are alternative names that can be used to execute the command.
     * The list is unmodifiable, meaning that it cannot be modified after it is initialized.
     */
    private final List<String> aliases;
    /**
     * Represents a private variable that stores the name of a command.
     */
    private String commandName;
    /**
     * Represents the permission associated with a command.
     */
    private String permission;
    /**
     * Description of the variable.
     */
    private String description;
    /**
     * The usage of the command
     */
    private String usage;
    /**
     * Represents a private string variable called 'label'.
     * This variable is used to store a label associated with a command.
     */
    private String label;

    /**
     * Represents a base command.
     *
     * @param commandName The name of the command.
     * @throws InvalidDescriptionException if the command name cannot be found in the plugin.yml file.
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
     * Tests if the specified command sender has the required permission to execute the command.
     *
     * @param sender The command sender to test.
     * @return true if the command sender has the required permission, false otherwise.
     */
    public boolean testPermission(@NotNull BaseCommandSender sender) {
        return this.testPermission(sender, this.permission);
    }

    /**
     * Returns the list of aliases associated with the command.
     *
     * @return the list of aliases
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * This method checks if a given command sender has the specified permission.
     *
     * @param sender     The BaseCommandSender to test the permission for.
     * @param permission The permission string to check.
     * @return True if the sender has the permission, false otherwise.
     */
    public boolean testPermission(@NotNull BaseCommandSender sender, @NotNull String permission) {
        if (permission.isEmpty())
            return true;
        if (sender.isConsole()) {
            return true;
        }
        if (manager.hasPermission(sender.getName(), "betterbansystem.*"))
            return true;

        if (permission.contains(".commands.") && manager.hasPermission(sender.getName(), "betterbansystem.commands.*"))
            return true;
        if (permission.contains(".exempt.") && manager.hasPermission(sender.getName(), "betterbansystem.exempt.*"))
            return true;

        return manager.hasPermission(sender.getName(), permission);
    }

    /**
     * Executes the command.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     * @return true if the command executed successfully, false otherwise.
     * @throws CommandException if a command encounters an error or exception.
     */
    public abstract boolean runCommand(BaseCommandSender sender, String[] args) throws CommandException;

    /**
     * Generates a list of tab completion options for the given command.
     *
     * @param sender The command sender initiating the tab completion.
     * @param args   The arguments provided after the command.
     * @return A list of tab completion options.
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
     * Retrieves the name of the command.
     *
     * @return The name of the command.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Sets the name of the command.
     *
     * @param commandName The name of the command to set.
     */
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    /**
     * Retrieves the permission associated with this command.
     *
     * @return the permission associated with this command as a string
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the permission for the command.
     *
     * @param permission the permission to set
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Retrieves the usage string of the command.
     *
     * @return the usage string with the label of the command
     */
    public String getUsage() {
        return usage.replaceAll("<command>", this.label);
    }

    /**
     * Sets the usage of the command.
     *
     * @param usage the usage of the command
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Sends the usage of the command to the specified sender.
     *
     * @param sender The base command sender.
     */
    public void sendUsage(@NotNull BaseCommandSender sender) {
        sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + getUsage());
    }

    /**
     * Sets the label for a command.
     *
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the description of the command.
     *
     * @return the description of the command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the method.
     *
     * @param description the description to be set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the PermissionsManager instance associated with this BaseCommand.
     *
     * @return The PermissionsManager instance.
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
     * Computes the hash code for this object.
     *
     * @return the hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(commandName, manager, aliases, permission, description, usage, label);
    }

    /**
     * Returns a string representation of the BaseCommand object.
     * The string includes the values of the commandName, manager, aliases, permission, description, usage, and label fields.
     *
     * @return a string representation of the BaseCommand object
     */
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
