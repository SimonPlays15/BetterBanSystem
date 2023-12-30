/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.uuid;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UUIDFetcher {
    private static final String API_URL = "https://api.mojang.com/users/profiles/minecraft/";

    /**
     * Retrieves the UUID of a player from the Mojang API.
     *
     * @param playername the player name for which to retrieve the UUID
     * @return the UUID of the player, or null if the API call fails
     */
    @Contract(pure = true)
    public static @Nullable UUID getUUID(String playername) {
        try {
            URL url = new URL(API_URL + playername);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            String uuidString = json.get("id").getAsString();
            reader.close();

            StringBuilder realUuid = new StringBuilder();
            for (int i = 0; i <= 31; i++) {
                realUuid.append(uuidString.charAt(i));
                if (i == 7 || i == 11 || i == 15 || i == 19) {
                    realUuid.append("-");
                }
            }

            return UUID.fromString(realUuid.toString());
        } catch (Exception ex) {
            GlobalLogger.getLogger().error("Failed to call mojang API for uuid fetching. Are the servers unavailable?", ex);
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
    public static @NotNull UUID getUUIDOrOfflineUUID(String playername) {
        try {
            URL url = new URL(API_URL + playername);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            String uuidString = json.get("id").getAsString();
            reader.close();

            StringBuilder realUuid = new StringBuilder();
            for (int i = 0; i <= 31; i++) {
                realUuid.append(uuidString.charAt(i));
                if (i == 7 || i == 11 || i == 15 || i == 19) {
                    realUuid.append("-");
                }
            }

            return UUID.fromString(realUuid.toString());
        } catch (Exception ex) {
            UUID g = UUID.fromString(playername);
            StringBuilder realUuid = new StringBuilder();
            for (int i = 0; i <= 31; i++) {
                realUuid.append(g.toString().charAt(i));
                if (i == 7 || i == 11 || i == 15 || i == 19) {
                    realUuid.append("-");
                }
            }
            return UUID.fromString(realUuid.toString());
        }
    }

}
