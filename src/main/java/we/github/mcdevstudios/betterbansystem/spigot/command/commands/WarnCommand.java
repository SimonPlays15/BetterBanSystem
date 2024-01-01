/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.core.warn.WarnHandler;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WarnCommand extends BaseCommand {

    public WarnCommand() {
        super("warn");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        String target = args[0];
        String reason = args.length == 1 ? "No reason provided" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        WarnHandler.addWarn(sender, target, reason);

        Player targetPlayer = Bukkit.getPlayer(target);
        if (targetPlayer != null)
            targetPlayer.sendMessage("You have been warned");

        sender.sendMessage("Warned player " + target + " for reason: " + reason);

        return true;
    }
}
