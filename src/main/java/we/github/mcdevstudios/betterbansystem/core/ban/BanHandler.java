/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.ban;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

import java.util.Date;
import java.util.UUID;

/**
 * The BanHandler class provides methods for adding bans and IP bans.
 */
public class BanHandler {

    public BanHandler() {
    }

    public static IBanEntry addBan(@NotNull BaseCommandSender sender, @NotNull String target, String reason, Date expires) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        IBanEntry banEntry = new BanEntry(targetUUID, target, sender.getName(), new Date(), getExpiryDate(expires), reason);
        addBanEntry(banEntry);
        return banEntry;
    }

    public static void removeBan(String target) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        BanEntry.removeFromJson(targetUUID);
    }

    public static IBanEntry findBanEntry(UUID uuid) {
        return BanEntry.findEntry(uuid);
    }

    public static IBanEntry findBanEntry(String playerName) {
        return findBanEntry(UUIDFetcher.getUUIDOrOfflineUUID(playerName));
    }

    public static IIPBanEntry findIPBanEntry(String ipAddress) {
        return IPBanEntry.findEntry(ipAddress);
    }

    public static void removeIpBan(String target) {
        IPBanEntry.removeFromJson(target);
    }

    /**
     * Adds an IP ban entry.
     *
     * @param sender    the sender of the ban
     * @param ipAddress the IP address to ban
     * @param reason    the reason for the ban
     * @param expires   the expiration date of the ban
     */
    public static IIPBanEntry addIpBan(@NotNull BaseCommandSender sender, String ipAddress, String reason, Date expires) {
        IPBanEntry entry = new IPBanEntry(ipAddress, sender.getName(), new Date(), getExpiryDate(expires), reason);
        addIpBanEntry(entry);
        return entry;
    }

    private static void addIpBanEntry(IPBanEntry entry) {
        IPBanEntry.saveToJson(entry);
    }

    private static void addBanEntry(IBanEntry entry) {
        BanEntry.saveToJson(entry);
    }

    @Contract(value = "null -> new; !null -> param1", pure = true)
    private static @NotNull Object getExpiryDate(Date expires) {
        return expires == null ? "forever" : expires;
    }

}
