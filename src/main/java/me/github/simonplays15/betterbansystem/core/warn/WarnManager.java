package me.github.simonplays15.betterbansystem.core.warn;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WarnManager {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public WarnManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void start() {
        if (!BetterBanSystem.getInstance().getConfig().getBoolean("warns.autodelete.use"))
            return;

        String timeUnit = BetterBanSystem.getInstance().getConfig().getString("warns.autodelete.unit", "MINUTES").toUpperCase();

        Duration interval = Duration.of(BetterBanSystem.getInstance().getConfig().getLong("warns.autodelete.time"), ChronoUnit.valueOf(timeUnit));

        Runnable checkWarns = () -> {
            for (IWarnEntry entry : WarnEntry.getAllEntries()) {
                for (Warn warn : entry.warns()) {
                    Instant now = Instant.now();
                    Instant future = now.plus(interval);

                    if (warn.created().before(Date.from(future))) {
                        entry.removeWarn(warn.id());
                        GlobalLogger.getLogger().debug("Removed warn ID " + warn.id() + " from " + entry.name() + " reason: Warn exceeded the interval");
                    }
                }
            }
        };

        scheduler.scheduleAtFixedRate(checkWarns, 0, 1, TimeUnit.SECONDS);

    }

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
