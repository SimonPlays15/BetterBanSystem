/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.command.commands;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.ban.BanEntry;
import we.github.mcdevstudios.betterbansystem.api.ban.IPBanEntry;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BanListCommand extends BaseCommand {

    public BanListCommand() {
        super("banlist");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String @NotNull [] args) throws CommandException {
        String type = "players";
        if (args.length > 0) {
            type = args[0].toLowerCase();
        }
        if (!Objects.equals(type, "players") && !Objects.equals(type, "ips")) {
            type = "players";
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + args[0] + " is not an correctly list parameter.");
        }
        List<String> banList = new ArrayList<>();
        if (type.equalsIgnoreCase("players")) {
            BanEntry.getAllEntries("banned-players.json").forEach((iBanEntry -> {
                banList.add(iBanEntry.name());
            }));
        } else if (type.equalsIgnoreCase("ips")) {
            IPBanEntry.getAllEntries("banned-ips.json").forEach((iBanEntry -> {
                banList.add(iBanEntry.ip());
            }));
        }
        if (banList.isEmpty()) {
            sender.sendMessage(BetterBanSystem.getInstance().getPrefix() + "There are no banned " + type);
            return true;
        }
        sender.sendMessage("§6Banlist (banned " + banList.size() + " " + type + "):");
        sender.sendMessage(String.join("§r, §c", banList));
        return true;
    }
}
