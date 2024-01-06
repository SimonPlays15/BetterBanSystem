package me.github.simonplays15.betterbansystem.bungeecord;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.bungeecord.event.EventManager;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCore extends Plugin {

    @Override
    public void onEnable() {
        GlobalLogger.getLogger().info("Enabling BetterBanSystem for BungeeCord");
        BetterBanSystem betterBanSystem;
        try {
            betterBanSystem = new BetterBanSystem(this.getDataFolder());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        betterBanSystem.saveConfig();

        new CommandHandler(this);
        new EventManager(this);
    }
}
