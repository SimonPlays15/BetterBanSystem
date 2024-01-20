package me.github.simonplays15.betterbansystem.api.runtimeservice;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * The RuntimeService class provides utility methods for detecting the runtime environment in which the application is running.
 */
public class RuntimeService {

    /**
     * Checks if the application is running in a Spigot environment.
     *
     * @return true if running in a Spigot environment, false otherwise
     */
    public static boolean isSpigot() {
        try {
            Class.forName("org.bukkit.plugin.java.JavaPlugin");
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    /**
     * Checks if the application is running in a BungeeCord environment.
     *
     * @return true if running in BungeeCord, false otherwise
     */
    public static boolean isBungeeCord() {
        try {
            Class.forName("net.md_5.bungee.api.plugin.Plugin");
            return true;
        } catch (ClassNotFoundException ignored) {
        }

        return false;
    }

}
