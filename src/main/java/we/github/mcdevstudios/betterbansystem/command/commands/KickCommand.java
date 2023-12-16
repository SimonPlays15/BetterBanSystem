/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import we.github.mcdevstudios.betterbansystem.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.command.BaseCommand;

import java.util.Map;

public class KickCommand extends BaseCommand {
    public KickCommand() {
        super("kick");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws Exception {
        if (args.length < 1) {
            this.sendUsage(sender);
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(BetterBanSystem.getLanguageFile().getMessage("player_is_offline", Map.of("player", args[0])));
            return true;
        }

        StringBuilder reason = new StringBuilder("No reason provided");
        if (args.length >= 2) {
            reason = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }
            reason.delete(reason.length() - 1, reason.length());
        }

        target.kickPlayer(ChatColor.translateAlternateColorCodes('&', reason.toString()));
        sender.sendMessage(BetterBanSystem.getLanguageFile().getMessage("kick_message", Map.of("target", target.getName(), "reason", reason.toString())));
        // TODO Broadcast Message to all Admins?
        return true;
    }
}
