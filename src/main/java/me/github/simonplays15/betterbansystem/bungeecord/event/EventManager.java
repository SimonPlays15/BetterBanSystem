package me.github.simonplays15.betterbansystem.bungeecord.event;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.bungeecord.event.events.ChatEvents;
import me.github.simonplays15.betterbansystem.bungeecord.event.events.LoginEvents;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * The EventManager class is responsible for managing event listeners for a Plugin.
 */

public class EventManager {

    /**
     * The private final Plugin object associated with the EventManager class.
     */
    private final Plugin plugin;

    /**
     * Constructs a new EventManager object for the given Plugin.
     *
     * @param plugin The Plugin object to be associated with the EventManager.
     */
    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        this.registerEvent(new LoginEvents());
        this.registerEvent(new ChatEvents());
    }

    /**
     * Registers an event listener with the plugin's event manager.
     *
     * @param listener The listener to be registered.
     */
    public void registerEvent(Listener listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, listener);
    }

}
