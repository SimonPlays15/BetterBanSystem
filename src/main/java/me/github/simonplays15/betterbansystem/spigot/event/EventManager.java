package me.github.simonplays15.betterbansystem.spigot.event;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.spigot.event.events.PlayerChatEvents;
import me.github.simonplays15.betterbansystem.spigot.event.events.PlayerLoginEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EventManager {
    private final Plugin plugin;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        this.registerEvent(new PlayerLoginEvents());
        this.registerEvent(new PlayerChatEvents());
    }

    public void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this.plugin);
    }
}
