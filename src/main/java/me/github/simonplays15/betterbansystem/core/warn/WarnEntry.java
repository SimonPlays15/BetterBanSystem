package me.github.simonplays15.betterbansystem.core.warn;

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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a warning entry for a player.
 */
public record WarnEntry(UUID uuid, String name, List<Warn> warns) implements IWarnEntry {
    /**
     * The idGenerator variable is an AtomicInteger that is used for generating unique IDs for warnings.
     * It is declared as public, static, and final, which means that it is a constant variable and can be accessed without creating an instance of its containing class.
     * <p>
     * idGenerator is an instance of the AtomicInteger class, which provides atomic operations for integers. This allows multiple threads to safely increment and retrieve the current
     * value of the generator without conflicts or data races.
     * <p>
     * The initial value of the generator is 1.
     * <p>
     * Example usage:
     * <p>
     * int id = idGenerator.getAndIncrement();
     * <p>
     * This code snippet gets the current value of idGenerator and then increments it atomically. The returned value is stored in the "id" variable.
     */
    public static final AtomicInteger idGenerator = new AtomicInteger(1);
    /**
     * The variable gson is an instance of the Gson class. Gson is a library used for JSON serialization and deserialization in Java.
     * It is used to convert Java objects to JSON representations and vice versa.
     */
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss Z").registerTypeAdapter(IWarnEntry.class, new IWarnEntryAdapter()).create();
    /**
     * Represents the file for storing warning entries for players.
     * The file is used to store player warnings in JSON format.
     * <p>
     * This file is used by the WarnEntry class to load and save warning entries.
     */
    private static final File file = new File("player-warns.json");
    /**
     * Represents a SimpleDateFormat object with the following pattern: "yyyy-MM-dd HH:mm:ss Z".
     * Uses the US locale for formatting and parsing dates.
     * This format is used for parsing and formatting dates in the warning system.
     */
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
    /**
     * The variable WARNED_PLAYERS_TABLENAME represents the name of the table
     * in the database that stores information about warned players.
     */
    private static final String WARNED_PLAYERS_TABLENAME = "warnedplayers";
    /**
     * Represents the table name for storing player warns.
     */
    private static final String PLAYER_WARNS_TABLENAME = "warns";

