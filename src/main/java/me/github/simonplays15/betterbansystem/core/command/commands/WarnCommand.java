package me.github.simonplays15.betterbansystem.core.command.commands;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.CommandException;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.command.BaseCommand;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import me.github.simonplays15.betterbansystem.core.warn.WarnHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Warn command.
 */
public class WarnCommand extends BaseCommand {

    /**
     * Instantiates a new Warn command.
     */
    public WarnCommand() {
        super("warn");
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
    public boolean runCommand(BaseCommandSender sender, String @NotNull [] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        String target = args[0];
        if (sender.isPlayer() && (this.getPermManager().hasPermission(target, "betterbansystem.exempt.warn") || BetterBanSystem.getInstance().getConfig().getStringList("exempted-warn-players").contains(target))) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.exemptMessage", Map.of("targetType", "player", "target", target, "type", "warn")));
            return true;
        }


        String reason = args.length == 1 ? "No reason provided" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        WarnHandler.addWarn(sender, target, reason);

        Object targetPlayer = BetterBanSystem.getPlayer(target);
        if (targetPlayer != null)
            BetterBanSystem.sendMessage(targetPlayer, BetterBanSystem.getInstance().getLanguageFile().getMessage("warn.playerMessage", Map.of("reason", reason)));

        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("warn.success", Map.of("target", target)));

        return true;
    }
}
