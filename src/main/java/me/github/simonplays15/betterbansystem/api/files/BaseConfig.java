package me.github.simonplays15.betterbansystem.api.files;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The `BaseConfig`
 * class is a configuration utility that provides methods to read and write configuration data using YAML format.
 * It utilizes the `Yaml` library to parse and serialize the configuration data.
 */
public class BaseConfig {
    /**
     * Map variable "config" stores configuration data.
     * The key is a string and the value can be any object.
     * This variable is private and can only be accessed within the containing class "BaseConfig".
     */
    private Map<String, Object> config = new HashMap<>();

    /**
     * BaseConfig class is used for managing configurations.
     */
    public BaseConfig() {
    }


    /**
     * Saves the configuration to a file.
     *
     * @param file the file to save the configuration to
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
     * Loads the configuration from the given file.
     *
     * @param file The file to load the configuration from.
     */
    public void load(@NotNull File file) {
        Yaml yaml = b();
        try (InputStream stream = new FileInputStream(file)) {
            this.config = yaml.load(stream);
        } catch (IOException ex) {
            GlobalLogger.getLogger().error("Failed to load configuration file", ex);
        }
    }

    /**
     * Constructs a new instance of Yaml with the specified options.
     *
     * @return a new instance of Yaml
     */
    protected Yaml b() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setAllowUnicode(true);
        return new Yaml(options);
    }

    /**
     * Retrieves the value associated with the specified key from the configuration.
     * If the value is not found, it returns the default value provided.
     *
     * @param key The key to look up in the configuration (can be nested with dots)
     * @return The value associated with the key, or the default value if not found
     */
    public Object get(String key) {
        return this.get(key, null);
    }

    /**
     * Retrieves the value associated with the specified key from the config map.
     * If the config map is empty, an empty string is returned.
     * If the key is empty or null, null is returned.
     * If the key does not contain a dot ('.'), the value associated with the key is returned from the config map,
     * or the default value if the key is not found.
     * If the key contains a dot ('.'), the value associated with the key is returned from the nested map structure,
     * or null if any of the intermediate keys are missing or if the value is not found.
     *
     * @param key the key to retrieve the value for
     * @param def the default value to return if the key is not found
     * @return the value associated with the key, or the default value if the key is not found
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

    /**
     * Retrieves the value of the specified path from the configuration as a string.
     *
     * @param path The path to the string value.
     * @return The string value associated with the specified path, or null if the path is not found.
     */
    @Nullable
    public String getString(@NotNull String path) {
        return this.getString(path, null);
    }

    /**
     * Retrieves a string value from the configuration.
     *
     * @param path the path of the string value
     * @param def  the default value to return if the path is not found
     * @return the string value at the specified path, or the default value if the path is not found
     */
    @Contract("_, !null -> !null")
    @Nullable
    public String getString(@NotNull String path, @Nullable String def) {
        Object val = this.get(path, def);
        return val != null ? val.toString() : def;
    }

    /**
     * Returns the integer value associated with the specified path in the configuration. If no value is found, the default value provided is returned.
     *
     * @param path The path to the integer value in the configuration. Cannot be null.
     * @return The integer value at the specified path, or the default value if no value is found.
     */
    public int getInt(@NotNull String path) {
        return this.getInt(path, 0);
    }

    /**
     * Returns the integer value associated with the specified path in the configuration.
     * If the value is not found or is not of type Number, the default value will be returned.
     *
     * @param path the path to the value in the configuration
     * @param def  the default value to be returned if the value is not found or is not of type Number
     * @return the integer value associated with the specified path, or the default value if not found or not of type Number
     */
    public int getInt(@NotNull String path, int def) {
        Object val = this.get(path, def);
        return val instanceof Number ? ((Number) val).intValue() : def;
    }

    /**
     * Retrieves the boolean value at the specified path in the configuration.
     *
     * @param path the path to the boolean value in the configuration
     * @return the boolean value at the specified path, or false if it doesn't exist or is not a boolean
     */
    public boolean getBoolean(@NotNull String path) {
        return this.getBoolean(path, false);
    }

    /**
     * Retrieves the boolean value associated with the specified path from the configuration. If the path does not exist,
     * or the value is not a boolean, the default value is returned.
     *
     * @param path The path to the boolean value in the configuration.
     * @param def  The default value to return if the path does not exist or the value is not a boolean.
     * @return The boolean value associated with the specified path, or the default value if the path does not exist
     * or the value is not a boolean.
     */
    public boolean getBoolean(@NotNull String path, boolean def) {
        Object val = this.get(path, def);
        return val instanceof Boolean ? (Boolean) val : def;
    }

    /**
     * Retrieves a double value from the configuration using the given path.
     *
     * @param path The path of the configuration value to retrieve.
     * @return The retrieved double value. If the value is not found or is not a number, the default value of 0.0 is returned.
     */
    public double getDouble(@NotNull String path) {
        return this.getDouble(path, 0.0);
    }

    /**
     * Returns the value associated with the given path as a double. If no value is found at the path, the default value is returned.
     *
     * @param path the path to retrieve the value from
     * @param def  the default value to return if no value is found at the path
     * @return the value associated with the path as a double, or the default value if no value is found or the value is not a number
     */
    public double getDouble(@NotNull String path, double def) {
        Object val = this.get(path, def);
        return val instanceof Number ? ((Number) val).doubleValue() : def;
    }

    /**
     * Returns the long value associated with the specified path.
     *
     * @param path the path to the long value
     * @return the long value associated with the specified path, or 0L if the path does not exist
     */
    public long getLong(@NotNull String path) {
        return this.getLong(path, 0L);
    }

    /**
     * Retrieves a long value from the configuration at the specified path.
     *
     * @param path the path to the configuration value
     * @param def  the default value to return if the configuration value is not found or is invalid
     * @return the long value at the specified path, or the default value if the configuration value is not found or is invalid
     */
    public long getLong(@NotNull String path, long def) {
        Object val = this.get(path, def);
        return val instanceof Number ? ((Number) val).longValue() : def;
    }

    /**
     * Retrieves a list of objects from the configuration file at a given path.
     *
     * @param path The path to the list in the configuration file.
     * @return The list of objects at the given path, or null if the path does not exist.
     */
    @Nullable
    public List<?> getList(@NotNull String path) {
        return this.getList(path, null);
    }

    /**
     * Retrieves a List value from the configuration based on the provided path.
     *
     * @param path the path to the List value in the configuration (e.g., "parent.child.list")
     * @param def  the default List value to return if the path does not exist or the value is not a List
     * @return the List value associated with the path, or the default value if not found or not a List
     */
    @Contract("_, !null -> !null")
    @Nullable
    public List<?> getList(@NotNull String path, @Nullable List<?> def) {
        Object val = this.get(path, def);
        return (List<?>) (val instanceof List ? val : def);
    }

    /**
     * Retrieves a list of strings from the specified path.
     *
     * @param path the path to the list
     * @return a list of strings from the specified path, an empty list if the path does not exist or is not a list
     */
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

    /**
     * Returns a list of strings from the specified path in the configuration.
     *
     * @param path the path to the list of strings
     * @param def  the default list of strings to return if the specified path does not exist or is not a list of strings
     * @return a list of strings from the specified path, or the default list of strings if the specified path does not exist or is not a list of strings
     */
    public List<String> getStringList(String path, List<String> def) {
        List<?> list = this.getList(path, def);
        if (list == null)
            return new ArrayList<>(0);
        return new ArrayList<>(def);
    }

    /**
     * Checks if the specified path exists in the configuration.
     *
     * @param path the path to check
     * @return true if the path exists, false otherwise
     */
    public boolean contains(String path) {
        return this.config.containsKey(path);
    }

    /**
     * Checks if the value at the specified path is a string.
     *
     * @param path the path to the value
     * @return true if the value at the specified path is a string, false otherwise
     */
    public boolean isString(String path) {
        Object g = this.get(path);
        return g instanceof String;
    }

    /**
     * Determines if the value stored at the specified path is of type Integer.
     *
     * @param path the path of the value to check
     * @return true if the value is of type Integer, false otherwise
     */
    public boolean isInt(String path) {
        Object g = this.get(path);
        return g instanceof Integer;
    }

    /**
     * Checks if the value at the specified path is a boolean.
     *
     * @param path the path to check
     * @return true if the value is a boolean, false otherwise
     */
    public boolean isBoolean(String path) {
        Object g = this.get(path);
        return g instanceof Boolean;
    }

    /**
     * Check if the value located at the specified path is a double.
     *
     * @param path the path to the value
     * @return true if the value is a double, false otherwise
     */
    public boolean isDouble(String path) {
        Object g = this.get(path);
        return g instanceof Double;
    }

    /**
     * Checks if the value at the specified path is of type Long.
     *
     * @param path the path to the value
     * @return true if the value is of type Long, false otherwise
     */
    public boolean isLong(@NotNull String path) {
        Object val = this.get(path);
        return val instanceof Long;
    }

    /**
     * Checks if the value at the specified path is a List.
     *
     * @param path The path to check.
     * @return True if the value at the specified path is a List, false otherwise.
     */
    public boolean isList(@NotNull String path) {
        Object val = this.get(path);
        return val instanceof List;
    }
}
