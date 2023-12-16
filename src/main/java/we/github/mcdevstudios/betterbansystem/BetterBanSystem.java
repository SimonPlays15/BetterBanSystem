/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem;

import org.bukkit.plugin.java.JavaPlugin;
import we.github.mcdevstudios.betterbansystem.command.CommandHandler;
import we.github.mcdevstudios.betterbansystem.event.EventManager;

public class BetterBanSystem extends JavaPlugin {
    private static BetterBanSystem instance;

    public static BetterBanSystem getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        new CommandHandler();
        new EventManager(this);
    }

    public void onDisable() {
    }

    public void onLoad() {
    }
}