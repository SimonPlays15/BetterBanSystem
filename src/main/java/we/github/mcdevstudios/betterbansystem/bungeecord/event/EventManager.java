/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.bungeecord.event;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import we.github.mcdevstudios.betterbansystem.bungeecord.event.events.ChatEvents;
import we.github.mcdevstudios.betterbansystem.bungeecord.event.events.LoginEvents;

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
