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

public record WarnEntry(UUID uuid, String name, List<Warn> warns) implements IWarnEntry {
    public static final AtomicInteger idGenerator = new AtomicInteger(1);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss Z").registerTypeAdapter(IWarnEntry.class, new IWarnEntryAdapter()).create();
    private static final File file = new File("player-warns.json");
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
    private static final String WARNED_PLAYERS_TABLENAME = "warnedplayers";
    private static final String PLAYER_WARNS_TABLENAME = "warns";

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

    public static void removeEntry(@NotNull IWarnEntry entry) {
        removeFromJson(entry.uuid());
    }

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

    public static IWarnEntry findEntry(UUID targetUUID) {
        List<IWarnEntry> entries = getAllEntries();
        return entries.stream()
                .filter(entry -> entry.uuid().equals(targetUUID))
                .findFirst()
                .orElse(null);
    }

    public void addWarn(Warn warn) {
        this.warns.add(warn);
    }

    public boolean removeWarn(int id) {
        return this.warns.removeIf(warn -> warn.id() == id);
    }

    public static class IWarnEntryAdapter extends TypeAdapter<IWarnEntry> {
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