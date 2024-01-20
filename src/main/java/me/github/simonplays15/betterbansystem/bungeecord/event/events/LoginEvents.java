package me.github.simonplays15.betterbansystem.bungeecord.event.events;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.ban.BanHandler;
import me.github.simonplays15.betterbansystem.core.ban.IBanEntry;
import me.github.simonplays15.betterbansystem.core.ban.IIPBanEntry;
import me.github.simonplays15.betterbansystem.core.chat.StringFormatter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The LoginEvents class is a listener class that handles login events.
 */
public class LoginEvents implements Listener {

    /**
     * Handles the login event.
     *
     * @param event The login event object.
     */
    @EventHandler
    public void loginEvent(@NotNull LoginEvent event) {
        PendingConnection connection = event.getConnection();
        UUID uuid = connection.getUniqueId();
        IBanEntry banEntry = BanHandler.findBanEntry(uuid);
        if (banEntry != null) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent(StringFormatter.formatBanMessage(banEntry)));
        }
        String address = connection.getVirtualHost().getHostName();
        IIPBanEntry ipBanEntry = BanHandler.findIPBanEntry(address);
        if (ipBanEntry != null) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent(StringFormatter.formatIpBanMessage(ipBanEntry)));
        }

    }

}
