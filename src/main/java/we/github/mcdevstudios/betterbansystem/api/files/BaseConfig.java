/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.files;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseConfig {

    private Map<String, Object> config;

    public BaseConfig() {
    }


    /**
     * @param file {@link File} to save
     */
    public void save(File file) {
        try (Writer fileWriter = new FileWriter(file)) {
            Yaml yaml = b();
            yaml.dump(this.config, fileWriter);
        } catch (IOException ex) {
            GlobalLogger.getLogger().error("Failed to save configuration file", ex);
        }
    }

    /**
     * @param file {@link File} to load
     */
    public void load(File file) {
        Yaml yaml = b();
        try (InputStream stream = new FileInputStream(file)) {
            this.config = yaml.load(stream);
            return;
        } catch (IOException ex) {
            GlobalLogger.getLogger().error("Failed to load configuration file", ex);
        }
        this.config = new HashMap<>();
    }

    protected Yaml b() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        return new Yaml(options);
    }

    /**
     * @param key {@link String} with the path to the specific configuration path
     * @return the Value of the given key | null
     */
    public Object get(String key) {
        return this.get(key, null);
    }

    /**
     * @param def {@link Object} as default value if the given key is null or not found
     * @param key {@link String} with the path to the specific configuration path
     * @return the Value of the given key | null
     */
    @SuppressWarnings("unchecked")
    public Object get(String key, Object def) {
        if (this.config.isEmpty()) {
            return "";
        }
        if (key.isEmpty())
            return null;
        if (!key.contains(".")) {
            return this.config.getOrDefault(key, def);
        }

        String[] keys = key.split("\\.");
        Map<String, Object> current = this.config;
        Object result = null;
        for (String k : keys) {
            Object val = current.get(k);
            if (!(val instanceof Map<?, ?>)) {
                result = val;
                continue;
            }
            current = (Map<String, Object>) val;
        }
        return result;
    }

    @Nullable
    public String getString(@NotNull String path) {
        return this.getString(path, null);
    }

    @Contract("_, !null -> !null")
    @Nullable
    public String getString(@NotNull String path, @Nullable String def) {
        Object val = this.get(path, def);
        return val != null ? val.toString() : def;
    }

    public int getInt(@NotNull String path) {
        return this.getInt(path, 0);
    }

    public int getInt(@NotNull String path, int def) {
        Object val = this.get(path, def);
        return val instanceof Number ? ((Number) val).intValue() : def;
    }

    public boolean getBoolean(@NotNull String path) {
        return this.getBoolean(path, false);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        Object val = this.get(path, def);
        return val instanceof Boolean ? (Boolean) val : def;
    }

    public double getDouble(@NotNull String path) {
        return this.getDouble(path, 0.0);
    }

    public double getDouble(@NotNull String path, double def) {
        Object val = this.get(path, def);
        return val instanceof Number ? ((Number) val).doubleValue() : def;
    }

    public long getLong(@NotNull String path) {
        return this.getLong(path, 0L);
    }

    public long getLong(@NotNull String path, long def) {
        Object val = this.get(path, def);
        return val instanceof Number ? ((Number) val).longValue() : def;
    }

    @Nullable
    public List<?> getList(@NotNull String path) {
        return this.getList(path, null);
    }

    @Contract("_, !null -> !null")
    @Nullable
    public List<?> getList(@NotNull String path, @Nullable List<?> def) {
        Object val = this.get(path, def);
        return (List<?>) (val instanceof List ? val : def);
    }

    public List<String> getStringList(String path) {
        List<?> list = this.getList(path);
        if (list == null)
            return new ArrayList<>(0);
        List<String> result = new ArrayList<>();
        for (Object o : list) {
            result.add(String.valueOf(o));
        }

        return result;
    }

    public boolean contains(String path) {
        return this.config.containsKey(path);
    }

    public boolean isString(String path) {
        Object g = this.get(path);
        return g instanceof String;
    }

    public boolean isInt(String path) {
        Object g = this.get(path);
        return g instanceof Integer;
    }

    public boolean isBoolean(String path) {
        Object g = this.get(path);
        return g instanceof Boolean;
    }

    public boolean isDouble(String path) {
        Object g = this.get(path);
        return g instanceof Double;
    }

    public boolean isLong(@NotNull String path) {
        Object val = this.get(path);
        return val instanceof Long;
    }

    public boolean isList(@NotNull String path) {
        Object val = this.get(path);
        return val instanceof List;
    }
}
