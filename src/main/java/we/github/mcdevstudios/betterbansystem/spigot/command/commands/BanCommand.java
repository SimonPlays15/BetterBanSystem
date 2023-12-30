/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;

import java.util.Arrays;
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
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§4The player " + target + " is already banned.");
            return true;
        }

        String reason = args.length < 2 ? "You have been banned from the server" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        if (sender.isPlayer() && (this.getPermManager().hasPermission(target, "betterbansystem.exempt.ban") || BetterBanSystem.getInstance().getConfig().getStringList("exempted-players").contains(target))) {
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§4The player is exempted from bans. If you really want to ban the user, please use the console to execute the ban.");
            return true;
        }
        Player targetPlayer = Bukkit.getPlayer(target);
        if (targetPlayer != null) {
            targetPlayer.kickPlayer(reason);
        }

        if (!Bukkit.getOfflinePlayer(UUIDFetcher.getUUIDOrOfflineUUID(target)).hasPlayedBefore()) {
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§4Warning: The player " + target + " never visited the server.");
        }

        BanHandler.addBan(sender, target, reason, null);
        sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§aPlayer " + target + " has been banned from the server.");

        return true;
    }
}
