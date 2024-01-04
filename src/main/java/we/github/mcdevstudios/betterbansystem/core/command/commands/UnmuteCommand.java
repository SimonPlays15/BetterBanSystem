/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command.commands;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.mute.MuteHandler;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

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
