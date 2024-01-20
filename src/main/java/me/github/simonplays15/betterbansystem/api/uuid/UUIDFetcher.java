package me.github.simonplays15.betterbansystem.api.uuid;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class that retrieves UUIDs of players from the Mojang API or returns their offline UUID.
 */
public class UUIDFetcher {
    /**
     * The API URL used to fetch user profiles from Mojang API.
     */
    private static final String API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    /**
     * The uuidCache variable is a private static final ConcurrentMap<String, UUID> object that represents a cache for UUID values.
     * It is used to store previously resolved UUIDs for faster lookups.
     * <p>
     * The cache is implemented as a ConcurrentHashMap, a thread-safe map implementation that allows multiple threads to access
     * the map concurrently without the need for external synchronization.
     * <p>
     * The keys in the map are strings representing player names, and the values are UUID objects corresponding to the UUIDs of the players.
     * <p>
     * This cache is used in conjunction with the UUIDFetcher class methods to efficiently retrieve UUIDs for player names.
     * <p>
     * Class Name: UUIDFetcher
     * Fields: API_URL, uuidCache, BIN_FILE, USERCACHE
     * Methods: loadUsercacheJson(), saveUUIDToBin(String playername, UUID uuid),
     * loadUUIDFromBin(String playername), getUUID(String playername), getUUIDOrOfflineUUID(String playername)
     * Superclasses: Object
     */
    private static final ConcurrentMap<String, UUID> uuidCache = new ConcurrentHashMap<>();
    /**
     * Represents the binary file used for storing UUIDs.
     * <p>
     * The BIN_FILE variable is a private static final File object that represents the binary file used for storing UUIDs.
     * The file is initialized using the {@link BetterBanSystem#getDataFolder()} method from the {@link BetterBanSystem} instance,
     * along with the file name "uuids.bin".
     * </p>
     * <p>
     * This file is used by the {@link UUIDFetcher} class to store and retrieve UUIDs associated with player names.
     * </p>
     *
     * @see BetterBanSystem#getDataFolder()
     * @see UUIDFetcher
     */
    private static final File BIN_FILE = new File(BetterBanSystem.getInstance().getDataFolder(), "uuids.bin");
    /**
     * The variable USERCACHE represents the file used to store the user cache in JSON format.
     * It is a private static final variable of type File.
     * <p>
     * Usage example:
     * // Create a new instance with the given file path
     * File userCache = new File("usercache.json");
     * <p>
     * Note: The file path should be a valid path to a file.
     */
    private static final File USERCACHE = new File("usercache.json");

    /**
     * Loads the user cache from the JSON file.
     * If the JSON file doesn't exist, the method returns.
     * The method reads the JSON file using a buffered reader.
     * It then converts the JSON data to a JsonArray using a Gson library.
     * For each JsonElement in the JsonArray, the method retrieves the UUID and name values from the JsonObject.
     * If the UUID is not already stored in the UUID bin file, it is saved using the saveUUIDToBin method.
     */
    public static void loadUsercacheJson() {
        if (!USERCACHE.exists())
            return;
        Gson gson = new Gson();

        Reader reader;
        try {
            reader = Files.newBufferedReader(USERCACHE.toPath());
        } catch (IOException e) {
            GlobalLogger.getLogger().error("Failed to load Usercached UUIDs", e);
            return;
        }
        JsonArray array = gson.fromJson(reader, JsonArray.class);
        for (JsonElement map : array) {
            JsonObject object = map.getAsJsonObject();
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String name = object.get("name").getAsString();
            if (loadUUIDFromBin(name) == null)
                saveUUIDToBin(name, uuid);
        }


    }

    /**
     * Saves a UUID to a binary file.
     *
     * @param playername the name of the player
     * @param uuid       the UUID of the player
     */
    private static void saveUUIDToBin(String playername, UUID uuid) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BIN_FILE, false))) {
            if (!uuidCache.containsKey(playername))
                uuidCache.put(playername, uuid);
            oos.writeObject(uuidCache);
        } catch (Exception ex) {
            GlobalLogger.getLogger().error(ex.getMessage().trim().substring(0, 60) + "...");
        }
    }

    /**
     * Loads a UUID from a binary file using the playername as the key.
     *
     * @param playername The playername to retrieve the UUID for.
     * @return The UUID associated with the playername, or null if not found.
     */
    private static @Nullable UUID loadUUIDFromBin(String playername) {
        if (uuidCache.containsKey(playername))
            return uuidCache.get(playername);
        if (!BIN_FILE.exists()) {
            try {
                BIN_FILE.createNewFile();
            } catch (IOException ignored) {
            }
        }
        Map<String, UUID> map;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BIN_FILE))) {
            Object potentialMap = ois.readObject();
            if (potentialMap instanceof ConcurrentMap<?, ?>) {
                map = (ConcurrentMap<String, UUID>) potentialMap;
                if (map.containsKey(playername)) {
                    return map.get(playername);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Retrieves the UUID for a given player name.
     *
     * @param playername the name of the player
     * @return the UUID of the player
     * @throws RuntimeException if failed to call the Mojang API for UUID fetching
     */
    @Contract(pure = true)
    public static @NotNull UUID getUUID(String playername) {
        UUID bin = loadUUIDFromBin(playername);
        if (bin != null) {
            return bin;
        }

        UUID uuidFromCache = uuidCache.get(playername);
        if (uuidFromCache != null) {
            return uuidFromCache;
        }

        try {
            URL url = new URL(API_URL + playername);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Gson gson = new Gson();
            JsonElement element = gson.fromJson(reader, JsonElement.class);
            JsonObject json = element.getAsJsonObject();

            String uuidString = json.get("id").getAsString();
            reader.close();

            StringBuilder realUuid = new StringBuilder();
            for (int i = 0; i <= 31; i++) {
                realUuid.append(uuidString.charAt(i));
                if (i == 7 || i == 11 || i == 15 || i == 19) {
                    realUuid.append("-");
                }
            }
            UUID uuid = UUID.fromString(realUuid.toString());
            uuidCache.put(playername, uuid);
            saveUUIDToBin(playername, uuid);
            return uuid;
        } catch (Exception ex) {
            GlobalLogger.getLogger().error("Failed to call mojang API for uuid fetching. Are the servers unavailable?");
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns the UUID of a player based on their name. If the UUID is not found,
     * it generates an offline UUID based on the player's name.
     *
     * @param playername the name of the player
     * @return the UUID of the player or an offline generated UUID
     */
    @Contract(pure = true)
    public static UUID getUUIDOrOfflineUUID(String playername) {
        UUID bin = loadUUIDFromBin(playername);
        if (bin != null) {
            return bin;
        }

        UUID uuidFromCache = uuidCache.get(playername);
        if (uuidFromCache != null) {
            return uuidFromCache;
        }
        try {
            return getUUID(playername);
        } catch (Exception ex) {
            UUID g = UUID.nameUUIDFromBytes(playername.getBytes());
            uuidCache.put(playername, g);
            saveUUIDToBin(playername, g);
            return g;
        }
    }

}
