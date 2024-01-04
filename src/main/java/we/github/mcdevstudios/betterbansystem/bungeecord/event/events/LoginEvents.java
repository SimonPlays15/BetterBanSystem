/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.bungeecord.event.events;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.ban.BanHandler;
import we.github.mcdevstudios.betterbansystem.core.ban.IBanEntry;
import we.github.mcdevstudios.betterbansystem.core.ban.IIPBanEntry;
import we.github.mcdevstudios.betterbansystem.core.chat.StringFormatter;

import java.util.UUID;

public class LoginEvents implements Listener {

    @EventHandler
    public void loginEvent(@NotNull LoginEvent event) {
        PendingConnection connection = event.getConnection();
        UUID uuid = connection.getUniqueId();
        String address = connection.getVirtualHost().getAddress().getHostAddress();
        IBanEntry banEntry = BanHandler.findBanEntry(uuid);
        if (banEntry != null) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent(StringFormatter.formatBanMessage(banEntry)));
        }
        IIPBanEntry ipBanEntry = BanHandler.findIPBanEntry(address);
        if (ipBanEntry != null) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent(StringFormatter.formatIpBanMessage(ipBanEntry)));
        }

    }

}
