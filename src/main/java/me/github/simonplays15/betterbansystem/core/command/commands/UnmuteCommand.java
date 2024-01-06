package me.github.simonplays15.betterbansystem.core.command.commands;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.CommandException;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.command.BaseCommand;
import me.github.simonplays15.betterbansystem.core.mute.MuteHandler;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("unmute.notmuted", Map.of("target", target)));
            return true;
        }

        Object targetPlayer = BetterBanSystem.getPlayer(target);
        if (targetPlayer != null) {
            BetterBanSystem.sendMessage(targetPlayer, BetterBanSystem.getInstance().getLanguageFile().getMessage("unmute.playerMessage"));
        }

        MuteHandler.removeMute(target);
        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("unmute.success", Map.of("target", target)));

        return true;
    }
}
