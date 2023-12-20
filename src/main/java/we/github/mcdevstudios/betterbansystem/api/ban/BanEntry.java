/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.ban;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import we.github.mcdevstudios.betterbansystem.spigot.BetterBanSystem;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public record BanEntry(UUID bannedPlayerUUID, String bannedPlayerName, String banningPlayerName, Date creationDate,
                       Date expirationDate, String reason, boolean isPermanent) implements IBanEntry {

    public static void saveToJson(IBanEntry banEntry, String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(banEntryToJson(banEntry), writer);
        } catch (IOException e) {
            BetterBanSystem.getGlobalLogger().error("Failed to save ban-entry", e);
        }
    }

    public static BanEntry loadFromJson(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonToBanEntry(jsonObject);
        } catch (IOException e) {
            BetterBanSystem.getGlobalLogger().error("Failed to load ban-entry", e);
            return null;
        }
    }

    private static JsonObject banEntryToJson(IBanEntry banEntry) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", banEntry.bannedPlayerUUID().toString());
        jsonObject.addProperty("name", banEntry.bannedPlayerName());
        jsonObject.addProperty("source", banEntry.banningPlayerName());
        jsonObject.addProperty("created", formatDate(banEntry.creationDate()));
        jsonObject.addProperty("expires", formatDate(banEntry.expirationDate()));
        jsonObject.addProperty("reason", banEntry.reason());
        jsonObject.addProperty("isPermanent", banEntry.isPermanent());
        return jsonObject;
    }

    private static BanEntry jsonToBanEntry(JsonObject jsonObject) {
        String bannedPlayerUUID = jsonObject.get("uuid").getAsString();
        String bannedPlayerName = jsonObject.get("name").getAsString();
        String banningPlayerName = jsonObject.get("source").getAsString();
        Date creationDate = parseDate(jsonObject.get("created").getAsString());
        Date expirationDate = parseDate(jsonObject.get("expires").getAsString());
        String reason = jsonObject.get("reason").getAsString();
        boolean isPermanent = jsonObject.get("isPermanent").getAsBoolean();
        return new BanEntry(UUID.fromString(bannedPlayerUUID), bannedPlayerName, banningPlayerName, creationDate, expirationDate, reason, isPermanent);
    }

    private static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    private static Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            BetterBanSystem.getGlobalLogger().error("Failed to Parse date:", dateString, e);
            return null;
        }
    }
}
