/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.ban;

import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BanManager {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public BanManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

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
        scheduler.scheduleAtFixedRate(checkExpiredBanEntry, 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(checkExpiredIPBanEntry, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        GlobalLogger.getLogger().debug("Shutting Down BanManager Scheduler Service");
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
