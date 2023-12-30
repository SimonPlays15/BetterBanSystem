/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.event.events.login;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;

import java.net.InetAddress;

public class PlayerLoginEvents implements Listener {
    /**
     * Handles the login event for players.
     *
     * @param event the player login event
     */
    @EventHandler
    public void onLogin(@NotNull PlayerLoginEvent event) {
        Player player = event.getPlayer();
        InetAddress address = event.getAddress();

        handleBan(event, BanHandler.findIPBanEntry(address.getHostAddress()), "You are IP-Banned from the server.");
        handleBan(event, BanHandler.findBanEntry(player.getUniqueId()), "You are banned from the server.");
    }

    /**
     * Handles the ban entry for a player during login.
     * If a ban entry exists, the player is kicked with a ban message.
     *
     * @param event      the PlayerLoginEvent
     * @param banEntry   the ban entry object
     * @param banMessage the ban message to display to the kicked player
     */
    private void handleBan(@NotNull PlayerLoginEvent event, Object banEntry, String banMessage) {
        if (banEntry != null) {
            GlobalLogger.getLogger().warn(event.getPlayer().getName(), "tried to login but is banned from the server.");
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            event.setKickMessage(banMessage);
        }
    }
}
