package me.github.simonplays15.betterbansystem.bungeecord.event.events;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.mute.MuteHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatEvents implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayers().stream().filter(g -> g.getPendingConnection().getSocketAddress().equals(event.getSender().getSocketAddress())).findFirst().orElse(null);
        if (player == null)
            return;

        if (MuteHandler.findMuteEntry(player.getUniqueId()) != null) {
            event.setCancelled(true);
            player.sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + "§4You cannot chat while you're muted"));
        }

    }

}
