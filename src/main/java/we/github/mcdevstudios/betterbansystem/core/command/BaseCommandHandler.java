/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.command.commands.*;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

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
