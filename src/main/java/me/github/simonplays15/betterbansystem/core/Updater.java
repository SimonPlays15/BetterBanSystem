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

public class Updater {

    private final int resourceId;

    public Updater() {
        this.resourceId = 114538;
    }

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
