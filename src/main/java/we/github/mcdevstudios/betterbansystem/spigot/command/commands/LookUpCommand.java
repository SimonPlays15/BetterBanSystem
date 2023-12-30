/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command.commands;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;

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
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "§cPlease provide a name to lookup.");
            return true;
        }
        if (args.length == 1) {
            if (sender.isPlayer() && !this.testPermission(sender, this.getPermission() + ".other")) {
                sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("permissions_message"));
                return true;
            }
        }

        String target = args.length == 1 ? args[0] : sender.getName();
        UUID uuid = UUIDFetcher.getUUIDOrOfflineUUID(target);
        boolean hasPlayedBefore = Bukkit.getOfflinePlayer(uuid).hasPlayedBefore();

        // TODO
        List<String> lines = new ArrayList<>();
        lines.add("§8====§c Player Lookup §8====");
        lines.add("§7UUID: §a" + uuid);
        lines.add("§7Username: §a" + target);
        lines.add("§aHas played before: " + (hasPlayedBefore ? "§a" : "§c") + hasPlayedBefore);
        lines.add("§aIs Banned: " + (BanHandler.findBanEntry(uuid) != null ? "§atrue" : "§cfalse"));
        lines.add("§8====§c Player Lookup §8====");

        lines.forEach((sender::sendMessage));

        return true;
    }
}
