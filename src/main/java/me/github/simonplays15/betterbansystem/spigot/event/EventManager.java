package me.github.simonplays15.betterbansystem.spigot.event;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.spigot.event.events.PlayerChatEvents;
import me.github.simonplays15.betterbansystem.spigot.event.events.PlayerLoginEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * The EventManager class handles the registration and management of events.
 */
public class EventManager {
    /**
     * The private final Plugin object associated with the EventManager class.
     */
    private final Plugin plugin;

    /**
     * The EventManager class handles the registration and management of events.
     */
    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        this.registerEvent(new PlayerLoginEvents());
        this.registerEvent(new PlayerChatEvents());
    }

    /**
     * Registers an event listener with this EventManager.
     *
     * @param listener The listener to be registered.
     */
    public void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this.plugin);
    }
}
