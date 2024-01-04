/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.uuid;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UUIDFetcher {
    private static final String API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final ConcurrentMap<String, UUID> uuidCache = new ConcurrentHashMap<>();
    private static final File BIN_FILE = new File(BetterBanSystem.getInstance().getDataFolder(), "uuids.bin");
    private static final File USERCACHE = new File("usercache.json");

    public static void loadUsercacheJson() {
        if (!USERCACHE.exists())
            return;
        Gson gson = new Gson();

        try {
            Reader reader = Files.newBufferedReader(USERCACHE.toPath());
            Map<?, ?> map = gson.fromJson(reader, Map.class);
            UUID uuid = UUID.fromString(map.get("uuid").toString());
            String name = map.get("name").toString();
            if (loadUUIDFromBin(name) == null)
                saveUUIDToBin(name, uuid);
        } catch (Exception ignored) {
        }

    }

    private static void saveUUIDToBin(String playername, UUID uuid) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BIN_FILE, false))) {
            if (!uuidCache.containsKey(playername))
                uuidCache.put(playername, uuid);
            oos.writeObject(uuidCache);
        } catch (Exception ex) {
            GlobalLogger.getLogger().error(ex.getMessage().trim().substring(0, 60) + "...");
        }
    }

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
     * Retrieves the UUID of a player from the Mojang API or returns the offline UUID.
     *
     * @param playername the player name for which to retrieve the UUID
     * @return the UUID of the player if it is found, otherwise the offline UUID
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
     * Retrieves the UUID of a player from the Mojang API or returns the offline UUID.
     *
     * @param playername the player name for which to retrieve the UUID
     * @return the UUID of the player if it is found, otherwise the offline UUID
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
