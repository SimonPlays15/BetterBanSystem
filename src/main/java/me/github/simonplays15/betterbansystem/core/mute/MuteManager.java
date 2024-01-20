package me.github.simonplays15.betterbansystem.core.mute;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.runtimeservice.RuntimeService;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The MuteManager class manages mute entries in a system.
 * It provides methods to start and stop the mute manager.
 * The mute manager periodically checks for expired mute entries and takes appropriate actions.
 */
public class MuteManager {

    /**
     * The scheduler variable is an instance of ScheduledExecutorService.
     * It is used to schedule recurring tasks such as checking for expired mute entries.
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * The MuteManager class manages mute entries in a system.
     * It provides methods to start and stop the mute manager.
     * The mute manager periodically checks for expired mute entries and takes appropriate actions.
     */
    public MuteManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    /**
     * The start method is used to start the mute manager.
     * It schedules a task to periodically check for expired mute entries and take appropriate actions.
     */
    public void start() {
        Runnable checkExpiredBanEntry = () -> {
            for (IMuteEntry entry : MuteEntry.getAllEntries()) {
                if (entry.expires() instanceof Date && ((Date) entry.expires()).before(new Date())) {
                    if (RuntimeService.isSpigot()) {
                        if (org.bukkit.Bukkit.getPlayer(entry.uuid()) != null) {
                            Objects.requireNonNull(org.bukkit.Bukkit.getPlayer(entry.uuid())).sendMessage(BetterBanSystem.getInstance().getPrefix() + "§aYour mute is expired. You can now chat again.");
                        }
                    } else if (RuntimeService.isBungeeCord()) {
                        if (net.md_5.bungee.api.ProxyServer.getInstance().getPlayer(entry.uuid()) != null) {
                            net.md_5.bungee.api.ProxyServer.getInstance().getPlayer(entry.uuid()).sendMessage(new TextComponent(BetterBanSystem.getInstance().getPrefix() + "§aYour mute is expired. You can now chat again."));
                        }
                    }
                    MuteEntry.removeEntry(entry);
                    GlobalLogger.getLogger().debug("Mute from", entry.name(), "expired.", entry.toString());
                }
            }
        };
        scheduler.scheduleAtFixedRate(checkExpiredBanEntry, 6, 1, TimeUnit.SECONDS);
    }

    /**
     * Stops the MuteManager.
     * <p>
     * This method shuts down the scheduler used by the MuteManager class. It waits for a maximum of 5 seconds for the
     * scheduler to terminate gracefully. If the scheduler has not terminated within the timeout, this method forcibly
     * shuts it down.
     * <p>
     * If an InterruptedException occurs while waiting for the scheduler to terminate, the scheduler is forcibly shut down
     * and an error message is logged using the GlobalLogger class.
     */
    public void stop() {
        scheduler.shutdown();
        try {
            if (scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            GlobalLogger.getLogger().error(e.getMessage());
        }
    }
}
