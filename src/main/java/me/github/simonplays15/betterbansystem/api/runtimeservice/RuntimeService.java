package me.github.simonplays15.betterbansystem.api.runtimeservice;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

public class RuntimeService {

    public static boolean isSpigot() {
        try {
            Class.forName("org.bukkit.plugin.java.JavaPlugin");
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    public static boolean isBungeeCord() {
        try {
            Class.forName("net.md_5.bungee.api.plugin.Plugin");
            return true;
        } catch (ClassNotFoundException ignored) {
        }

        return false;
    }

}
