package me.github.simonplays15.betterbansystem.spigot;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import me.github.simonplays15.betterbansystem.spigot.event.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotCore extends JavaPlugin {

    public void onEnable() {
        GlobalLogger.getLogger().info("Enabling BetterBanSystem for Spigot");
        BetterBanSystem core;
        try {
            core = new BetterBanSystem(this.getDataFolder());
        } catch (NoClassDefFoundError | Exception e) {
            GlobalLogger.getLogger().error("Failed to initialize BetterBanSystem:", e);
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