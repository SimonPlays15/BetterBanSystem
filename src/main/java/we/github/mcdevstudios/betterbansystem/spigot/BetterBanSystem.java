/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.spigot;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import we.github.mcdevstudios.betterbansystem.api.language.LanguageFile;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.spigot.command.CommandHandler;
import we.github.mcdevstudios.betterbansystem.spigot.event.EventManager;

public class BetterBanSystem extends JavaPlugin {
    private static BetterBanSystem instance;
    private static GlobalLogger globalLogger;
    private static LanguageFile languageFile;
    private static String prefix = "§6[§cBetterBanSystem§6]§r ";

    public static BetterBanSystem getInstance() {
        return instance;
    }

    public static GlobalLogger getGlobalLogger() {
        return globalLogger;
    }

    public static LanguageFile getLanguageFile() {
        return languageFile;
    }

    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public void onEnable() {
        instance = this;
        globalLogger = new GlobalLogger(this.getDescription().getName());
        new CommandHandler();
        new EventManager(this);
        languageFile = new LanguageFile(getDataFolder() + "/BetterBanSystem/language/" + getConfig().getString("chat.language" + ".yml"));
        if (getConfig().getBoolean("chat.usePrefix")) {
            prefix = getConfig().getString("chat.prefix") + "§r ";
        } else {
            prefix = "";
        }

        getGlobalLogger().info("Plugin enabled");
    }

    public void onDisable() {
    }

    public void onLoad() {
    }
}