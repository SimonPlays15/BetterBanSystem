package me.github.simonplays15.betterbansystem.core.command;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.runtimeservice.RuntimeService;
import me.github.simonplays15.betterbansystem.core.command.commands.*;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The BaseCommandHandler class is responsible for handling commands in a command-based system.
 * It contains a collection of registered commands and provides methods to register and retrieve them.
 */
public class BaseCommandHandler {
    /**
     * The commands variable is a private member of the BaseCommandHandler class.
     * It is a Map that stores registered commands in a command-based system.
     * The keys of the map are command names, and the values are instances of the BaseCommand class.
     * <p>
     * The Map data structure allows for efficient retrieval of commands by their names.
     * New commands can be registered using the registerCommand() method, and existing commands can be retrieved using the getCommands() method.
     * <p>
     * The commands map is initialized in the constructor of the BaseCommandHandler class.
     * Several example commands are registered during initialization using the registerCommand() method.
     * <p>
     * Example usage:
     * <p>
     * // Create an instance of BaseCommandHandler     *  commandHandler = new BaseCommandHandler();
     * <p>
     * // Get all registered commands
     * Map<String, BaseCommand> commands = commandHandler.getCommands();
     * <p>
     * // Register a new command
     * BaseCommand newCommand = new CustomCommand();
     * commandHandler.registerCommand(newCommand);
     * <p>
     * // Retrieve a specific command by its name
     * BaseCommand command = commands.get("commandName");
     * <p>
     * Note: This documentation only covers the purpose and usage of the commands variable and does not provide example code.
     */
    private final Map<String, BaseCommand> commands = new HashMap<>();

    /**
     * The BaseCommandHandler class represents a command handler for managing and executing commands.
     */
    public BaseCommandHandler() {
        registerCommand(new KickCommand());
        registerCommand(new BanListCommand());
        registerCommand(new BanCommand());
        registerCommand(new UnbanIpCommand());
        registerCommand(new UnbanCommand());
        registerCommand(new LookUpCommand());
        registerCommand(new IpBanCommand());
        registerCommand(new TimeBanCommand());
        registerCommand(new WarnCommand());
        registerCommand(new DelWarnCommand());
        registerCommand(new MuteCommand());
        registerCommand(new UnmuteCommand());
    }

    /**
     * Registers a command in the command handler.
     * If a command with the same name is already registered, the method will return without registering.
     *
     * @param baseCommand The command to register. Must not be null.
     */
    private void registerCommand(@NotNull BaseCommand baseCommand) {

        if (RuntimeService.isBungeeCord()) {
            baseCommand.setCommandName("global" + baseCommand.getCommandName());
        }

        if (commands.containsKey(baseCommand.getCommandName()))
            return;

        commands.put(baseCommand.getCommandName(), baseCommand);
        GlobalLogger.getLogger().debug("Registering Command:", baseCommand.getCommandName() + ":" + baseCommand.getPermission(), baseCommand.getDescription());
    }

    /**
     * Retrieves the commands registered in the BaseCommandHandler.
     *
     * @return A map containing the registered commands, where the key is the command name and the value is the corresponding BaseCommand object.
     */
    public Map<String, BaseCommand> getCommands() {
        return commands;
    }
}
