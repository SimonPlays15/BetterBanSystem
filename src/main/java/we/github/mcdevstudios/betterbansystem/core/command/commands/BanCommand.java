/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command.commands;


import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.core.ban.IBanEntry;
import we.github.mcdevstudios.betterbansystem.core.chat.StringFormatter;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class BanCommand extends BaseCommand {

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
