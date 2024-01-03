/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.spigot.event.EventManager;

public class SpigotCore extends JavaPlugin {

    public void onEnable() {
        GlobalLogger.getLogger().info("Enabling BetterBanSystem for Spigot");
        BetterBanSystem core;
        try {
            core = new BetterBanSystem(this.getDataFolder());
        } catch (RuntimeException e) {
            GlobalLogger.getLogger().error(e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        core.saveConfig();
        new CommandHandler();
        new EventManager(this);
    }

    public void onDisable() {

    }

    public void onLoad() {
    }
}