/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.files;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.Reader;
import java.util.Map;
import java.util.regex.Pattern;

public class BasePluginDescription {
    private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z0-9 _.-]+$");
    private final Yaml yaml = new Yaml();
    private String name;
    private String version;
    private String main;
    private Map<String, Map<String, Object>> commands;
    private String description;

    public BasePluginDescription(@NotNull InputStream stream) throws InvalidObjectException {
        loadMap(asMap(this.yaml.load(stream)));
    }

    public BasePluginDescription(@NotNull Reader reader) throws InvalidObjectException {
        loadMap(asMap(this.yaml.load(reader)));
    }

    @Contract(value = "null -> fail", pure = true)
    private @NotNull Map<?, ?> asMap(Object object) throws InvalidObjectException {
        if (object instanceof Map<?, ?>)
            return (Map<?, ?>) object;

        throw new InvalidObjectException(object + " is not properly structured");
    }

    /**
     * @param map Map<?, ?>
     * @throws InvalidObjectException if {@link BasePluginDescription#VALID_NAME} does not match
     * @throws NullPointerException   if {@link Map#get(Object)} values not exists
     * @see org.bukkit.plugin.PluginDescriptionFile
     */
    private void loadMap(Map<?, ?> map) throws InvalidObjectException, NullPointerException {
        //TODO Throws NullPointer
        this.name = map.get("name").toString();
        if (!VALID_NAME.matcher(this.name).matches()) {
            throw new InvalidObjectException("name '" + this.name + "' contains invalid characters.");
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
                throw new InvalidObjectException("commands are of wrong type");
            }
            this.commands = commandsBuilder.build();
        }
    }

    public String getName() {
        return name;
    }

    public String getMain() {
        return main;
    }

    public Map<String, Map<String, Object>> getCommands() {
        return commands;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }
}