    /**
     * Checks if the specified file exists, and if it doesn't, creates a new file.
     * If the file is created successfully, a log message will be logged indicating the creation
     * of the file. Any IOException that occurs during the file creation process will be logged as an error.
     */
    public WarnEntry {
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
     * Converts a map representation of a warning into a Warn object.
     *
     * @param map the map representation of a warning
     * @return the converted Warn object
     * @throws RuntimeException if the map contains invalid data or an error occurs during parsing
     */
    @Contract("_ -> new")
    private static @NotNull Warn convertMapToWarn(Map<String, Object> map) {
        Map<String, Object> warnMap = new HashMap<>();
        map.forEach((key, value) -> warnMap.put(key, value.toString()));
        try {
            return new Warn(
                    Integer.parseInt((String) warnMap.get("id")),
                    (String) warnMap.get("source"),
                    format.parse((String) warnMap.get("created")),
                    (String) warnMap.get("reason")
            );
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves a WarnEntry to a JSON file.
     *
     * @param entry The WarnEntry to be saved.
     */
    public static void saveToJson(IWarnEntry entry) {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            List<Map<String, Object>> existingEntry = BetterBanSystem.getInstance().getDatabase().select(WARNED_PLAYERS_TABLENAME, "uuid = '" + entry.uuid().toString() + "'");
            if (existingEntry == null || existingEntry.isEmpty()) {
                BetterBanSystem.getInstance().getDatabase().insert(WARNED_PLAYERS_TABLENAME, Map.of("uuid", entry.uuid().toString(), "name", entry.name()));
                for (Warn warn : entry.warns()) {
                    BetterBanSystem.getInstance().getDatabase().insert(PLAYER_WARNS_TABLENAME, Map.of("source", warn.source(), "created", format.format(warn.created()), "reason", warn.reason(), "uuid", entry.uuid()));
                }
                return;
            }
            List<Map<String, Object>> existingWarnsMaps = BetterBanSystem.getInstance().getDatabase().select(PLAYER_WARNS_TABLENAME, "uuid = '" + entry.uuid().toString() + "'");
            List<Warn> existingWarns = existingWarnsMaps.stream().map(WarnEntry::convertMapToWarn).toList();
            for (Warn warn : entry.warns()) {
                if (!existingWarns.contains(warn)) {
                    BetterBanSystem.getInstance().getDatabase().insert(PLAYER_WARNS_TABLENAME, Map.of("source", warn.source(), "created", format.format(warn.created()), "reason", warn.reason(), "uuid", entry.uuid()));
                }
            }
            for (Warn existing : existingWarns) {
                if (!entry.warns().contains(existing)) {
                    BetterBanSystem.getInstance().getDatabase().delete(PLAYER_WARNS_TABLENAME, "created", format.format(existing.created()));
                }
            }
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
        List<IWarnEntry> entries;
        if (file.length() != 0) {
            try (Reader reader = new FileReader(file.getName())) {
                Type listType = new TypeToken<ArrayList<IWarnEntry>>() {
                }.getType();
                entries = gson.fromJson(reader, listType);
            } catch (IOException ex) {
                GlobalLogger.getLogger().error(ex);
                return;
            }
        } else {
            entries = new ArrayList<>();
        }
        Optional<IWarnEntry> existing = entries.stream().filter(e -> e.uuid().equals(entry.uuid())).findFirst();
        if (existing.isPresent()) {
            existing.get().warns().clear();
            existing.get().warns().addAll(entry.warns());
        } else {
            entries.add(entry);
        }
        try (Writer writer = new FileWriter(file.getName())) {
            gson.toJson(entries, writer);
        } catch (IOException ex) {
            GlobalLogger.getLogger().error(ex);
        }
    }

    /**
     * Removes the specified warning entry from the system.
     *
     * @param entry the warning entry to be removed
     */
    public static void removeEntry(@NotNull IWarnEntry entry) {
        removeFromJson(entry.uuid());
    }

    /**
     * Removes the specified UUID from the JSON file.
     *
     * @param target The UUID to remove from the JSON file.
     */
    public static void removeFromJson(UUID target) {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            BetterBanSystem.getInstance().getDatabase().delete(WARNED_PLAYERS_TABLENAME, "uuid", target.toString());
            BetterBanSystem.getInstance().getDatabase().delete(PLAYER_WARNS_TABLENAME, "uuid", target.toString());
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
        List<IWarnEntry> entries;
        List<IWarnEntry> tempEntries;
        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IWarnEntry>>() {
            }.getType();
            entries = gson.fromJson(reader, listType);
            tempEntries = new ArrayList<>(entries);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
            return;
        }
        tempEntries.removeIf(entry -> entry.uuid().equals(target));
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
     * Retrieves all the warning entries.
     *
     * @return a list of IWarnEntry objects representing the warning entries
     */
    public static @NotNull List<IWarnEntry> getAllEntries() {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            List<Map<String, Object>> potentialEntries = BetterBanSystem.getInstance().getDatabase().selectAll(WARNED_PLAYERS_TABLENAME);
            List<IWarnEntry> entries = new ArrayList<>();
            potentialEntries.forEach(entry -> {
                UUID uuid = UUID.fromString((String) entry.get("uuid"));
                String name = (String) entry.get("name");
                List<Map<String, Object>> warnEntries = BetterBanSystem.getInstance().getDatabase().select(PLAYER_WARNS_TABLENAME, "uuid = '" + uuid + "'");
                List<Warn> warns = new ArrayList<>();
                warnEntries.forEach(warnEntry -> {
                    int id = (int) warnEntry.get("id");
                    String source = (String) warnEntry.get("source");
                    Date created = null;
                    try {
                        created = format.parse((String) warnEntry.get("created"));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    String reason = (String) warnEntry.get("reason");
                    warns.add(new Warn(id, source, created, reason));
                });
                entries.add(new WarnEntry(uuid, name, warns));
            });
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
        List<IWarnEntry> entries = new ArrayList<>();
        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IWarnEntry>>() {
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
     * Finds the warning entry with the specified UUID.
     *
     * @param targetUUID the UUID of the warning entry to find
     * @return the warning entry with the specified UUID, or null if not found
     */
    public static IWarnEntry findEntry(UUID targetUUID) {
        List<IWarnEntry> entries = getAllEntries();
        return entries.stream()
                .filter(entry -> entry.uuid().equals(targetUUID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a warning to the warn entry.
     *
     * @param warn the warning to be added
     */
    public void addWarn(Warn warn) {
        this.warns.add(warn);
    }

    /**
     * Removes a warning with the specified ID from the warn entry.
     *
     * @param id the ID of the warning to remove
     * @return true if the warning is successfully removed, false otherwise
     */
    public boolean removeWarn(int id) {
        return this.warns.removeIf(warn -> warn.id() == id);
    }

    /**
     * The IWarnEntryAdapter class is a TypeAdapter for serializing and deserializing IWarnEntry objects to and from JSON.
     */
    public static class IWarnEntryAdapter extends TypeAdapter<IWarnEntry> {
        /**
         * Writes an IWarnEntry object to JSON using a JsonWriter.
         *
         * @param writer The JsonWriter object to write to.
         * @param entry  The IWarnEntry object to write.
         * @throws IOException if an I/O error occurs while writing to the JsonWriter.
         */
        @Override
        public void write(@NotNull JsonWriter writer, @NotNull IWarnEntry entry) throws IOException {
            writer.beginObject();
            writer.name("uuid").value(entry.uuid().toString());
            writer.name("name").value(entry.name());
            writer.name("warns").beginArray();
            for (Warn warn : entry.warns()) {
                writer.beginObject();
                writer.name("id").value(warn.id());
                writer.name("source").value(warn.source());
                writer.name("created").value(format.format(warn.created()));
                writer.name("reason").value(warn.reason());
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();
        }

        /**
         * Reads a JSON representation of a warning entry and converts it into an IWarnEntry object.
         *
         * @param reader The JSON reader to read the input from.
         * @return An IWarnEntry object representing the deserialized warning entry.
         * @throws IOException If an I/O error occurs while reading the JSON data.
         */
        @Override
        public IWarnEntry read(@NotNull JsonReader reader) throws IOException {
            UUID uuid = null;
            String name = null;
            List<Warn> warns = new ArrayList<>();

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
                    case "warns":
                        reader.beginArray();
                        while (reader.hasNext()) {
                            int id = -1;
                            String source = null;
                            Date created = null;
                            String reason = null;

                            reader.beginObject();
                            while (reader.hasNext()) {
                                String subPropName = reader.nextName();
                                switch (subPropName) {
                                    case "id":
                                        id = reader.nextInt();
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
                                    case "reason":
                                        reason = reader.nextString();
                                        break;
                                    default:
                                        reader.skipValue();  // ignore unexpected property
                                        break;
                                }
                            }
                            reader.endObject();

                            warns.add(new Warn(id, source, created, reason));
                        }
                        reader.endArray();
                        break;
                    default:
                        reader.skipValue();  // ignore unexpected property
                        break;
                }
            }
            reader.endObject();

            return new WarnEntry(uuid, name, warns);
        }
    }

}
