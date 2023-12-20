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

public record IPBanEntry(String bannedIP, String bannerName, Date creationDate, Date expirationDate, String reason,
                         boolean isPermanent) implements IIPBanEntry {

    public static void saveToJson(IIPBanEntry ipBanEntry, String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(ipBanEntryToJson(ipBanEntry), writer);
        } catch (IOException e) {
            BetterBanSystem.getGlobalLogger().error("Failed to load save Object", e);
        }
    }

    public static IPBanEntry loadFromJson(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonToIPBanEntry(jsonObject);
        } catch (IOException e) {
            BetterBanSystem.getGlobalLogger().error("Failed to load IPBan Object", e);
            return null;
        }
    }

    private static JsonObject ipBanEntryToJson(IIPBanEntry ipBanEntry) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ip", ipBanEntry.bannedIP());
        jsonObject.addProperty("source", ipBanEntry.bannerName());
        jsonObject.addProperty("created", formatDate(ipBanEntry.creationDate()));
        jsonObject.addProperty("expires", formatDate(ipBanEntry.expirationDate()));
        jsonObject.addProperty("reason", ipBanEntry.reason());
        jsonObject.addProperty("isPermanent", ipBanEntry.isPermanent());
        return jsonObject;
    }

    private static IPBanEntry jsonToIPBanEntry(JsonObject jsonObject) {
        String bannedIP = jsonObject.get("ip").getAsString();
        String bannerName = jsonObject.get("source").getAsString();
        Date creationDate = parseDate(jsonObject.get("created").getAsString());
        Date expirationDate = parseDate(jsonObject.get("expires").getAsString());
        String reason = jsonObject.get("reason").getAsString();
        boolean isPermanent = jsonObject.get("isPermanent").getAsBoolean();
        return new IPBanEntry(bannedIP, bannerName, creationDate, expirationDate, reason, isPermanent);
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
            BetterBanSystem.getGlobalLogger().error("Failed to parse date:", dateString, e);
            return null;
        }
    }
}
