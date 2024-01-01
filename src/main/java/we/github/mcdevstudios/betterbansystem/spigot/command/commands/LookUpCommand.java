/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command.commands;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.core.warn.IWarnEntry;
import we.github.mcdevstudios.betterbansystem.core.warn.Warn;
import we.github.mcdevstudios.betterbansystem.core.warn.WarnEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LookUpCommand extends BaseCommand {

    public LookUpCommand() {
        super("lookup");
    }

    @Override
    public boolean runCommand(@NotNull BaseCommandSender sender, String[] args) throws CommandException {
        if (sender.isConsole() && args.length == 0) {
            sender.sendMessage("§cPlease provide a name to lookup.");
            return true;
        }
        if (sender.isPlayer() && args.length == 1) {
            if (!this.testPermission(sender, this.getPermission() + ".other")) {
                sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("permissions_message"));
                return true;
            }
        }
        String target = args.length >= 1 ? args[0] : sender.getName();
        UUID uuid = UUIDFetcher.getUUIDOrOfflineUUID(target);
        boolean hasPlayedBefore = Bukkit.getOfflinePlayer(uuid).hasPlayedBefore();
        IWarnEntry warnEntry = WarnEntry.findEntry(uuid);
        int warns = 0;
        if (warnEntry != null)
            warns = warnEntry.warns().size();
        if (args.length > 1 && args[1].equalsIgnoreCase("listwarns")) {
            if (warnEntry == null || warnEntry.warns().isEmpty()) {
                sender.sendMessage("§aThe User " + target + " as no actuall warns.");
                return true;
            }
            // ID: # | Date: # | Source: # | Reason
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            List<String> g = new ArrayList<>();
            for (Warn warn : warnEntry.warns()) {
                g.add("§7ID: §a" + warn.id() + " §7| Date: §a" + dateFormat.format(warn.created()) + " §7| Source: §a" + warn.source() + " §7| Reason: §a" + warn.reason());
            }
            g.forEach(sender::sendMessage);
            return true;
        }

        // TODO
        List<String> lines = new ArrayList<>();
        lines.add("§8====§c Player Lookup §8====");
        lines.add("§7UUID: §a" + uuid);
        lines.add("§7Username: §a" + target);
        lines.add("§aHas played before: " + (hasPlayedBefore ? "§a" : "§c") + hasPlayedBefore);
        lines.add("§aIs Banned: " + (BanHandler.findBanEntry(uuid) != null ? "§atrue" : "§cfalse"));
        lines.add("§aWarns: " + (warns <= 5 ? "§a" : warns <= 9 ? "§c" : "§4") + warns);
        lines.add("§8====§c Player Lookup §8====");

        lines.forEach(sender::sendMessage);

        return true;
    }
}
