/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command.commands;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.mute.MuteHandler;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

public class UnmuteCommand extends BaseCommand {
    public UnmuteCommand() {
        super("unmute");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String @NotNull [] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }
        String target = args[0];
        if (MuteHandler.findMuteEntry(target) == null) {
            sender.sendMessage("§c" + target + " is not muted from the server.");
            return true;
        }

        MuteHandler.removeMute(target);
        sender.sendMessage("§a" + target + " has been unmuted from the server.");

        return true;
    }
}
