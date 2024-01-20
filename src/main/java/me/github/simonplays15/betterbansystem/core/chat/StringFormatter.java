package me.github.simonplays15.betterbansystem.core.chat;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.ban.IBanEntry;
import me.github.simonplays15.betterbansystem.core.ban.IIPBanEntry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The StringFormatter class provides methods for formatting messages related to bans and kicks.
 */
public class StringFormatter {
    /**
     * The HEADER variable represents the header string used in the BetterBanSystem.
     * It is a static, final variable of type String.
     * The value of HEADER is "§8=== §c§lBetterBanSystem §8===".
     */
    private static final String HEADER = "§8=== §c§lBetterBanSystem §8===";
    /**
     * A string that represents a line separator.
     * It is used to separate different sections or lines of text in chat messages.
     * <p>
     * The value of this variable is "\n".
     *
     * @see StringFormatter
     */
    private static final String SEPARATOR = "\n";
    /**
     * The dateFormat variable is an instance of the SimpleDateFormat class. It is used to format dates and times in a specific pattern.
     * The pattern used for formatting is "HH:mm:ss | dd.MM.yyyy", which represents hours (24-hour format), minutes, seconds, day of the month, month, and year.
     * <p>
     * Example usage:
     * String formattedDate = dateFormat.format(date);
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss | dd.MM.yyyy");
    /**
     * This variable represents the template for a kick message.
     * It is used to format the kick message with specific information such as the sender and reason.
     */
    private static final String KICK_MESSAGE_TEMPLATE = HEADER + SEPARATOR +
            "§cYou have been kicked" + SEPARATOR +
            "§6By:§4 %s" + SEPARATOR +
            "§6For:§f %s" + SEPARATOR +
            HEADER;

    /**
     * The BAN_MESSAGE_TEMPLATE variable is a template for formatting ban messages.
     * <p>
     * It is used in the StringFormatter class to generate ban messages for banned players. The template includes placeholders for various ban details such as the ban source, ban
     * reason, ban expiration date, and date of ban creation.
     * <p>
     * The template uses the HEADER and SEPARATOR variables from the StringFormatter class to format the message header and separate each line of the message.
     * <p>
     * Example usage:
     * ```
     * String banMessage = String.format(BAN_MESSAGE_TEMPLATE, banSource, banReason, banExpiration, banDate);
     * ```
     */
    private static final String BAN_MESSAGE_TEMPLATE = HEADER + SEPARATOR +
            "§4You have been banned" + SEPARATOR +
            "§6By:§c %s" + SEPARATOR +
            "§6For:§c %s" + SEPARATOR +
            "§6Until:§a %s" + SEPARATOR +
            "§6Date of ban:§c %s" + SEPARATOR +
            HEADER;

    /**
     * IPBAN_MESSAGE_TEMPLATE is a constant variable that stores the message template for an IP ban message. This message template is used to format the IP ban message that is sent
     * to banned players.
     * <p>
     * The IPBAN_MESSAGE_TEMPLATE is a string that consists of several sections:
     * 1. HEADER - A constant string that represents the header of the message, which is "§8=== §c§lBetterBanSystem §8===". This header is displayed at the top and bottom of the message
     * .
     * 2. SEPARATOR - A constant string that represents a new line character, which is "\n". This separator is used to separate different sections of the message.
     * 3. "§4You have been IP banned" - This string is displayed as the first line of the message and informs the player that they have been IP banned.
     * 4. "§6By:§c %s" - This string is displayed as the second line of the message and specifies the source of the ban. The "%s" is a placeholder that will be replaced with the source
     * of the ban.
     * 5. "§6For:§c %s" - This string is displayed as the third line of the message and specifies the reason for the ban. The "%s" is a placeholder that will be replaced with the
     * reason for the ban.
     * 6. "§6Until:§a %s" - This string is displayed as the fourth line of the message and specifies the expiration date of the ban. The "%s" is a placeholder that will be replaced
     * with the expiration date of the ban.
     * 7. "§6Date of ban:§c %s" - This string is displayed as the fifth line of the message and specifies the date when the ban was created. The "%s" is a placeholder that will be
     * replaced with the date of the ban.
     * 8. HEADER - The same header string is displayed at the bottom of the message.
     * <p>
     * The IPBAN_MESSAGE_TEMPLATE is used by the formatIpBanMessage method in the StringFormatter class to format the IP ban message.
     */
    private static final String IPBAN_MESSAGE_TEMPLATE = HEADER + SEPARATOR +
            "§4You have been IP banned" + SEPARATOR +
            "§6By:§c %s" + SEPARATOR +
            "§6For:§c %s" + SEPARATOR +
            "§6Until:§a %s" + SEPARATOR +
            "§6Date of ban:§c %s" + SEPARATOR +
            HEADER;

    /**
     * Formats a kick message by replacing placeholders in the kick message template with the sender and reason.
     *
     * @param sender the sender of the kick message
     * @param reason the reason for the kick
     * @return the formatted kick message
     */
    @Contract("_, _ -> new")
    public static @NotNull String formatKickMessage(String sender, String reason) {
        return ChatColor.translateAlternateColorCodes('&', String.format(KICK_MESSAGE_TEMPLATE, sender, reason));
    }

    /**
     * Formats a ban message for a given ban entry.
     *
     * @param entry the ban entry to format the message for
     * @return the formatted ban message
     */
    @Contract("_, -> new")
    public static @NotNull String formatBanMessage(@NotNull IBanEntry entry) {
        return ChatColor.translateAlternateColorCodes('&', String.format(BAN_MESSAGE_TEMPLATE, entry.source(), entry.reason(), (entry.expires() instanceof Date ? dateFormat.format(entry.expires()) : entry.expires()), dateFormat.format(entry.created())));
    }

    /**
     * Formats an IP ban message using the given IIPBanEntry.
     *
     * @param entry The IIPBanEntry containing the ban details.
     * @return The formatted IP ban message.
     */
    @Contract("_, -> new")
    public static @NotNull String formatIpBanMessage(@NotNull IIPBanEntry entry) {
        return ChatColor.translateAlternateColorCodes('&', String.format(BAN_MESSAGE_TEMPLATE, entry.source(), entry.reason(), (entry.expires() instanceof Date ? dateFormat.format(entry.expires()) : entry.expires()), dateFormat.format(entry.created())));
    }
}
