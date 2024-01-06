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

public class MuteManager {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public MuteManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

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

    public void stop() {
        GlobalLogger.getLogger().debug("Shutting Down MuteManager Scheduler Service");
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
