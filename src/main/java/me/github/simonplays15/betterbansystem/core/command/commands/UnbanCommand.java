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

/**
 * The type Unban command.
 */
public class UnbanCommand extends BaseCommand {
    /**
     * Instantiates a new Unban command.
     */
    public UnbanCommand() {
        super("unban");
    }

    /**
     * Run command boolean.
     *
     * @param sender the sender
     * @param args   the args
     * @return the boolean
     * @throws CommandException the command exception
     */
    @Override
    public boolean runCommand(BaseCommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }
        String target = args[0];
        if (BanHandler.findBanEntry(target) == null) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.notBanned", Map.of("target", target)));
            return true;
        }

        BanHandler.removeBan(target);
        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("unban.unbanmessage", Map.of("target", target)));

        return true;
    }
}
