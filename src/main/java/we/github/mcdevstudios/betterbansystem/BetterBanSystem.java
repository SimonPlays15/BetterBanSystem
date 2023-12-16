/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem;

import org.bukkit.plugin.java.JavaPlugin;
import we.github.mcdevstudios.betterbansystem.command.CommandHandler;
import we.github.mcdevstudios.betterbansystem.event.EventManager;
import we.github.mcdevstudios.betterbansystem.utils.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.utils.language.LanguageFile;

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
        return prefix;
    }

    public void onEnable() {
        instance = this;
        globalLogger = new GlobalLogger(this.getDescription().getName());
        new CommandHandler();
        new EventManager(this);
        languageFile = new LanguageFile(getDataFolder() + "/BetterBanSystem/language/" + getConfig().getString("chat.language" + ".yml"));

        getGlobalLogger().info("Plugin enabled");

        if (getConfig().getBoolean("chat.usePrefix")) {
            prefix = getConfig().getString("chat.prefix") + "§r ";
        } else {
            prefix = "";
        }
    }

    public void onDisable() {
    }

    public void onLoad() {
    }
}