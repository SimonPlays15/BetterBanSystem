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
 * The BanHandler class provides methods for adding bans and IP bans.
 */
public class MuteHandler {

    public MuteHandler() {
    }

    public static void addMute(@NotNull BaseCommandSender sender, @NotNull String target, String reason, Date expires) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        MuteEntry banEntry = new MuteEntry(targetUUID, target, sender.getName(), new Date(), getExpiryDate(expires), reason);
        addMuteEntry(banEntry);
    }

    public static void removeMute(String target) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        MuteEntry.removeFromJson(targetUUID);
    }

    public static IMuteEntry findMuteEntry(UUID uuid) {
        return MuteEntry.findEntry(uuid);
    }

    public static IMuteEntry findMuteEntry(String playerName) {
        return findMuteEntry(UUIDFetcher.getUUIDOrOfflineUUID(playerName));
    }

    private static void addMuteEntry(IMuteEntry entry) {
        MuteEntry.saveToJson(entry);
    }

    @Contract(value = "null -> new; !null -> param1", pure = true)
    private static @NotNull Object getExpiryDate(Date expires) {
        return expires == null ? "forever" : expires;
    }

}
