package me.github.simonplays15.betterbansystem.core.command.commands;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.CommandException;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.ban.BanHandler;
import me.github.simonplays15.betterbansystem.core.command.BaseCommand;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;

import java.util.Map;
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
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.notBanned", Map.of("target", target)));
            return true;
        }

        BanHandler.removeIpBan(target);
        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("unban.unbanmessage", Map.of("target", target)));

        return true;
    }
}
