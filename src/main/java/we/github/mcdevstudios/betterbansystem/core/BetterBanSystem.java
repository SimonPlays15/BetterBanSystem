/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.files.BaseConfig;
import we.github.mcdevstudios.betterbansystem.api.files.BasePluginDescription;
import we.github.mcdevstudios.betterbansystem.api.files.LanguageFile;
import we.github.mcdevstudios.betterbansystem.api.files.ResourceFile;
import we.github.mcdevstudios.betterbansystem.api.runtimeservice.RuntimeService;
import we.github.mcdevstudios.betterbansystem.core.ban.BanManager;
import we.github.mcdevstudios.betterbansystem.core.chat.ChatColor;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;
import we.github.mcdevstudios.betterbansystem.core.mute.MuteManager;
import we.github.mcdevstudios.betterbansystem.core.permissions.BungeeCordDefaultHandler;
import we.github.mcdevstudios.betterbansystem.core.permissions.PermissionsHandlerType;
import we.github.mcdevstudios.betterbansystem.core.permissions.PermissionsManager;
import we.github.mcdevstudios.betterbansystem.core.permissions.SpigotPermissionsHandler;

import java.io.File;

public class BetterBanSystem {

    /**
     * The singleton instance of the BetterBanSystem class.
     * <p>
     * This variable allows access to the BetterBanSystem class from anywhere in the code.
     * It ensures that there is only one instance of the class throughout the application.
     * <p>
     * Containing Class: BetterBanSystem
     * <p>
     * Type: BetterBanSystem
     * <p>
     * Example Usage:
     * BetterBanSystem system = BetterBanSystem.getInstance();
     */
    private static BetterBanSystem instance;

