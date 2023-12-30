/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.chat.ChatColor;
import we.github.mcdevstudios.betterbansystem.api.files.BaseConfig;
import we.github.mcdevstudios.betterbansystem.api.files.BasePluginDescription;
import we.github.mcdevstudios.betterbansystem.api.files.LanguageFile;
import we.github.mcdevstudios.betterbansystem.api.files.ResourceFile;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.api.permissions.BungeeCordDefaultHandler;
import we.github.mcdevstudios.betterbansystem.api.permissions.PermissionsHandlerType;
import we.github.mcdevstudios.betterbansystem.api.permissions.PermissionsManager;
import we.github.mcdevstudios.betterbansystem.api.permissions.SpigotPermissionsHandler;
import we.github.mcdevstudios.betterbansystem.api.runtimeservice.RuntimeService;

import java.io.File;

public class BetterBanSystem {

    private static BetterBanSystem instance;

    private final File dataFolder;
    private final File configFile;
    private final BasePluginDescription basePluginDescription;
    public String prefix;
    public BaseConfig config;
    private LanguageFile languageFile;
    private PermissionsManager manager;

    public BetterBanSystem(File dataFolder) {
        instance = this;
        this.dataFolder = dataFolder;
        ResourceFile resourceFile = new ResourceFile(this.dataFolder);

        if (RuntimeService.isSpigot())
            this.basePluginDescription = new BasePluginDescription(resourceFile.getResource("plugin.yml"));
        else if (RuntimeService.isBungeeCord())
            this.basePluginDescription = new BasePluginDescription(resourceFile.getResource("bungee.yml"));
        else
            throw new RuntimeException("Failed to load the correct runtime service. Is this plugin runnin on an Spigot or BungeeCord Server?");

        this.configFile = new File(this.dataFolder, "config.yml");
        resourceFile.saveResource("config.yml", true);
        resourceFile.saveResource("language/de_DE.yml", true);
        resourceFile.saveResource("language/en_US.yml", true);
        this.config = new BaseConfig();
        this.config.load(this.configFile);
        GlobalLogger.getLogger().info(this.getConfig().getString("chat.prefix"));
        if (this.config.getBoolean("chat.usePrefix")) {
            this.prefix = ChatColor.translateAlternateColorCodes('&', this.config.getString("chat.prefix", "§6[§$cBetterBanSystem§$6]§r "));
        } else {
            this.prefix = "";
        }

        this.loadLanguage(this.config.getString("chat.language", "en_US"));
        this.loadPermissionsSystem(PermissionsHandlerType.valueOf(this.config.getString("permissions.system", "SPIGOT").toUpperCase()));
    }

    @Contract(pure = true)
    public static BetterBanSystem getInstance() {
        return instance;
    }

    public String getPrefix() {
        if (this.prefix == null) {
            return "§6[§cBetterBanSystem§6]§r ";
        } else if (this.prefix.isEmpty()) {
            return "§r";
        }
        return prefix + "§r ";
    }

    public void loadLanguage(@NotNull String language) {
        if (!language.endsWith(".yml"))
            language += ".yml";
        this.languageFile = new LanguageFile(this.getDataFolder().getPath() + "/language/" + language);
    }

    public void loadPermissionsSystem(PermissionsHandlerType type) {
        try {
            this.manager = PermissionsManager.getHandler(type);
        } catch (Exception e) {
            GlobalLogger.getLogger().error("Failed to get a permission manager. Falling back to default", e);
        }
        if (RuntimeService.isSpigot())
            this.manager = new SpigotPermissionsHandler();
        else if (RuntimeService.isBungeeCord())
            this.manager = new BungeeCordDefaultHandler();
    }

    public void reloadConfig() {
        if (this.config == null)
            this.config = new BaseConfig();
        this.config.load(this.configFile);
    }

    public void saveConfig() {
        this.config.save(this.configFile);
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public BaseConfig getConfig() {
        return config;
    }

    public LanguageFile getLanguageFile() {
        return languageFile;
    }

    public PermissionsManager getPermissionsManager() {
        return manager;
    }

    public BasePluginDescription getPluginDescription() {
        return basePluginDescription;
    }
}
