/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.event.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.core.ban.IBanEntry;
import we.github.mcdevstudios.betterbansystem.core.ban.IIPBanEntry;
import we.github.mcdevstudios.betterbansystem.core.chat.StringFormatter;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

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

        handleBan(event, BanHandler.findIPBanEntry(address.getHostAddress()));
        handleBan(event, BanHandler.findBanEntry(player.getUniqueId()));
    }

    /**
     * Handles the ban entry for a player during login.
     * If a ban entry exists, the player is kicked with a ban message.
     *
     * @param event    the PlayerLoginEvent
     * @param banEntry the ban entry object
     */
    private void handleBan(@NotNull PlayerLoginEvent event, Object banEntry) {
        if (banEntry != null) {
            GlobalLogger.getLogger().warn(event.getPlayer().getName(), "tried to login but is banned from the server.");
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            if (banEntry instanceof IBanEntry)
                event.setKickMessage(StringFormatter.formatBanMessage((IBanEntry) banEntry));
            else if (banEntry instanceof IIPBanEntry)
                event.setKickMessage(StringFormatter.formatIpBanMessage((IIPBanEntry) banEntry));
        }
    }
}