    /**
     * Represents the folder where data is stored.
     */
    private final File dataFolder;
    /**
     * Represents the configuration file used by the application.
     */
    private final File configFile;
    /**
     * This class represents the plugin description for the base plugin.
     */
    private final BasePluginDescription basePluginDescription;
    /**
     * The prefix variable represents the prefix used in the BetterBanSystem class.
     * It is a string that is used to prefix messages or log statements in the system.
     * It can be set and retrieved using the getter and setter methods defined in the BetterBanSystem class.
     */
    public String prefix;
    /**
     * The `config` variable is an instance of the `BaseConfig` class. It represents a configuration object that
     * stores key-value pairs for various settings and options.
     * <p>
     * The `BaseConfig` class provides methods for loading and saving configuration data from a file, retrieving
     * values based on keys, and checking the type of value.
     * <p>
     * The configuration data is stored internally as a `Map<String, Object>` where the keys are strings representing
     * the path to a specific configuration property, and the values are the corresponding values for those properties.
     * <p>
     * The `BaseConfig` class provides the following methods:
     * <p>
     * - `save(File)`: Saves the configuration data to the specified file using the YAML format.
     * <p>
     * - `load(File)`: Loads the configuration data from the specified file using the YAML format.
     * <p>
     * - `get(String key)`: Returns the value associated with the specified key. If the key is not found, it returns null.
     * <p>
     * - `get(String key, Object def)`: Returns the value associated with the specified key. If the key is not found,
     * it returns the default value specified by `def`.
     * <p>
     * - `getString(String path)`: Returns the value associated with the specified key as a string. If the key is not
     * found, it returns null.
     * <p>
     * - `getString(String path, String def)`: Returns the value associated with the specified key as a string. If the
     * key is not found, it returns the default value specified by `def`.
     * <p>
     * - `getInt(String path)`: Returns the value associated with the specified key as an integer. If the key is not
     * found or the value is not a valid integer, it returns 0.
     * <p>
     * - `getInt(String path, int def)`: Returns the value associated with the specified key as an integer. If the key
     * is not found or the value is not a valid integer, it returns the default value specified by `def`.
     * <p>
     * - `getBoolean(String path)`: Returns the value associated with the specified key as a boolean. If the key is not
     * found or the value is not a valid boolean, it returns false.
     * <p>
     * - `getBoolean(String path, boolean def)`: Returns the value associated with the specified key as a boolean.
     * If the key is not found or the value is not a valid boolean, it returns the default value specified by `def`.
     * <p>
     * - `getDouble(String path)`: Returns the value associated with the specified key as a double. If the key is not
     * found or the value is not a valid double, it returns 0.0.
     * <p>
     * - `getDouble(String path, double def)`: Returns the value associated with the specified key as a double.
     * If the key is not found or the value is not a valid double, it returns the default value specified by `def`.
     * <p>
     * - `getLong(String path)`: Returns the value associated with the specified key as a long. If the key is not
     * found or the value is not a valid long, it returns 0L.
     * <p>
     * - `getLong(String path, long def)`: Returns the value associated with the specified key as a long. If the key
     * is not found or the value is not a valid long, it returns the default value specified by `def`.
     * <p>
     * - `getList(String path)`: Returns the value associated with the specified key as a list. If the key is not
     * found or the value is not a valid list, it returns null.
     * <p>
     * - `getList(String path, List<?> def)`: Returns the value associated with the specified key as a list. If the key
     * is not found or the value is not a valid list, it returns the default value specified by `def`.
     * <p>
     * - `getStringList(String path)`: Returns the value associated with the specified key as a list of strings. If
     * the key is not found or the value is not a valid list, it returns an empty list.
     * <p>
     * - `contains(String path)`: Checks if the configuration contains the specified key.
     * <p>
     * - `isString(String path)`: Checks if the value associated with the specified key is a string.
     * <p>
     * - `isInt(String path)`: Checks if the value associated with the specified key is an integer.
     * <p>
     * - `isBoolean(String path)`: Checks if the value associated with the specified key is a boolean.
     * <p>
     * - `isDouble(String path)`: Checks if the value associated with the specified key is a double.
     * <p>
     * - `isLong(String path)`: Checks if the value associated with the specified key is a long.
     * <p>
     * - `isList(String path)`: Checks if the value associated with the specified key is a list.
     */
    public BaseConfig config;
    /**
     * Represents a language file which stores key-value pairs for different messages and their translations.
     * The messages are loaded from a YAML file specified by the languagePath parameter.
     * If the file fails to be loaded, a default language file ("language/en_US.yml") is used instead.
     */
    private LanguageFile languageFile;
    /**
     * Represents a permissions' manager.
     * This class is abstract and cannot be instantiated directly.
     * Use the static methods to obtain an instance of a specific permissions' manager.
     */
    private PermissionsManager manager;

    /**
     * Constructs a new instance of the BetterBanSystem class.
     *
     * @param dataFolder The data folder for the plugin.
     * @throws RuntimeException         If the correct runtime service cannot be loaded.
     * @throws NullPointerException     If the dataFolder is null.
     * @throws IllegalArgumentException If the plugin.yml or bungee.yml resource cannot be found.
     */

    public BetterBanSystem(File dataFolder) throws RuntimeException {
        instance = this;
        this.dataFolder = dataFolder;
        ResourceFile resourceFile = new ResourceFile(this.dataFolder);

        if (RuntimeService.isSpigot())
            this.basePluginDescription = new BasePluginDescription(resourceFile.getResource("plugin.yml"));
        else if (RuntimeService.isBungeeCord())
            this.basePluginDescription = new BasePluginDescription(resourceFile.getResource("bungee.yml"));
        else
            throw new RuntimeException("Failed to load the correct runtime service. Is this plugin running on an Spigot or BungeeCord Server?");

        this.configFile = new File(this.dataFolder, "config.yml");
        resourceFile.saveResource("config.yml", true);
        resourceFile.saveResource("language/de_DE.yml", true);
        resourceFile.saveResource("language/en_US.yml", true);
        this.config = new BaseConfig();
        this.config.load(this.configFile);
        if (this.config.getBoolean("chat.usePrefix")) {
            this.prefix = ChatColor.translateAlternateColorCodes('&', this.config.getString("chat.prefix", "§6[§$cBetterBanSystem§$6]§r "));
        } else {
            this.prefix = "";
        }

        this.loadLanguage(this.config.getString("chat.language", "en_US"));
        this.loadPermissionsSystem(PermissionsHandlerType.valueOf(this.config.getString("permissions.system", "SPIGOT").toUpperCase()));

        new BanManager().start();
        new MuteManager().start();
    }

