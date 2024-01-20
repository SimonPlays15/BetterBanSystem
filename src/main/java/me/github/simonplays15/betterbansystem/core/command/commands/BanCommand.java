package me.github.simonplays15.betterbansystem.core.command.commands;


/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.CommandException;
import me.github.simonplays15.betterbansystem.api.uuid.UUIDFetcher;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.ban.BanHandler;
import me.github.simonplays15.betterbansystem.core.ban.IBanEntry;
import me.github.simonplays15.betterbansystem.core.chat.StringFormatter;
import me.github.simonplays15.betterbansystem.core.command.BaseCommand;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The BanCommand class represents a command for banning a player in the system.
 * It extends the BaseCommand class.
 */
public class BanCommand extends BaseCommand {

    /**
     * Instantiates a new Ban command
     */
    public BanCommand() {
        super("ban");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String @NotNull [] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        String target = args[0];

        if (BanHandler.findBanEntry(target) != null) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("ban.alreadybanned", Map.of("target", target)));
            return true;
        }

        String reason = args.length < 2 ? BetterBanSystem.getInstance().getLanguageFile().getMessage("ban.defaults.banreason") : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        if (sender.isPlayer() && (this.getPermManager().hasPermission(target, "betterbansystem.exempt.ban") || BetterBanSystem.getInstance().getConfig().getStringList("exempted-players").contains(target))) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.exemptMessage", Map.of("targetType", "player", "target", target, "type", "ban")));
            return true;
        }
        IBanEntry entry = BanHandler.addBan(sender, target, reason, null);
        Object targetPlayer = BetterBanSystem.getPlayer(target);
        if (targetPlayer != null) {
            BetterBanSystem.kickPlayer(targetPlayer, StringFormatter.formatBanMessage(entry));
        }

        Object offlinePlayer = BetterBanSystem.getOfflinePlayer(UUIDFetcher.getUUIDOrOfflineUUID(target));
        if (offlinePlayer != null && !BetterBanSystem.hasPlayedBefore(offlinePlayer)) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.warning", Map.of("target", target)));
        }


        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("ban.defaults.banSuccess", Map.of("target", target)));

        return true;
    }
}
