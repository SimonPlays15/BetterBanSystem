/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.warn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

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

    public static void saveToJson(IWarnEntry entry) {
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);

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