    /**
     * Returns the instance of the BetterBanSystem.
     *
     * @return The instance of the BetterBanSystem.
     */
    @Contract(pure = true)
    public static BetterBanSystem getInstance() {
        return instance;
    }

    /**
     * Retrieves the prefix of the BetterBanSystem instance.
     *
     * @return the prefix if it is not null and not empty, otherwise returns a default prefix
     */
    public String getPrefix() {
        if (this.prefix == null) {
            return "§6[§cBetterBanSystem§6]§r ";
        } else if (this.prefix.isEmpty()) {
            return "§r";
        }
        return prefix + "§r ";
    }

    /**
     * Loads a language file with the given name. If the file name does not end with ".yml", it will be appended automatically.
     * The language file is loaded using the LanguageFile class and stored in the languageFile field.
     *
     * @param language the name of the language file to load
     * @throws NullPointerException if language is null
     */
    public void loadLanguage(@NotNull String language) {
        if (!language.endsWith(".yml"))
            language += ".yml";
        this.languageFile = new LanguageFile(this.getDataFolder().getPath() + "/language/" + language);
    }

    /**
     * Loads the permissions system based on the provided handler type.
     * If the specified handler type is not available, it falls back to the default handler based on the runtime environment.
     *
     * @param type The type of permissions handler to load. See {@link PermissionsHandlerType}.
     */
    public void loadPermissionsSystem(PermissionsHandlerType type) {
        try {
            this.manager = PermissionsManager.getHandler(type);
        } catch (Exception e) {
            GlobalLogger.getLogger().error("Failed to get a permission manager. Falling back to default", e);
        }
        if (RuntimeService.isSpigot())
            this.manager = new SpigotPermissionsHandler();
        else if (RuntimeService.isBungeeCord())
            this.manager = new BungeeCordDefaultHandler();
    }

    /**
     * Reloads the configuration file.
     * If the configuration object is null, a new instance of BaseConfig is created.
     * The configuration file is loaded using a Yaml object.
     * If the file is successfully loaded, the configuration object is updated with the contents of the file.
     * If there is an error loading the file, a new empty configuration object is created.
     */
    public void reloadConfig() {
        if (this.config == null)
            this.config = new BaseConfig();
        this.config.load(this.configFile);
    }

    /**
     * Saves the configuration file.
     */
    public void saveConfig() {
        this.config.save(this.configFile);
    }

    /**
     * Returns the data folder for the plugin.
     *
     * @return The data folder for the plugin.
     */
    public File getDataFolder() {
        return dataFolder;
    }

    /**
     * Retrieves the BaseConfig object that holds the configuration settings.
     *
     * @return the BaseConfig object
     */
    public BaseConfig getConfig() {
        return config;
    }

    /**
     * Retrieves the LanguageFile object associated with the BetterBanSystem instance.
     * The LanguageFile object stores the messages and translations used by the plugin.
     *
     * @return The LanguageFile object.
     */
    public LanguageFile getLanguageFile() {
        return languageFile;
    }

    /**
     * Returns the PermissionsManager instance used by the BetterBanSystem.
     *
     * @return The PermissionsManager instance.
     */
    public PermissionsManager getPermissionsManager() {
        return manager;
    }

    /**
     * Returns the plugin description of the current instance.
     *
     * @return The plugin description.
     */
    public BasePluginDescription getPluginDescription() {
        return basePluginDescription;
    }
}
