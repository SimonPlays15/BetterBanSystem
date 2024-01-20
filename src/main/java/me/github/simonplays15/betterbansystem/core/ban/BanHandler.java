package me.github.simonplays15.betterbansystem.core.ban;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.uuid.UUIDFetcher;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

/**
 * The BanHandler class provides methods for banning and unbanning players and IP addresses.
 */
public class BanHandler {

    /**
     * The BanHandler class provides methods for handling bans and IP bans.
     */
    @Contract(pure = true)
    public BanHandler() {
    }

    /**
     * Adds a ban entry for a target player.
     *
     * @param sender  The sender who is adding the ban.
     * @param target  The target player.
     * @param reason  The reason for the ban.
     * @param expires The expiration date of the ban. Can be null for a permanent ban.
     * @return The added ban entry.
     */
    public static @NotNull IBanEntry addBan(@NotNull BaseCommandSender sender, @NotNull String target, String reason, Date expires) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        IBanEntry banEntry = new BanEntry(targetUUID, target, sender.getName(), new Date(), getExpiryDate(expires), reason);
        addBanEntry(banEntry);
        return banEntry;
    }

    /**
     * Removes the ban for the specified target.
     *
     * @param target The name of the player for whom to remove the ban.
     */
    public static void removeBan(String target) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        BanEntry.removeFromJson(targetUUID);
    }

    /**
     * Finds a ban entry for the given UUID.
     *
     * @param uuid the UUID of the player
     * @return the ban entry for the given UUID
     */
    public static IBanEntry findBanEntry(UUID uuid) {
        return BanEntry.findEntry(uuid);
    }

    /**
     * Finds a ban entry for the given player name.
     *
     * @param playerName the name of the player
     * @return the ban entry of the player
     */
    public static IBanEntry findBanEntry(String playerName) {
        return findBanEntry(UUIDFetcher.getUUIDOrOfflineUUID(playerName));
    }

    /**
     * Finds an IP ban entry given the IP address.
     *
     * @param ipAddress The IP address to search for.
     * @return The IP ban entry matching the given IP address, or null if no match is found.
     */
    public static IIPBanEntry findIPBanEntry(String ipAddress) {
        return IPBanEntry.findEntry(ipAddress);
    }

    /**
     * Removes an IP ban for a given target.
     *
     * @param target The IP address to remove the ban for.
     */
    public static void removeIpBan(String target) {
        IPBanEntry.removeFromJson(target);
    }

    /**
     * Adds an IP ban entry.
     *
     * @param sender    the command sender who is adding the ban
     * @param ipAddress the IP address to ban
     * @param reason    the reason for the ban
     * @param expires   the date the ban expires (null for permanent ban)
     * @return the IP ban entry that was added
     */
    public static @NotNull IIPBanEntry addIpBan(@NotNull BaseCommandSender sender, String ipAddress, String reason, Date expires) {
        IPBanEntry entry = new IPBanEntry(ipAddress, sender.getName(), new Date(), getExpiryDate(expires), reason);
        addIpBanEntry(entry);
        return entry;
    }

    /**
     * Adds an IP ban entry.
     *
     * @param entry The IPBanEntry object representing the ban entry to be added.
     */
    private static void addIpBanEntry(IPBanEntry entry) {
        IPBanEntry.saveToJson(entry);
    }

    /**
     * Adds a ban entry to the system.
     *
     * @param entry the ban entry to be added
     */
    private static void addBanEntry(IBanEntry entry) {
        BanEntry.saveToJson(entry);
    }

    /**
     * Returns the expiry date for a ban entry.
     *
     * @param expires The expiration date of the ban. Can be null for a permanent ban.
     * @return The expiry date of the ban. If expires is null, returns the string "forever". Otherwise, returns the input date.
     */
    @Contract(value = "null -> new; !null -> param1", pure = true)
    private static @NotNull Object getExpiryDate(Date expires) {
        return expires == null ? "forever" : expires;
    }

}
