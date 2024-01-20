package me.github.simonplays15.betterbansystem.api.files;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Objects;

/**
 * ResourceFile represents a utility class for saving and accessing resources within a data folder.
 */
public class ResourceFile {
    /**
     * Represents the folder directory for data files.
     * <p>
     * The dataFolder variable is a private final File object that holds the directory path
     * for data files in the ResourceFile class.
     * <p>
     * This variable is used to locate and access data files within the class. It is initialized
     * when the ResourceFile object is created and is immutable throughout the life of the object.
     * <p>
     * The dataFolder variable is used in several methods of the ResourceFile class to manipulate
     * and retrieve data resources. It is also accessed by other classes that use the ResourceFile
     * class to interact with data files.
     *
     * @see ResourceFile
     * @see ResourceFile#getResource(String)
     * @see ResourceFile#saveResource(String, boolean)
     * @see ResourceFile#getResourceLastModified(String)
     * @see ResourceFile#copyResourceToFile(String, File)
     */
    private final File dataFolder;

    /**
     * Constructs a new instance of the ResourceFile class.
     *
     * @param dataFolder The data folder for the resource file.
     */
    public ResourceFile(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    /**
     * Saves a resource to the specified resource path.
     *
     * @param resourcePath The path of the resource to save.
     * @param replace      Indicates whether to replace the existing file if it already exists.
     * @throws NullPointerException     if the resource path is empty or null.
     * @throws IllegalArgumentException if the embedded resource cannot be found.
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
        if (outFile.exists() && replace) {
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
        if (!outFile.exists()) {
            try {
                copyResourceToFile(resourcePath, outFile);
            } catch (IOException ex) {
                GlobalLogger.getLogger().error("Could not save " + outFile.getName() + " to " + outFile, ex);
            }
        }
    }

    /**
     * Retrieves an InputStream for a given resource file.
     *
     * @param fileName The name of the resource file.
     * @return An InputStream for the resource file, or null if the resource file was not found.
     * @throws RuntimeException If an error occurs while finding or accessing the resource file.
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
            throw new RuntimeException("Failed to find resource " + fileName);
        }
    }

    /**
     * Retrieves the last modified time of a resource specified by the given resource path.
     *
     * @param resourcePath The path of the resource.
     * @return The last modified time of the resource in milliseconds, or 0 if the resource cannot be found or an error occurs.
     */
    private long getResourceLastModified(@NotNull String resourcePath) {
        try {
            return Files.getLastModifiedTime(new File(Objects.requireNonNull(getClass().getClassLoader().getResource(resourcePath)).toURI()).toPath()).toMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Copies a resource to a destination file.
     *
     * @param resourcePath    The path of the resource to be copied.
     * @param destinationFile The file to which the resource should be copied.
     * @throws IOException If an I/O error occurs during the copy process.
     */
    private void copyResourceToFile(@NotNull String resourcePath, @NotNull File destinationFile) throws IOException {
        try (FileWriter writer = new FileWriter(destinationFile)) {
            InputStream inputStream = getResource(resourcePath);
            int c;
            while ((c = inputStream.read()) != -1) {
                writer.write(c);
            }
        }
    }
}
