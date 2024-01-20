package me.github.simonplays15.betterbansystem.core.mute;

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
 * The MuteHandler class provides methods for muting and unmuting players.
 */
public class MuteHandler {

    /**
     * The MuteHandler class provides methods for muting and unmuting players.
     */
    public MuteHandler() {
    }

    /**
     * Adds a mute for a target player.
     *
     * @param sender  The command sender.
     * @param target  The name of the target player.
     * @param reason  The reason for the mute.
     * @param expires The expiration date of the mute.
     */
    public static void addMute(@NotNull BaseCommandSender sender, @NotNull String target, String reason, Date expires) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        MuteEntry banEntry = new MuteEntry(targetUUID, target, sender.getName(), new Date(), getExpiryDate(expires), reason);
        addMuteEntry(banEntry);
    }

    /**
     * Removes the mute for a specified target.
     *
     * @param target the target for whom the mute needs to be removed
     */
    public static void removeMute(String target) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        MuteEntry.removeFromJson(targetUUID);
    }

    /**
     * This method finds a mute entry based on the provided UUID.
     *
     * @param uuid The UUID of the player for which to find the mute entry.
     * @return The mute entry found for the given UUID.
     */
    public static IMuteEntry findMuteEntry(UUID uuid) {
        return MuteEntry.findEntry(uuid);
    }

    /**
     * Finds the mute entry for the given player UUID.
     *
     * @param playerName The player name.
     * @return The mute entry corresponding to the player UUID, or null if no mute entry is found.
     */
    public static IMuteEntry findMuteEntry(String playerName) {
        return findMuteEntry(UUIDFetcher.getUUIDOrOfflineUUID(playerName));
    }

    /**
     * Adds a mute entry to the system.
     *
     * @param entry the mute entry to add
     */
    private static void addMuteEntry(IMuteEntry entry) {
        MuteEntry.saveToJson(entry);
    }

    /**
     * Retrieves the expiry date for a given date.
     * If expires is null, it returns "forever".
     *
     * @param expires the expiration date
     * @return the expiry date as a String ("forever" if expires is null)
     */
    @Contract(value = "null -> new; !null -> param1", pure = true)
    private static @NotNull Object getExpiryDate(Date expires) {
        return expires == null ? "forever" : expires;
    }

}
