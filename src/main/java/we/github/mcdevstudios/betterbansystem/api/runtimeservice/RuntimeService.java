/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.runtimeservice;

public class RuntimeService {

    public static boolean isSpigot() {
        try {
            Class<?> serverClass = Class.forName("org.bukkit.craftbukkit.CraftServer");
            org.bukkit.Server server = org.bukkit.Bukkit.getServer();
            return serverClass.isInstance(server);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isBungeeCord() {
        try {
            Class.forName("net.md_5.bungee.api.ProxyServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
