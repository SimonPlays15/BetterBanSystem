/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.uuid;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import we.github.mcdevstudios.betterbansystem.api.logging.GlobalLogger;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UUIDFetcher {
    private static final String API_URL = "https://api.mojang.com/users/profiles/minecraft/";

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

            return UUID.fromString(uuidString);
        } catch (Exception ex) {
            GlobalLogger.getLogger().error("Failed call mojang API for uuid fetching. Are the servers unavailable?", ex);
        }
        return null;
    }

}
