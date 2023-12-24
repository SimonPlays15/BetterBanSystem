/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import we.github.mcdevstudios.betterbansystem.api.chat.ChatColor;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;

import java.util.Map;

public class KickCommand extends BaseCommand {
    public KickCommand() {
        super("kick");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender);
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("player_is_offline", Map.of("target", args[0])));
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
        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("kick_message", Map.of("target", target.getName(), "reason", reason.toString())));
        GlobalLogger.getLogger().info(sender.getName(), "kicked user", target.getName(), "from the server for:", reason);
        // TODO Broadcast Message to all Admins?
        return true;
    }
}
