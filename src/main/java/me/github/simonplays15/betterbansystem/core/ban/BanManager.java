package me.github.simonplays15.betterbansystem.core.ban;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * BanManager class is responsible for managing bans and checking for expired ban entries.
 */
public class BanManager {

    /**
     * The scheduled executor service used for scheduling tasks to be executed at fixed intervals or delays.
     *
     * @see BanManager
     * @since 1.0
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * BanManager is responsible for managing bans and checking for expired ban entries.
     * It has a start method that schedules tasks to check for expired ban entries periodically,
     * and a stop method to shut down the scheduler.
     */
    public BanManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    /**
     * Starts the BanManager and schedules tasks to check for expired ban entries.
     */
    public void start() {
        Runnable checkExpiredBanEntry = () -> {
            for (IBanEntry entry : BanEntry.getAllEntries()) {
                if (entry.expires() instanceof Date && ((Date) entry.expires()).before(new Date())) {
                    BanEntry.removeEntry(entry);
                    GlobalLogger.getLogger().debug("TimeBan from", entry.name(), "expired.", entry.toString());
                }
            }
        };

        Runnable checkExpiredIPBanEntry = () -> {
            for (IIPBanEntry entry : IPBanEntry.getAllEntries()) {
                if (entry.expires() instanceof Date && ((Date) entry.expires()).before(new Date())) {
                    IPBanEntry.removeEntry(entry);
                    GlobalLogger.getLogger().debug("TimeBan from", entry.ip(), "expired.", entry.toString());
                }
            }
        };
        scheduler.scheduleAtFixedRate(checkExpiredBanEntry, 6, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(checkExpiredIPBanEntry, 6, 1, TimeUnit.SECONDS);
    }

    /**
     * Shuts down the scheduler and ensures all scheduled tasks are completed or canceled within 5 seconds.
     * If the tasks do not complete within 5 seconds, the scheduler is forcibly shutdown.
     * <p>
     * If an InterruptedException occurs during the termination process, the scheduler is forcibly shutdown and an error message is written to the logger.
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
