/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.spigot.command.CommandHandler;
import we.github.mcdevstudios.betterbansystem.spigot.event.EventManager;

public class SpigotCore extends JavaPlugin {

    private BetterBanSystem core;

    public void onEnable() {
        core.onEnable();
        core.saveConfig();

        new CommandHandler();
        new EventManager(this);
    }

    public void onDisable() {
        this.core.onDisable();
        GlobalLogger.getLogger().destroyLogger();
    }

    public void onLoad() {
        this.core = new BetterBanSystem(this.getDataFolder()) {
            /**
             *
             */
            @Override
            public void onEnable() {
                Bukkit.getConsoleSender().sendMessage("");
            }

            /**
             *
             */
            @Override
            public void onDisable() {
                Bukkit.getConsoleSender().sendMessage("");
            }

            /**
             *
             */
            @Override
            public void onLoad() {
                Bukkit.getConsoleSender().sendMessage("");
            }
        };
        this.core.onLoad();
    }
}