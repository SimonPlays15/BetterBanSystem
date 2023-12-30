/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.ban;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.command.BaseCommandSender;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;

import java.util.Date;
import java.util.UUID;

/**
 * The BanHandler class provides methods for adding bans and IP bans.
 */
public class BanHandler {

    public BanHandler() {
    }

    public static void addBan(@NotNull BaseCommandSender sender, @NotNull String target, String reason, Date expires) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        IBanEntry banEntry = new BanEntry(targetUUID, target, sender.getName(), new Date(), getExpiryDate(expires), reason);
        addBanEntry(banEntry);
    }

    public static void removeBan(String target) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        BanEntry.removeFromJson(targetUUID, "banned-players.json");
    }

    public static IBanEntry findBanEntry(UUID uuid) {
        return BanEntry.findEntry(uuid, "banned-players.json");
    }

    public static IBanEntry findBanEntry(String playerName) {
        return findBanEntry(UUIDFetcher.getUUIDOrOfflineUUID(playerName));
    }

    public static IIPBanEntry findIPBanEntry(String ipAddress) {
        return IPBanEntry.findEntry(ipAddress, "banned-ips.json");
    }

    public static void removeIpBan(String target) {
        IPBanEntry.removeFromJson(target, "banned-ips.json");
    }

    /**
     * Adds an IP ban entry.
     *
     * @param sender    the sender of the ban
     * @param ipAddress the IP address to ban
     * @param reason    the reason for the ban
     * @param expires   the expiration date of the ban
     */
    public static void addIpBan(@NotNull BaseCommandSender sender, String ipAddress, String reason, Date expires) {
        IPBanEntry entry = new IPBanEntry(ipAddress, sender.getName(), new Date(), getExpiryDate(expires), reason);
        addIpBanEntry(entry);
    }

    private static void addIpBanEntry(IPBanEntry entry) {
        IPBanEntry.saveToJson(entry, "banned-ips.json");
    }

    private static void addBanEntry(IBanEntry entry) {
        BanEntry.saveToJson(entry, "banned-players.json");
    }

    @Contract(value = "null -> new; !null -> param1", pure = true)
    private static @NotNull Object getExpiryDate(Date expires) {
        return expires == null ? "forever" : expires;
    }

}
