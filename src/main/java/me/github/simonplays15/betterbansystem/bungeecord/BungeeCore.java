package me.github.simonplays15.betterbansystem.bungeecord;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.bungeecord.event.EventManager;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * The BungeeCore class represents the core plugin for BetterBanSystem on BungeeCord.
 */
public class BungeeCore extends Plugin {

    /**
     * The BungeeCore class represents the core plugin for BetterBanSystem on BungeeCord.
     */
    protected BungeeCore instance;

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        instance = this;
        GlobalLogger.getLogger().info("Enabling BetterBanSystem for BungeeCord");
        BetterBanSystem betterBanSystem;
        try {
            betterBanSystem = new BetterBanSystem(this.getDataFolder()) {
                /**
                 * Retrieves the currently running plugin.
                 *
                 * @return The running plugin object.
                 */
                @Override
                public Object getRunningPlugin() {
                    return instance;
                }

                @Override
                public void dispatchCommand(String command) {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
                }
            };
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        betterBanSystem.saveConfig();

        new CommandHandler(this);
        new EventManager(this);
    }
}
