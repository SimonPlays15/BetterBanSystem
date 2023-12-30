/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.spigot.command.CommandHandler;
import we.github.mcdevstudios.betterbansystem.spigot.event.EventManager;

public class SpigotCore extends JavaPlugin {

    public void onEnable() {
        BetterBanSystem core = new BetterBanSystem(this.getDataFolder());

        core.saveConfig();

        new CommandHandler();
        new EventManager(this);
    }

    public void onDisable() {
        GlobalLogger.getLogger().destroyLogger();
    }

    public void onLoad() {
    }
}