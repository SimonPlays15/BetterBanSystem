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

/**
 * WarnManager class for managing warnings and auto-deletion of expired warnings.
 */
public class WarnManager {

    /**
     *
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * WarnManager class for managing warnings and auto-deletion of expired warnings.
     */
    public WarnManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    /**
     * Starts the auto-deletion of expired warnings based on the configured interval.
     * <p>
     * Note: The auto-deletion feature must be enabled in the configuration file.
     * The interval for auto-deletion can be configured in the configuration file
     * using the properties 'warns.autodelete.use' and 'warns.autodelete.time'.
     * <p>
     * The time unit for the interval can be configured using the property 'warns.autodelete.unit'.
     * By default, the time unit is set to 'MINUTES'. The available time units are:
     * - NANOSECONDS
     * - MICROSECONDS
     * - MILLISECONDS
     * - SECONDS
     * - MINUTES
     * - HOURS
     * - DAYS
     */
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

    /**
     * Stops the execution of the scheduler and shuts it down gracefully.
     * If the scheduler does not terminate within 5 seconds, it will be forced to shut down.
     * If an InterruptedException occurs during the termination process, the scheduler will be forced to shut down as well.
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
