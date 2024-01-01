/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.chat.StringFormatter;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("player_is_offline", Map.of("target", args[0])));
            return true;
        }

        String reason = args.length < 2 ? "No reason provided" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        target.kickPlayer(StringFormatter.formatKickMessage(sender.getName(), reason));
        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("kick_message", Map.of("target", target.getName(), "reason", reason)));
        GlobalLogger.getLogger().info(sender.getName(), "kicked user", target.getName(), "from the server for:", reason);
        // TODO Broadcast Message to all Admins?
        return true;
    }

    @Override
    public List<String> onTabComplete(BaseCommandSender sender, String @NotNull [] args) {
        if (args.length == 1) {
            List<String> a = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                a.add(onlinePlayer.getName());
            }
            return a;
        }
        return null;
    }
}
