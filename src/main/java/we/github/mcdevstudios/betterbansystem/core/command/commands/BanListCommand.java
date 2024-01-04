/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command.commands;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.ban.BanEntry;
import we.github.mcdevstudios.betterbansystem.core.ban.IBanEntry;
import we.github.mcdevstudios.betterbansystem.core.ban.IIPBanEntry;
import we.github.mcdevstudios.betterbansystem.core.ban.IPBanEntry;
import we.github.mcdevstudios.betterbansystem.core.chat.HoverMessageUtil;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

import java.text.SimpleDateFormat;
import java.util.*;

public class BanListCommand extends BaseCommand {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss | dd.MM.yyyy");

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

        sendBanList(sender, type);
        return true;
    }

    private void sendBanList(@NotNull BaseCommandSender sender, @NotNull String type) {
        List<TextComponent> banList = new ArrayList<>();
        Object player = BetterBanSystem.getPlayer(sender.getName());
        if (type.equalsIgnoreCase("players")) {
            List<IBanEntry> entries = BanEntry.getAllEntries();
            for (IBanEntry entry : entries) {
                banList.add(new TextComponent(getHoverableComponent("§c" + entry.name(), getBanInfo(entry))));
            }
        }
        if (type.equalsIgnoreCase("ips")) {
            List<IIPBanEntry> entries = IPBanEntry.getAllEntries();
            for (IIPBanEntry entry : entries) {
                banList.add(new TextComponent(getHoverableComponent("§c" + entry.ip(), getIpBanInfo(entry))));
            }
        }

        if (banList.isEmpty()) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("banlist.nobans", Map.of("type", type)));
            return;
        }

        sender.sendMessage("§6Banlist (banned " + banList.size() + " " + type + "):");
        TextComponent message = new TextComponent("");
        for (int i = 0; i < banList.size(); i++) {
            message.addExtra(banList.get(i));
            if (i < banList.size() - 1)
                message.addExtra("§7, ");
        }
        if (sender.isConsole())
            sender.sendMessage(message.toPlainText());
        else
            HoverMessageUtil.sendHoverableMessage(player, message);

    }

    private @NotNull TextComponent getHoverableComponent(String text, String hover) {
        TextComponent component = new TextComponent(text);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        return component;
    }

    private String getIpBanInfo(@NotNull IIPBanEntry entry) {
        return new StringJoiner("\n", "§c", "")
                .add("§bReason: §f" + entry.reason().substring(0, 20))
                .add("§bCreated: §6" + dateFormat.format(entry.created()))
                .add("§bExpires: §6" + (entry.expires() instanceof Date ? dateFormat.format(entry.expires()) : entry.expires()))
                .add("§bBanned by: §c" + entry.source())
                .toString();
    }

    private String getBanInfo(@NotNull IBanEntry entry) {
        return new StringJoiner("\n", "§c", "")
                .add("§bReason: §f" + entry.reason().substring(0, 20))
                .add("§bCreated: §6" + dateFormat.format(entry.created()))
                .add("§bExpires: §6" + (entry.expires() instanceof Date ? dateFormat.format(entry.expires()) : entry.expires()))
                .add("§bBanned by: §c" + entry.source())
                .toString();
    }

}
