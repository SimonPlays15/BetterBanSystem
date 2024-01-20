package me.github.simonplays15.betterbansystem.core.warn;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.uuid.UUIDFetcher;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.chat.ChatColor;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * The WarnHandler class provides methods for adding and removing warnings for a target player.
 * Warnings are associated with a target player and can be issued by a command sender.
 * Warnings consist of a reason and a unique identifier.
 */
public class WarnHandler {

    /**
     * Adds a warning for a target player with a given reason.
     *
     * @param sender the command sender issuing the warning
     * @param target the name or UUID of the target player
     * @param reason the reason for the warning
     */
    public static void addWarn(@NotNull BaseCommandSender sender, String target, String reason) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        IWarnEntry potentialWarnEntry = WarnEntry.findEntry(targetUUID);
        if (potentialWarnEntry == null)
            potentialWarnEntry = new WarnEntry(targetUUID, target, new ArrayList<>());
        potentialWarnEntry.addWarn(new Warn(WarnEntry.idGenerator.getAndIncrement(), sender.getName(), new Date(), reason));
        WarnEntry.saveToJson(potentialWarnEntry);
        String commandString = BetterBanSystem.getInstance().getConfig().getString("warns.actions." + potentialWarnEntry.warns().size(), "");
        if (!commandString.isEmpty()) {
            commandString = commandString.replace("%p", target);
            BetterBanSystem.getInstance().dispatchCommand(ChatColor.translateAlternateColorCodes('&', commandString));
        }
    }

    /**
     * Removes a specific warning from the target players warn entry.
     *
     * @param target The name of the target player.
     * @param id     The unique identifier of the warning to be removed.
     */
    public static void removeWarn(String target, int id) {
        UUID targetUUID = UUIDFetcher.getUUIDOrOfflineUUID(target);
        IWarnEntry potentialWarnEntry = WarnEntry.findEntry(targetUUID);
        potentialWarnEntry.removeWarn(id);
        WarnEntry.saveToJson(potentialWarnEntry);
    }
}
