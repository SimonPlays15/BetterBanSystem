/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.chat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.ban.IBanEntry;
import we.github.mcdevstudios.betterbansystem.core.ban.IIPBanEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringFormatter {
    private static final String HEADER = "§8=== §c§lBetterBanSystem §8===";
    private static final String SEPARATOR = "\n";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss | dd.MM.yyyy");
    private static final String KICK_MESSAGE_TEMPLATE = HEADER + SEPARATOR +
            "§cYou have been kicked" + SEPARATOR +
            "§6By:§4 %s" + SEPARATOR +
            "§6For:§f %s" + SEPARATOR +
            HEADER;

    private static final String BAN_MESSAGE_TEMPLATE = HEADER + SEPARATOR +
            "§4You have been banned" + SEPARATOR +
            "§6By:§c %s" + SEPARATOR +
            "§6For:§c %s" + SEPARATOR +
            "§6Until:§a %s" + SEPARATOR +
            "§6Date of ban:§c %s" + SEPARATOR +
            HEADER;

    private static final String IPBAN_MESSAGE_TEMPLATE = HEADER + SEPARATOR +
            "§4You have been IP banned" + SEPARATOR +
            "§6By:§c %s" + SEPARATOR +
            "§6For:§c %s" + SEPARATOR +
            "§6Until:§a %s" + SEPARATOR +
            "§6Date of ban:§c %s" + SEPARATOR +
            HEADER;

    @Contract("_, _ -> new")
    public static @NotNull String formatKickMessage(String sender, String reason) {
        return ChatColor.translateAlternateColorCodes('&', String.format(KICK_MESSAGE_TEMPLATE, sender, reason));
    }

    @Contract("_, -> new")
    public static @NotNull String formatBanMessage(IBanEntry entry) {
        return ChatColor.translateAlternateColorCodes('&', String.format(BAN_MESSAGE_TEMPLATE, entry.source(), entry.reason(), (entry.expires() instanceof Date ? dateFormat.format(entry.expires()) : entry.expires()), dateFormat.format(entry.created())));
    }

    @Contract("_, -> new")
    public static @NotNull String formatIpBanMessage(IIPBanEntry entry) {
        return ChatColor.translateAlternateColorCodes('&', String.format(BAN_MESSAGE_TEMPLATE, entry.source(), entry.reason(), (entry.expires() instanceof Date ? dateFormat.format(entry.expires()) : entry.expires()), dateFormat.format(entry.created())));
    }
}
