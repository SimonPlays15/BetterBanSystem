package me.github.simonplays15.betterbansystem.core.command;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.command.commands.*;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BaseCommandHandler {
    private final Map<String, BaseCommand> commands = new HashMap<>();

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

    private void registerCommand(@NotNull BaseCommand baseCommand) {
        if (commands.containsKey(baseCommand.getCommandName()))
            return;

        commands.put(baseCommand.getCommandName(), baseCommand);
        GlobalLogger.getLogger().debug("Registering Command:", baseCommand.getCommandName() + ":" + baseCommand.getPermission(), baseCommand.getDescription());
    }

    public Map<String, BaseCommand> getCommands() {
        return commands;
    }
}
