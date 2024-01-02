/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import we.github.mcdevstudios.betterbansystem.spigot.event.events.chat.PlayerChatEvents;
import we.github.mcdevstudios.betterbansystem.spigot.event.events.login.PlayerLoginEvents;

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
