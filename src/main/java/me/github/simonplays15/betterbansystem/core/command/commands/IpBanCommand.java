package me.github.simonplays15.betterbansystem.core.command.commands;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.exceptions.CommandException;
import me.github.simonplays15.betterbansystem.api.runtimeservice.RuntimeService;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.ban.BanHandler;
import me.github.simonplays15.betterbansystem.core.ban.IIPBanEntry;
import me.github.simonplays15.betterbansystem.core.chat.StringFormatter;
import me.github.simonplays15.betterbansystem.core.command.BaseCommand;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IpBanCommand extends BaseCommand {

    public IpBanCommand() {
        super("banip");
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String @NotNull [] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        String target = args[0];
        Pattern pattern = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

        Matcher matcher = pattern.matcher(target);
        if (!matcher.matches()) {
            sender.sendMessage("§4Invalid IP address.");
            return true;
        }


        if (BanHandler.findIPBanEntry(target) != null) {
            sender.sendMessage("§4The IP " + target + " is already banned.");
            return true;
        }

        String reason = args.length < 2 ? "You have been IP banned from the server" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));


        if (sender.isPlayer() && BetterBanSystem.getInstance().getConfig().getStringList("exempted-ips").contains(target)) {
            sender.sendMessage("§4The IP-Address is exempted from bans. If you really want to ban the IP-Address, please use the console to execute the ban.");
            return true;
        }

        IIPBanEntry entry = BanHandler.addIpBan(sender, target, reason, null);

        if (RuntimeService.isSpigot()) {
            org.bukkit.Bukkit.getOnlinePlayers().stream().filter(g -> Objects.requireNonNull(g.getAddress()).getAddress().getHostAddress().equalsIgnoreCase(target)).forEach(player -> player.kickPlayer(StringFormatter.formatIpBanMessage(entry)));
        } else if (RuntimeService.isBungeeCord()) {
            net.md_5.bungee.api.ProxyServer.getInstance().getPlayers().stream().filter(g -> g.getPendingConnection().getVirtualHost().getAddress().getHostAddress().equalsIgnoreCase(target)).forEach(player -> player.disconnect(new TextComponent(StringFormatter.formatIpBanMessage(entry))));
        }


        sender.sendMessage("§aIP " + target + " has been banned from the server.");

        return true;
    }
}
