/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import we.github.mcdevstudios.betterbansystem.bungeecord.event.EventManager;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

public class BungeeCore extends Plugin {

    @Override
    public void onEnable() {
        GlobalLogger.getLogger().info("Enabling BetterBanSystem for Spigot");
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
