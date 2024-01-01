/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.warn;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class WarnHandler {

    /**
     * Adds a warning for a target player.
     *
     * @param sender the sender of the warning
     * @param target the name of the target player
     * @param reason the reason for the warning
     */
    public static void addWarn(@NotNull BaseCommandSender sender, String target, String reason) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        IWarnEntry potentialWarnEntry = WarnEntry.findEntry(targetUUID);
        if (potentialWarnEntry == null)
            potentialWarnEntry = new WarnEntry(targetUUID, target, new ArrayList<>());
        potentialWarnEntry.addWarn(new Warn(WarnEntry.idGenerator.getAndIncrement(), sender.getName(), new Date(), reason));

        WarnEntry.saveToJson(potentialWarnEntry);
    }

    /**
     * Removes a warning for a target player.
     *
     * @param target the player name or UUID for which to remove the warning
     */
    public static void removeWarn(String target, int id) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        IWarnEntry potentialWarnEntry = WarnEntry.findEntry(targetUUID);
        potentialWarnEntry.removeWarn(id);
        WarnEntry.saveToJson(potentialWarnEntry);
    }
}
