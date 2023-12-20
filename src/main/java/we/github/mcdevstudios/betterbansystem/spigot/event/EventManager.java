/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import we.github.mcdevstudios.betterbansystem.spigot.BetterBanSystem;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Objects;

public class EventManager {
    private final Plugin plugin;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        this.registerEvents();
    }

    private void registerEvents() {
        String packageName = "we.github.mcdevstudios.betterbansystem.event.events"; // Base

        File directory = new File(this.plugin.getDataFolder(), packageName.replace(".", "/"));
        if (!directory.exists())
            return;

        File[] files = Objects.requireNonNull(directory.listFiles());
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> eventClass = Class.forName(className);

                    if (Listener.class.isAssignableFrom(eventClass) && !eventClass.equals(Listener.class)) {
                        Constructor<?> constructor = eventClass.getConstructor();

                        Listener listener = (Listener) constructor.newInstance();

                        Bukkit.getPluginManager().registerEvents(listener, this.plugin);
                    }

                } catch (Exception ex) {
                    BetterBanSystem.getGlobalLogger().error("Failed to load EventListeners", ex);
                }
            }
        }
    }

    public void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this.plugin);
    }
}
