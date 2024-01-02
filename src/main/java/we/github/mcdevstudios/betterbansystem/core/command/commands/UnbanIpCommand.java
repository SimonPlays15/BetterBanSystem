/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command.commands;

import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnbanIpCommand extends BaseCommand {
    public UnbanIpCommand() {
        super("unbanip");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }
        String target = args[0];

        Pattern pattern = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

        Matcher matcher = pattern.matcher(target);
        if (!matcher.matches()) {
            sender.sendMessage("§4Invalid IP address.");
            return true;
        }

        if (BanHandler.findIPBanEntry(target) == null) {
            sender.sendMessage("§c" + target + " is not banned from the server.");
            return true;
        }

        BanHandler.removeIpBan(target);
        sender.sendMessage("§a" + target + " has been unbanned from the server.");

        return true;
    }
}
