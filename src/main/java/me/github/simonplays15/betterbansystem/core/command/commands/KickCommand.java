package me.github.simonplays15.betterbansystem.core.command.commands;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.chat.StringFormatter;
import me.github.simonplays15.betterbansystem.core.command.BaseCommand;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Kick command.
 */
public class KickCommand extends BaseCommand {
    /**
     * Instantiates a new Kick command.
     */
    public KickCommand() {
        super("kick");
    }

    /**
     * Run command boolean.
     *
     * @param sender the sender
     * @param args   the args
     * @return the boolean
     */
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
