package me.github.simonplays15.betterbansystem.spigot;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import me.github.simonplays15.betterbansystem.spigot.event.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents the core plugin class for Spigot.
 */
public class SpigotCore extends JavaPlugin {

    /**
     * The `instance` variable represents an instance of the `SpigotCore` class, which is a core plugin class for Spigot.
     * <p>
     * This variable is declared as `protected`, which means it can be accessed by subclasses within the same package and by subclasses in different packages.
     * <p>
     * The purpose of this variable is to hold a reference to the running plugin instance. It is assigned with the value of `this` in the `onEnable()` method.
     * <p>
     * This variable can be accessed by other methods within the `SpigotCore` class or its subclasses to access the running plugin object.
     * <p>
     * Example usage:
     * <p>
     * SpigotCore plugin = instance;
     *
     * @see SpigotCore
     */
    protected SpigotCore instance;

    /**
     * Called when the plugin is enabled.
     */
    public void onEnable() {
        instance = this;
        BetterBanSystem core;
        try {
            core = new BetterBanSystem(this.getDataFolder()) {
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
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            };
        } catch (NoClassDefFoundError | Exception e) {
            GlobalLogger.getLogger().error("Failed to initialize BetterBanSystem:", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        GlobalLogger.getLogger().info("Enabling BetterBanSystem for Spigot");
        core.saveConfig();
        new CommandHandler();
        new EventManager(this);
    }

    /**
     * This method is called when the plugin is being disabled.
     * <p>
     * During the onDisable() method, any clean up or finalization tasks can be performed. This method is usually used to save data, close connections, or perform any necessary cleanup
     * operations before the plugin is completely shut down.
     * <p>
     * Note that when the plugin is disabled, it will no longer receive events or be able to execute commands.
     */
    public void onDisable() {

    }

    /**
     * This method is called when the plugin is being loaded by the server.
     * It is responsible for any necessary preloading tasks or initializations.
     * <p>
     * This method is automatically called by the Spigot server and should not be called manually.
     * <p>
     * Note: This method does not have a specific implementation in the given code sample.
     * Developers should extend this class and override the `onLoad` method to provide
     * any specific functionality required during plugin loading.
     *
     * @see SpigotCore
     * @see JavaPlugin#onLoad()
     */
    public void onLoad() {
    }
}