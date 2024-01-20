package me.github.simonplays15.betterbansystem.spigot.event.events;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.ban.BanHandler;
import me.github.simonplays15.betterbansystem.core.ban.IBanEntry;
import me.github.simonplays15.betterbansystem.core.ban.IIPBanEntry;
import me.github.simonplays15.betterbansystem.core.chat.StringFormatter;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The PlayerLoginEvents class handles the login event for players.
 */
public class PlayerLoginEvents implements Listener {
    /**
     * Handles the login event for players.
     */
    @EventHandler
    public void onLogin(@NotNull PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!handleBan(event, BanHandler.findIPBanEntry(event.getAddress().getHostAddress())))
            handleBan(event, BanHandler.findBanEntry(player.getUniqueId()));
    }

    /**
     * Handles the ban for a player during login.
     *
     * @param event    The PlayerLoginEvent triggered during player login.
     * @param banEntry The ban entry for the player.
     */
    @Contract("_, null -> false")
    private boolean handleBan(@NotNull PlayerLoginEvent event, Object banEntry) {
        if (banEntry != null) {
            GlobalLogger.getLogger().warn(event.getPlayer().getName(), "tried to login but is banned from the server.");
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            if (banEntry instanceof IIPBanEntry) {
                event.setKickMessage(StringFormatter.formatIpBanMessage((IIPBanEntry) banEntry));
                return true;
            }
            if (banEntry instanceof IBanEntry) {
                event.setKickMessage(StringFormatter.formatBanMessage((IBanEntry) banEntry));
                return true;
            }
        }
        return false;
    }
}
