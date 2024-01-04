/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command.commands;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.core.warn.IWarnEntry;
import we.github.mcdevstudios.betterbansystem.core.warn.WarnEntry;
import we.github.mcdevstudios.betterbansystem.core.warn.WarnHandler;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelWarnCommand extends BaseCommand {

    public DelWarnCommand() {
        super("delwarn");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String @NotNull [] args) throws CommandException {
        if (args.length < 2) {
            return false;
        }

        String target = args[0];
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(args[1]);
        if (!matcher.matches()) {
            sender.sendMessage("§cPlease provide a Number (e.g. 1 or 521) for the ID");
            return true;
        }
        int id = Integer.parseInt(args[1]);

        IWarnEntry entry = WarnEntry.findEntry(UUIDFetcher.getUUIDOrOfflineUUID(target));
        if (entry == null) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("delwarn.nowarns", Map.of("target", target)));
            return true;
        }

        WarnHandler.removeWarn(target, id);
        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("delwarn.success", Map.of("id", String.valueOf(id), "target", target)));
        return true;
    }
}
