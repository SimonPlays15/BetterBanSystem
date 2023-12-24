/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.files;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class ResourceFile {
    private final File dataFolder;

    /**
     * @param dataFolder {@link File}
     */
    public ResourceFile(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    /**
     * Saves the File given from the resourcepath
     * of this JAR File.
     *
     * @param resourcePath {@link String}
     * @param replace      {@link Boolean}
     * @throws NullPointerException     if {@code resourcePath} is null or empty
     * @throws IllegalArgumentException if the given {@link InputStream} of {@link ResourceFile#getResource(String)} is null
     * @apiNote if {@code replace} is set to {@code true} its only replacing the File when the file inside the jar file is newer.
     */
    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath.isEmpty()) {
            throw new NullPointerException("ResourcePath cannot be empty or null");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream stream = this.getResource(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }
        File outFile = new File(this.dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf(47);
        File outDIr = new File(this.dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDIr.exists()) {
            boolean created = outDIr.mkdirs();
            GlobalLogger.getLogger().debug(outDIr, "created:", created);
        }
        if (outFile.exists() && !replace) {
            long newResource = getResourceLastModified(resourcePath);
            long existing = outFile.lastModified();

            if (newResource > existing) {
                try {
                    copyResourceToFile(resourcePath, outFile);
                } catch (IOException e) {
                    GlobalLogger.getLogger().error("Could not save " + outFile.getName() + " to " + outFile, e);
                }
            }
            return;
        }
        try {
            copyResourceToFile(resourcePath, outFile);
        } catch (IOException ex) {
            GlobalLogger.getLogger().error("Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    /**
     * Tries to find the resource from the given fileName inside the jar File
     *
     * @param fileName {@link String}
     * @return {@link InputStream} | null
     */
    public InputStream getResource(@NotNull String fileName) {
        try {
            URL url = this.getClass().getClassLoader().getResource(fileName);
            if (url == null)
                return null;

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * @param resourcePath String
     * @return {@link Long}
     */
    private long getResourceLastModified(@NotNull String resourcePath) {
        try {
            return Files.getLastModifiedTime(new File(Objects.requireNonNull(getClass().getClassLoader().getResource(resourcePath)).toURI()).toPath()).toMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param resourcePath    String
     * @param destinationFile File
     * @throws IOException if {@link Files#copy(Path, OutputStream)} failed
     */
    private void copyResourceToFile(@NotNull String resourcePath, @NotNull File destinationFile) throws IOException {
        try (InputStream inputStream = getResource(resourcePath)) {
            Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
