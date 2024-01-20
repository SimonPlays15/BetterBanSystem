package me.github.simonplays15.betterbansystem.core.ban;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.github.simonplays15.betterbansystem.core.BetterBanSystem;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * BanEntry represents a single ban entry in a ban system.
 * It implements the IBanEntry interface.
 * This class provides methods to save, remove, retrieve and manipulate ban entries.
 */
public record BanEntry(UUID uuid, String name, String source, Date created,
                       Object expires, String reason)
        implements IBanEntry {
    /**
     * Gson object for serializing and deserializing IBanEntry objects in JSON format.
     *
     * <p>
     * This {@code Gson} instance is created with the following configuration:
     * <ul>
     *   <li>PrettyPrinting: enabled</li>
     *   <li>Date Format: "yyyy-MM-dd HH:mm:ss Z"</li>
     *   <li>Type Adapter: IBanEntryAdapter</li>
     * </ul>
     * </p>
     *
     * @see Gson
     * @see GsonBuilder
     * @see TypeAdapter
     * @see IBanEntry
     * @see IBanEntryAdapter
     */
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss Z").registerTypeAdapter(IBanEntry.class, new IBanEntryAdapter()).create();
    /**
     * Represents a File object for the "banned-players.json" file.
     * This file is used to store information about banned players.
     * <p>
     * File path: banned-players.json
     */
    private static final File file = new File("banned-players.json");
    /**
     * A private static final SimpleDateFormat object used for formatting dates and times.
     * The format follows the pattern "yyyy-MM-dd HH:mm:ss Z" and uses the US locale.
     */
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
    /**
     * The variable BANNED_PLAYERS_TABLE represents the name of the table that stores information about banned players.
     * It is a private static final String.
     */
    private static final String BANNED_PLAYERS_TABLE = "bannedplayers";

    /**
     * Saves an IBanEntry object to a JSON file or a database table.
     * If a database is available, the entry is inserted into the "BANNED_PLAYERS_TABLE" table.
     * If a database is not available, the entry is added to a JSON file.
     *
     * @param entry The IBanEntry object to be saved.
     */
    public static void saveToJson(IBanEntry entry) {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            BetterBanSystem.getInstance().getDatabase().insert(BANNED_PLAYERS_TABLE, Map.of("uuid", entry.uuid().toString(), "name", entry.name(), "source", entry.source(), "created", format.format(entry.created()), "expires", (entry.expires() instanceof Date ? format.format(entry.expires()) : entry.expires()), "reason", entry.reason()));
            return;
        }
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    GlobalLogger.getLogger().info("File " + file.getName() + " created.");
                }
            } catch (IOException e) {
                GlobalLogger.getLogger().error(e);
            }
        }
        List<IBanEntry> entries;
        if (file.length() != 0) {
            try (Reader reader = new FileReader(file.getName())) {
                Type listType = new TypeToken<ArrayList<IBanEntry>>() {
                }.getType();
                entries = gson.fromJson(reader, listType);
            } catch (IOException e) {
                GlobalLogger.getLogger().error(e);
                return;
            }
        } else {
            entries = new ArrayList<>();
        }

        List<IBanEntry> tempEntries = new ArrayList<>(entries);
        tempEntries.add(entry);

        try (Writer writer = new FileWriter(file.getName())) {
            gson.toJson(tempEntries, writer);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
        }
    }

    /**
     * Removes an entry from the ban list.
     *
     * @param entry The ban entry to remove.
     */
    public static void removeEntry(@NotNull IBanEntry entry) {
        removeFromJson(entry.uuid());
    }

    /**
     * Removes a specific UUID from a JSON file.
     *
     * @param targetUUID The UUID to be removed from the JSON file.
     */
    public static void removeFromJson(UUID targetUUID) {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            BetterBanSystem.getInstance().getDatabase().delete(BANNED_PLAYERS_TABLE, "uuid", targetUUID.toString());
            return;
        }
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    GlobalLogger.getLogger().info("File " + file.getName() + " created.");
                }
            } catch (IOException e) {
                GlobalLogger.getLogger().error(e);
            }
        }
        List<IBanEntry> entries;
        List<IBanEntry> tempEntries;
        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IBanEntry>>() {
            }.getType();
            entries = gson.fromJson(reader, listType);
            tempEntries = new ArrayList<>(entries);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
            return;
        }
        tempEntries.removeIf(entry -> entry.uuid().equals(targetUUID));
        if (entries.size() != tempEntries.size()) {
            entries.clear();
            entries.addAll(tempEntries);
            try (Writer writer = new FileWriter(file.getName())) {
                gson.toJson(entries, writer);
            } catch (IOException e) {
                GlobalLogger.getLogger().error(e);
            }
        }
    }

    /**
     * Retrieves all ban entries from the database and/or file.
     *
     * @return A list of IBanEntry objects representing all ban entries.
     */
    public static @NotNull List<IBanEntry> getAllEntries() {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            List<Map<String, Object>> potentialEntries = BetterBanSystem.getInstance().getDatabase().selectAll(BANNED_PLAYERS_TABLE);
            List<IBanEntry> entries = new ArrayList<>();
            for (Map<String, Object> potentialEntry : potentialEntries) {
                UUID uuid = UUID.fromString((String) potentialEntry.get("uuid"));
                String name = (String) potentialEntry.get("name");
                String source = (String) potentialEntry.get("source");
                Date created = null;
                try {
                    created = format.parse((String) potentialEntry.get("created"));
                } catch (ParseException e) {
                    GlobalLogger.getLogger().error(e);
                }
                Object expires = potentialEntry.get("expires");
                String reason = (String) potentialEntry.get("reason");
                entries.add(new BanEntry(uuid, name, source, created, expires, reason));
            }
            return entries;
        }
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    GlobalLogger.getLogger().info("File " + file.getName() + " created.");
                }
            } catch (IOException e) {
                GlobalLogger.getLogger().error(e);
            }
        }
        List<IBanEntry> entries = new ArrayList<>();
        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IBanEntry>>() {
            }.getType();
            entries = gson.fromJson(reader, listType);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
        }
        if (entries == null)
            entries = new ArrayList<>();
        return entries;
    }

    /**
     * Finds an entry in the list of all entries by UUID.
     *
     * @param targetUUID the UUID of the entry to find
     * @return the found IBanEntry, or null if no entry is found
     */
    public static IBanEntry findEntry(UUID targetUUID) {
        List<IBanEntry> entries = getAllEntries();
        return entries.stream()
                .filter(entry -> entry.uuid().equals(targetUUID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the string representation of the BanEntry object.
     * The format of the string is:
     * "BanEntry{uuid=<uuid>, name='<name>', source='<source>', created=<created>, expires=<expires>, reason='<reason>'}"
     *
     * @return the string representation of the BanEntry object
     */
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "BanEntry{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", created=" + created +
                ", expires=" + expires +
                ", reason='" + reason + '\'' +
                '}';
    }

    /**
     * The IBanEntryAdapter class is a TypeAdapter for serializing and deserializing IBanEntry objects using Gson.
     */
    public static class IBanEntryAdapter extends TypeAdapter<IBanEntry> {
        /**
         * Writes the JSON representation of an IBanEntry object to a JsonWriter.
         *
         * @param writer The JsonWriter used to write the JSON data. Cannot be null.
         * @param entry  The IBanEntry object to be written. Cannot be null.
         * @throws IOException If an error occurs while writing to the JsonWriter.
         */
        @Override
        public void write(@NotNull JsonWriter writer, @NotNull IBanEntry entry) throws IOException {
            writer.beginObject();
            writer.name("uuid").value(entry.uuid().toString());
            writer.name("name").value(entry.name());
            writer.name("source").value(entry.source());
            writer.name("created").value(format.format(entry.created()));
            if (entry.expires() instanceof Date) {
                writer.name("expires").value(format.format(entry.expires()));
            } else {
                writer.name("expires").value(entry.expires().toString());
            }
            writer.name("reason").value(entry.reason());
            writer.endObject();
        }

        /**
         * Reads a JSON object from a JsonReader and creates a new IBanEntry instance based on the read data.
         *
         * @param reader The JsonReader to read from. Cannot be null.
         * @return The newly created IBanEntry instance.
         * @throws IOException If an error occurs while reading from the JsonReader.
         */
        @Override
        public IBanEntry read(@NotNull JsonReader reader) throws IOException {
            UUID uuid = null;
            String name = null;
            String source = null;
            Date created = null;
            Object expires = null;
            String reason = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String propName = reader.nextName();
                switch (propName) {
                    case "uuid":
                        uuid = UUID.fromString(reader.nextString());
                        break;
                    case "name":
                        name = reader.nextString();
                        break;
                    case "source":
                        source = reader.nextString();
                        break;
                    case "created":
                        String dateString = reader.nextString();
                        try {
                            created = format.parse(dateString);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "expires":
                        String expiredString = reader.nextString();
                        try {
                            expires = format.parse(expiredString);
                        } catch (ParseException e) {
                            expires = expiredString;
                        }
                        break;
                    case "reason":
                        reason = reader.nextString();
                        break;
                    default:
                        reader.skipValue();  // ignore unexpected property
                        break;
                }
            }
            reader.endObject();

            return new BanEntry(uuid, name, source, created, expires, reason);
        }
    }
}
