/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.bungeecord.event.events;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import we.github.mcdevstudios.betterbansystem.core.ban.BanHandler;

import java.net.SocketAddress;
import java.util.UUID;

public class LoginEvents implements Listener {

    @EventHandler
    public void loginEvent(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        UUID uuid = connection.getUniqueId();
        SocketAddress address = connection.getSocketAddress();

        if (BanHandler.findBanEntry(uuid) != null) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent("You have been banned from the server."));
        }

        if (BanHandler.findIPBanEntry(address.toString()) != null) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent("Your IP address has been banned from the server."));
        }

    }

}
