/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command.commands;

import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

public class UnbanCommand extends BaseCommand {
    public UnbanCommand() {
        super("unban");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }
        String target = args[0];
        if (BanHandler.findBanEntry(target) == null) {
            sender.sendMessage("§c" + target + " is not banned from the server.");
            return true;
        }

        BanHandler.removeBan(target);
        sender.sendMessage("§a" + target + " has been unbanned from the server.");

        return true;
    }
}
