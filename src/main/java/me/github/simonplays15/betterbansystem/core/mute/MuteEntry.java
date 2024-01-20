package me.github.simonplays15.betterbansystem.core.mute;

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
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a mute entry for a player.
 * Implements the IMuteEntry interface.
 */
public record MuteEntry(UUID uuid, String name, String source, Date created,
                        Object expires, String reason)
        implements IMuteEntry {
    /**
     * The variable gson is an instance of the Gson class from the Gson library.
     * It is used for JSON serialization and deserialization of IMuteEntry objects.
     * The Gson instance is configured with pretty printing, date format, and a custom type adapter for IMuteEntry.
     * It is a private static final variable.
     * <p>
     * To serialize an IMuteEntry object to JSON, use the toJson() method of the gson variable.
     * To deserialize a JSON string to an IMuteEntry object, use the fromJson() method of the gson variable.
     * The IMuteEntryAdapter class is responsible for the custom serialization and deserialization logic.
     */
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss Z").registerTypeAdapter(IMuteEntry.class, new IMuteEntryAdapter()).create();
    /**
     *
     */
    private static final File file = new File("muted-players.json");

    /**
     * The format variable is a private static final SimpleDateFormat object used to parse and format dates in the format "yyyy-MM-dd HH:mm:ss Z".
     * It is initialized with the given pattern and the US locale.
     * <p>
     * The format object is used in the IMuteEntryAdapter class to parse and format date values when reading and writing JSON.
     * It ensures consistent date representation across different systems and locales.
     * <p>
     * Example usage:
     * <p>
     * // Parsing a date string
     * String dateString = "2021-06-01 12:30:00 +0200";
     * Date date = format.parse(dateString);
     * <p>
     * // Formatting a date object
     * Date now = new Date();
     * String formattedDate = format.format(now);
     *
     * @see IMuteEntryAdapter
     */
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
    /**
     * The MUTE_TABLENAME variable represents the name of the table used for storing muted player entries.
     * It is a constant variable defined in the MuteEntry class.
     */
    private static final String MUTE_TABLENAME = "mutedplayers";

    /**
     * Creates a mute entry if the file does not exist.
     *
     * @param file The file to be checked and created if necessary.
     * @throws IOException If an I/O error occurs.
     */
    public MuteEntry {
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    GlobalLogger.getLogger().info("File " + file.getName() + " created.");
                }
            } catch (IOException e) {
                GlobalLogger.getLogger().error(e);
            }
        }
    }

    /**
     * Saves an IMuteEntry object to JSON.
     *
     * @param entry The IMuteEntry object to save.
     */
    public static void saveToJson(IMuteEntry entry) {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            BetterBanSystem.getInstance().getDatabase().insert(MUTE_TABLENAME, Map.of("uuid", entry.uuid().toString(), "name", entry.name(), "source", entry.source(), "created", format.format(entry.created()), "expires", (entry.expires() instanceof Date ? format.format(entry.expires()) : entry.expires()), "reason", entry.reason()));
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
        List<IMuteEntry> entries;
        if (file.length() != 0) {
            try (Reader reader = new FileReader(file.getName())) {
                Type listType = new TypeToken<ArrayList<IMuteEntry>>() {
                }.getType();
                entries = gson.fromJson(reader, listType);
            } catch (IOException e) {
                GlobalLogger.getLogger().error(e);
                return;
            }
        } else {
            entries = new ArrayList<>();
        }

        List<IMuteEntry> tempEntries = new ArrayList<>(entries);
        tempEntries.add(entry);

        try (Writer writer = new FileWriter(file.getName())) {
            gson.toJson(tempEntries, writer);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
        }
    }

    /**
     * Removes an entry from the system.
     *
     * @param entry The entry to be removed.
     * @throws NullPointerException if the entry is null.
     */
    public static void removeEntry(@NotNull IMuteEntry entry) {
        removeFromJson(entry.uuid());
    }

    /**
     * Removes a mute entry with the given UUID from a JSON file or database.
     *
     * @param targetUUID The UUID of the mute entry to be removed.
     */
    public static void removeFromJson(UUID targetUUID) {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            BetterBanSystem.getInstance().getDatabase().delete(MUTE_TABLENAME, "uuid", targetUUID.toString());

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
        List<IMuteEntry> entries;
        List<IMuteEntry> tempEntries;
        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IMuteEntry>>() {
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
     * Retrieves a list of all mute entries.
     *
     * @return A list of all mute entries, represented as {@link IMuteEntry}.
     * If no entries are found, an empty list is returned.
     * @throws IOException if there is an error reading the mute entries from the file
     */
    public static @NotNull List<IMuteEntry> getAllEntries() {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            List<Map<String, Object>> potentialEntries = BetterBanSystem.getInstance().getDatabase().selectAll(MUTE_TABLENAME);
            List<IMuteEntry> entries = new ArrayList<>();
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
                entries.add(new MuteEntry(uuid, name, source, created, expires, reason));
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
        List<IMuteEntry> entries = new ArrayList<>();
        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IMuteEntry>>() {
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
     * Finds an entry in the list of mute entries with the specified target UUID.
     *
     * @param targetUUID The UUID of the target entry.
     * @return The first mute entry with the specified UUID, or null if no such entry is found.
     */
    public static IMuteEntry findEntry(UUID targetUUID) {
        List<IMuteEntry> entries = getAllEntries();
        return entries.stream()
                .filter(entry -> entry.uuid().equals(targetUUID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a string representation of the mute entry.
     *
     * @return A string representation of the mute entry.
     */
    @Override
    public String toString() {
        return "MuteEntry{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", created=" + created +
                ", expires=" + expires +
                ", reason='" + reason + '\'' +
                '}';
    }

    /**
     * The IMuteEntryAdapter class is a Gson TypeAdapter for serializing and deserializing IMuteEntry objects.
     */
    public static class IMuteEntryAdapter extends TypeAdapter<IMuteEntry> {
        /**
         * Writes the given {@link IMuteEntry} object to the provided {@link JsonWriter}.
         *
         * @param writer the JSON writer to write the mute entry object to
         * @param entry  the mute entry object to be written
         * @throws IOException if an I/O error occurs during the write operation
         */
        @Override
        public void write(@NotNull JsonWriter writer, @NotNull IMuteEntry entry) throws IOException {
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
         * Reads the JSON representation of an IMuteEntry from a JsonReader and
         * constructs a new IMuteEntry object.
         *
         * @param reader the JsonReader from which to read the JSON representation
         * @return a new IMuteEntry object constructed from the JSON representation
         * @throws IOException if an I/O error occurs while reading the JSON representation
         */
        @Override
        public IMuteEntry read(@NotNull JsonReader reader) throws IOException {
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

            return new MuteEntry(uuid, name, source, created, expires, reason);
        }
    }
}
