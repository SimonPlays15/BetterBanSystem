/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.chat;

public class StringFormatter {
    private static final String HEADER = "§8=== §c§lBetterBanSystem §8===";
    private static final String SEPARATOR = "\n";
    private static final String KICK_MESSAGE_TEMPLATE = HEADER + SEPARATOR +
            "You have been kicked" + SEPARATOR +
            "By: %s" + SEPARATOR +
            "For: %s" + SEPARATOR +
            HEADER;

    private static final String BAN_MESSAGE_TEMPLATE = HEADER + SEPARATOR +
            "You have been banned" + SEPARATOR +
            "By: %s" + SEPARATOR +
            "For: %s" + SEPARATOR +
            "Until: %s" + SEPARATOR +
            "Date of ban: %s" + SEPARATOR +
            HEADER;

    public static String formatKickMessage(String sender, String reason) {
        return ChatColor.translateAlternateColorCodes('&', String.format(KICK_MESSAGE_TEMPLATE, sender, reason));
    }

    public static String formatBanMessage(String sender, String reason, String banDuration, String banDate) {
        return ChatColor.translateAlternateColorCodes('&', String.format(BAN_MESSAGE_TEMPLATE, sender, reason, banDuration, banDate));
    }
}
