/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command.commands;

import we.github.mcdevstudios.betterbansystem.api.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;

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
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§c" + target + " is not banned from the server.");
            return true;
        }

        BanHandler.removeBan(target);
        sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§a" + target + " has been unbanned from the server.");

        return true;
    }
}
