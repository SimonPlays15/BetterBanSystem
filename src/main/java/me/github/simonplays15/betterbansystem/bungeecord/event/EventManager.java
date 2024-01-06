package me.github.simonplays15.betterbansystem.bungeecord.event;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.bungeecord.event.events.ChatEvents;
import me.github.simonplays15.betterbansystem.bungeecord.event.events.LoginEvents;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class EventManager {

    private final Plugin plugin;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        this.registerEvent(new LoginEvents());
        this.registerEvent(new ChatEvents());
    }

    public void registerEvent(Listener listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, listener);
    }

}
