package me.github.simonplays15.betterbansystem.core;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * The Updater class is responsible for checking for updates of a resource.
 */
public class Updater {

    /**
     * The private final variable resourceId represents the unique identifier of a resource.
     */
    private final int resourceId;

    /**
     * The Updater class is responsible for checking for updates of a resource.
     */
    public Updater() {
        this.resourceId = 114538;
    }

    /**
     * Retrieves the version of a resource.
     * This method fetches the version by querying a specific URL and passes the result to the provided consumer.
     * If an exception occurs during the process, an error message is logged using a global logger.
     *
     * @param version the consumer that accepts the version string
     */
    public void getVersion(final Consumer<String> version) {
        CompletableFuture.runAsync(() -> {
            try (InputStream in = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId + "/~").openStream(); Scanner scann = new Scanner(in)) {
                if (scann.hasNext())
                    version.accept(scann.next());
            } catch (Exception ex) {
                GlobalLogger.getLogger().error("Unable to check for updates", ex);
            }
        });
    }

}
