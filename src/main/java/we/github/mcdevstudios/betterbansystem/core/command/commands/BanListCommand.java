/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command.commands;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.ban.BanEntry;
import we.github.mcdevstudios.betterbansystem.core.ban.IPBanEntry;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

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
            sender.sendMessage(args[0] + " is not an correctly list parameter.");
        }
        List<String> banList = new ArrayList<>();
        if (type.equalsIgnoreCase("players")) {
            BanEntry.getAllEntries().forEach((iBanEntry -> {
                banList.add(iBanEntry.name());
            }));
        } else if (type.equalsIgnoreCase("ips")) {
            IPBanEntry.getAllEntries().forEach((iBanEntry -> {
                banList.add(iBanEntry.ip());
            }));
        }
        if (banList.isEmpty()) {
            sender.sendMessage("There are no banned " + type);
            return true;
        }
        sender.sendMessage("§6Banlist (banned " + banList.size() + " " + type + "):");
        sender.sendMessage(String.join("§r, §c", banList));
        return true;
    }
}
