/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.ban;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public record IPBanEntry(String ip, String source, Date created,
                         Object expires, String reason)
        implements IIPBanEntry {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss Z").registerTypeAdapter(IIPBanEntry.class, new IIPBanEntryAdapter()).create();
    private static final File file = new File("banned-ips.json");

    public static void saveToJson(IIPBanEntry entry) {
        List<IIPBanEntry> entries = new ArrayList<>();

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    GlobalLogger.getLogger().info("File " + file.getName() + " created.");
                }
            } catch (IOException e) {
                GlobalLogger.getLogger().error(e);
                return;
            }
        }
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

    public static void removeEntry(IIPBanEntry entry) {
        removeFromJson(entry.ip());
    }

    public static void removeFromJson(String ipAddress) {
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

    public static List<IIPBanEntry> getAllEntries() {
        List<IIPBanEntry> entries = new ArrayList<>();
        try (Reader reader = new FileReader(file.getName())) {
            Type listType = new TypeToken<ArrayList<IIPBanEntry>>() {
            }.getType();
            entries = gson.fromJson(reader, listType);
        } catch (IOException e) {
            GlobalLogger.getLogger().error(e);
        }
        return entries;
    }

    public static IIPBanEntry findEntry(String ipAddress) {
        List<IIPBanEntry> entries = getAllEntries();
        return entries.stream()
                .filter(entry -> entry.ip().equals(ipAddress))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "IPBanEntry{" +
                "ip='" + ip + '\'' +
                ", source='" + source + '\'' +
                ", created=" + created +
                ", expires=" + expires +
                ", reason='" + reason + '\'' +
                '}';
    }

    public static class IIPBanEntryAdapter extends TypeAdapter<IIPBanEntry> {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);

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
