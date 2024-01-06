package we.github.mcdevstudios.betterbansystem.core.command.commands;

/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.chat.StringFormatter;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class KickCommand extends BaseCommand {
    public KickCommand() {
        super("kick");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            return false;
        }
        String targetString = args[0];
        Object target = BetterBanSystem.getPlayer(targetString);
        if (target == null) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.playerOffline", Map.of("target", args[0])));
            return true;
        }

        if (sender.isPlayer() && (this.getPermManager().hasPermission(targetString, "betterbansystem.exempt.kick"))) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.exemptMessage", Map.of("targetType", "player", "target", targetString, "type", "kick")));
            return true;
        }

        String reason = args.length < 2 ? BetterBanSystem.getInstance().getLanguageFile().getMessage("kick.defaults.reason") : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        BetterBanSystem.kickPlayer(target, StringFormatter.formatKickMessage(sender.getName(), reason));
        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("kick.success", Map.of("target", targetString, "reason", reason)));
        GlobalLogger.getLogger().info(sender.getName(), "kicked user", targetString, "from the server for:", reason);
        return true;
    }
}
