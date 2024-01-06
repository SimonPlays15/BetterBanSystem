package me.github.simonplays15.betterbansystem.core.command.commands;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.CommandException;
import me.github.simonplays15.betterbansystem.api.uuid.UUIDFetcher;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.command.BaseCommand;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import me.github.simonplays15.betterbansystem.core.warn.IWarnEntry;
import me.github.simonplays15.betterbansystem.core.warn.WarnEntry;
import me.github.simonplays15.betterbansystem.core.warn.WarnHandler;
import org.jetbrains.annotations.NotNull;

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
