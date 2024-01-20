package me.github.simonplays15.betterbansystem.api.files;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.github.simonplays15.betterbansystem.api.exceptions.InvalidDescriptionException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The BasePluginDescription class represents the description of a plugin. It stores information about the plugin's name, version, main class, commands, and description.
 */
public class BasePluginDescription {
    /**
     * Represents a regular expression pattern that matches valid names.
     * Only allows alphanumeric characters, spaces, underscores, dots, and hyphens.
     */
    private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z0-9 _.-]+$");
    /**
     * Represents a YAML object used for parsing and handling YAML data.
     */
    private final Yaml yaml = new Yaml();
    /**
     * Represents a private string variable named "name" in the class BasePluginDescription.
     * This variable stores the name of the plugin.
     *
     * @see BasePluginDescription
     */
    private String name;
    /**
     * Represents the version of a plugin.
     */
    private String version;
    /**
     * Represents the "main" variable of a plugin description.
     * It specifies the main class or entry point of the plugin.
     */
    private String main;
    /**
     *
     */
    private Map<String, Map<String, Object>> commands;
    /**
     * Represents a private variable for the description of a plugin.
     * <p>
     * This variable is used in the BasePluginDescription class to store the description of a plugin.
     */
    private String description;

    /**
     * Constructs a new instance of the BasePluginDescription class with the given InputStream.
     *
     * @param stream The InputStream containing the plugin's description data.
     * @throws InvalidDescriptionException If the description is invalid.
     */
    public BasePluginDescription(@NotNull InputStream stream) throws InvalidDescriptionException {
        loadMap(asMap(this.yaml.load(stream)));
    }

    /**
     * Class: BasePluginDescription
     * Represents a plugin description.
     */
    public BasePluginDescription(@NotNull Reader reader) throws InvalidDescriptionException {
        loadMap(asMap(this.yaml.load(reader)));
    }

    /**
     * Converts the given object to a map.
     *
     * @param object the object to convert
     * @return a map representing the object
     * @throws InvalidDescriptionException if the object is not properly structured
     */
    @Contract(value = "null -> fail", pure = true)
    private @NotNull Map<?, ?> asMap(Object object) throws InvalidDescriptionException {
        if (object instanceof Map<?, ?>)
            return (Map<?, ?>) object;

        throw new InvalidDescriptionException(object + " is not properly structured");
    }

    /**
     * Load map.
     *
     * @param map The map containing plugin information.
     * @throws InvalidDescriptionException If the plugin name contains invalid characters.
     * @throws NullPointerException        If any of the required fields are missing in the map.
     * @see org.bukkit.plugin.PluginDescriptionFile
     */
    private void loadMap(Map<?, ?> map) throws InvalidDescriptionException, NullPointerException {
        //TODO Throws NullPointer
        this.name = map.get("name").toString();
        if (!VALID_NAME.matcher(this.name).matches()) {
            throw new InvalidDescriptionException("name '" + this.name + "' contains invalid characters.");
        }
        this.name = this.name.replace(' ', '_');
        this.version = map.get("version").toString();
        this.main = map.get("main").toString();
        this.description = map.get("description").toString();

        if (map.get("commands") != null) {
            ImmutableMap.Builder<String, Map<String, Object>> commandsBuilder = ImmutableMap.builder();
            try {
                for (Map.Entry<?, ?> command : ((Map<?, ?>) map.get("commands")).entrySet()) {
                    ImmutableMap.Builder<String, Object> commandBuilder = ImmutableMap.builder();
                    if (command.getValue() != null)
                        for (Map.Entry<?, ?> commandEntry : ((Map<?, ?>) command.getValue()).entrySet()) {
                            if (command.getValue() instanceof Iterable) {
                                ImmutableList.Builder<Object> commandSubList = ImmutableList.builder();
                                for (Object commandSubListItem : (Iterable<?>) commandEntry.getValue()) {
                                    if (commandSubListItem != null) {
                                        commandSubList.add(commandSubListItem);
                                    }
                                }
                                commandBuilder.put(commandEntry.getKey().toString(), commandSubList.build());
                                continue;
                            }
                            if (commandEntry.getValue() != null) {
                                commandBuilder.put(commandEntry.getKey().toString(), commandEntry.getValue());
                            }
                        }
                    commandsBuilder.put(command.getKey().toString(), commandBuilder.build());
                }
            } catch (Exception ex) {
                throw new InvalidDescriptionException("commands are of wrong type");
            }
            this.commands = commandsBuilder.build();
        }
    }

    /**
     * Returns the name of the plugin.
     *
     * @return The name of the plugin.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the main class specified in the plugin description.
     *
     * @return the main class specified in the plugin description
     */
    public String getMain() {
        return main;
    }

    /**
     * Retrieves the commands stored in a map.
     *
     * @return A map of commands, where the key is a string representing the command name and the value is another map object containing the command data.
     */
    public Map<String, Map<String, Object>> getCommands() {
        return commands;
    }

    /**
     * Retrieves the description of the plugin.
     *
     * @return The description of the plugin as a string.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the version of the plugin.
     *
     * @return The version of the plugin.
     */
    public String getVersion() {
        return version;
    }
}
