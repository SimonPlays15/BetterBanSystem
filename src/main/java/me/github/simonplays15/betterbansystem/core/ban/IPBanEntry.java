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
 * Represents an entry for an IP ban.
 */
public record IPBanEntry(String ip, String source, Date created,
                         Object expires, String reason)
        implements IIPBanEntry {
    /**
     * The variable gson represents an instance of Gson, a library for converting Java objects to JSON representation and vice versa.
     */
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss Z").registerTypeAdapter(IIPBanEntry.class, new IIPBanEntryAdapter()).create();
    /**
     * Represents the file "banned-ips.json".
     */
    private static final File file = new File("banned-ips.json");
    /**
     * The constant BANNED_IPS_TABLENAME represents the name of the table where banned IP addresses are stored.
     * It is used to identify the table in the database.
     */
    private static final String BANNED_IPS_TABLENAME = "bannedips";
    /**
     * The IPBanEntry class represents an entry in an IP ban list.
     * It implements the IIPBanEntry interface.
     */
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);

    /**
     * Creates a new IPBanEntry object and checks if the associated file exists.
     * If the file does not exist, it will attempt to create a new file and output a log message if successful.
     *
     * @param file The associated file for this IPBanEntry.
     */
    public IPBanEntry {
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
     * Saves the given IP ban entry to a JSON file or a database.
     *
     * @param entry The IP ban entry to save.
     */
    public static void saveToJson(IIPBanEntry entry) {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            BetterBanSystem.getInstance().getDatabase().insert(BANNED_IPS_TABLENAME, Map.of("ip", entry.ip(), "source", entry.source(), "created", format.format(entry.created()), "expires", (entry.expires() instanceof Date ? format.format(entry.expires()) : entry.expires()), "reason", entry.reason()));
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
        List<IIPBanEntry> entries = new ArrayList<>();

        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IIPBanEntry>>() {
            }.getType();
            entries = gson.fromJson(reader, listType);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
        }
        if (entries == null)
            entries = new ArrayList<>();
        entries.add(entry);
        try (Writer writer = new FileWriter(file.getName())) {
            gson.toJson(entries, writer);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
        }
    }

    /**
     * Removes an entry from the IP ban list.
     *
     * @param entry The IP ban entry to be removed.
     */
    public static void removeEntry(@NotNull IIPBanEntry entry) {
        removeFromJson(entry.ip());
    }

    /**
     * Removes an entry from a JSON file based on the provided IP address.
     *
     * @param ipAddress The IP address of the entry to be removed.
     */
    public static void removeFromJson(String ipAddress) {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            BetterBanSystem.getInstance().getDatabase().delete(BANNED_IPS_TABLENAME, "ip", ipAddress);
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
        List<IIPBanEntry> entries = new ArrayList<>();
        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IIPBanEntry>>() {
            }.getType();
            entries = gson.fromJson(reader, listType);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
        }

        entries.removeIf(entry -> entry.ip().equals(ipAddress));
        try (Writer writer = new FileWriter(file.getName())) {
            gson.toJson(entries, writer);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
        }
    }

    /**
     * Retrieves all IP ban entries.
     *
     * @return A list of IPBanEntry objects representing the ban entries.
     */
    public static @NotNull List<IIPBanEntry> getAllEntries() {
        if (BetterBanSystem.getInstance().getDatabase() != null) {
            List<Map<String, Object>> potentialEntries = BetterBanSystem.getInstance().getDatabase().selectAll(BANNED_IPS_TABLENAME);
            List<IIPBanEntry> entries = new ArrayList<>();
            for (Map<String, Object> potentialEntry : potentialEntries) {
                String ip = (String) potentialEntry.get("ip");
                String source = (String) potentialEntry.get("source");
                Date created = null;
                try {
                    created = format.parse((String) potentialEntry.get("created"));
                } catch (ParseException e) {
                    GlobalLogger.getLogger().error(e);
                }
                Object expires = potentialEntry.get("expires");
                String reason = (String) potentialEntry.get("reason");
                entries.add(new IPBanEntry(ip, source, created, expires, reason));
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
        List<IIPBanEntry> entries = new ArrayList<>();
        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IIPBanEntry>>() {
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
     * Finds an IP ban entry given the IP address.
     *
     * @param ipAddress The IP address to search for.
     * @return The IP ban entry matching the given IP address, or null if no match is found.
     */
    public static IIPBanEntry findEntry(String ipAddress) {
        List<IIPBanEntry> entries = getAllEntries();
        return entries.stream()
                .filter(entry -> entry.ip().equals(ipAddress))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a string representation of the IPBanEntry object.
     *
     * @return A string representation of the IPBanEntry object.
     */
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "IPBanEntry{" +
                "ip='" + ip + '\'' +
                ", source='" + source + '\'' +
                ", created=" + created +
                ", expires=" + expires +
                ", reason='" + reason + '\'' +
                '}';
    }

    /**
     * The IIPBanEntryAdapter class is a Gson TypeAdapter for serializing and deserializing IIPBanEntry objects.
     *
     * @since 1.0.0
     */
    public static class IIPBanEntryAdapter extends TypeAdapter<IIPBanEntry> {
        /**
         * Writes an IIPBanEntry object to a JsonWriter in a specific format.
         *
         * @param writer The JsonWriter object to write the IIPBanEntry object to.
         * @param entry  The IIPBanEntry object to be written.
         * @throws IOException If there is an error during the writing process.
         */
        @Override
        public void write(@NotNull JsonWriter writer, @NotNull IIPBanEntry entry) throws IOException {
            writer.beginObject();
            writer.name("ip").value(entry.ip());
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
         * Reads a JSON object from a JsonReader and returns an instance of IIPBanEntry.
         *
         * @param reader The JsonReader from which to read the JSON object.
         * @return An instance of IIPBanEntry populated with the values read from the JSON object.
         * @throws IOException If an I/O error occurs while reading from the JsonReader.
         */
        @Override
        public IIPBanEntry read(@NotNull JsonReader reader) throws IOException {
            String ip = null;
            String source = null;
            Date created = null;
            Object expires = null;
            String reason = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String propName = reader.nextName();
                switch (propName) {
                    case "ip":
                        ip = reader.nextString();
                        break;
                    case "source":
                        source = reader.nextString();
                        break;
                    case "created":
                        try {
                            created = format.parse(reader.nextString());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "expires":
                        String g = reader.nextString();
                        try {
                            expires = format.parse(g);
                        } catch (ParseException e) {
                            expires = g;
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

            return new IPBanEntry(ip, source, created, expires, reason);
        }
    }

}
