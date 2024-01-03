/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.mute;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public record MuteEntry(UUID uuid, String name, String source, Date created,
                        Object expires, String reason)
        implements IMuteEntry {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss Z").registerTypeAdapter(IMuteEntry.class, new IMuteEntryAdapter()).create();
    private static final File file = new File("muted-players.json");

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
    private static final String MUTE_TABLENAME = "mutedplayers";

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

    public static void removeEntry(@NotNull IMuteEntry entry) {
        removeFromJson(entry.uuid());
    }

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

    public static IMuteEntry findEntry(UUID targetUUID) {
        List<IMuteEntry> entries = getAllEntries();
        return entries.stream()
                .filter(entry -> entry.uuid().equals(targetUUID))
                .findFirst()
                .orElse(null);
    }

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

    public static class IMuteEntryAdapter extends TypeAdapter<IMuteEntry> {
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
