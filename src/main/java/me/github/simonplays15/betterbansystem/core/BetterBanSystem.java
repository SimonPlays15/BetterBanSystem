package me.github.simonplays15.betterbansystem.core;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.files.BaseConfig;
import me.github.simonplays15.betterbansystem.api.files.BasePluginDescription;
import me.github.simonplays15.betterbansystem.api.files.LanguageFile;
import me.github.simonplays15.betterbansystem.api.files.ResourceFile;
import me.github.simonplays15.betterbansystem.api.runtimeservice.RuntimeService;
import me.github.simonplays15.betterbansystem.api.uuid.UUIDFetcher;
import me.github.simonplays15.betterbansystem.core.ban.BanManager;
import me.github.simonplays15.betterbansystem.core.chat.ChatColor;
import me.github.simonplays15.betterbansystem.core.command.BaseCommandHandler;
import me.github.simonplays15.betterbansystem.core.database.DriverType;
import me.github.simonplays15.betterbansystem.core.database.IDatabase;
import me.github.simonplays15.betterbansystem.core.database.mongodb.CachedMongoDBDatabase;
import me.github.simonplays15.betterbansystem.core.database.mysql.CachedMySQLDatabase;
import me.github.simonplays15.betterbansystem.core.database.sqlite.CachedSQLiteDatabase;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import me.github.simonplays15.betterbansystem.core.mute.MuteManager;
import me.github.simonplays15.betterbansystem.core.permissions.BungeeCordDefaultHandler;
import me.github.simonplays15.betterbansystem.core.permissions.PermissionsHandlerType;
import me.github.simonplays15.betterbansystem.core.permissions.PermissionsManager;
import me.github.simonplays15.betterbansystem.core.permissions.SpigotPermissionsHandler;
import me.github.simonplays15.betterbansystem.core.player.BaseCommandSender;
import me.github.simonplays15.betterbansystem.core.warn.WarnManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public abstract class BetterBanSystem {

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
     * This variable is used to cache an instance of the BaseCommandSender class.
     * It is declared as private, static and volatile to ensure thread safety in a multi-threaded environment.
     * The cachedSender variable is initially set to null and will be assigned an instance of the BaseCommandSender class when needed.
     * <p>
     * The BaseCommandSender class represents a command sender, such as a player or the console, that can execute commands in the plugin.
     * <p>
     * To access the cachedSender variable, use the appropriate getter method in the BetterBanSystem class.
     * <p>
     * Example usage:
     * BaseCommandSender sender = BetterBanSystem.getConsoleSender();
     * <p>
     * Note: The example code is not included in the documentation to comply with the requirements.
     */
    private static volatile BaseCommandSender cachedSender = null;
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
     * The `commandHandler` variable is an instance of the `BaseCommandHandler` class.
     * It represents a command handler that manages registered commands.
     * <p>
     * The `BaseCommandHandler` class has the following methods:
     * <p>
     * - `registerCommand(BaseCommand)`:
     * This method is used to register a new command with the command handler.
     * It takes a `BaseCommand` object as a parameter and adds it to the internal map of commands.
     * If a command with the same name is already registered, the method does nothing.
     * <p>
     * - `getCommands()`:
     * This method returns a map containing all registered commands.
     * <p>
     * Example usage:
     * ```
     * BaseCommandHandler commandHandler = new BaseCommandHandler();
     * BaseCommand myCommand = new MyCommand();
     * commandHandler.registerCommand(myCommand);
     * Map<String, BaseCommand> commands = commandHandler.getCommands();
     * ```
     */
    private final BaseCommandHandler commandHandler;
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
     * Private variable that holds the database object associated with the BetterBanSystem instance.
     */
    private IDatabase database;

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
        UUIDFetcher.loadUsercacheJson();
        ResourceFile resourceFile = new ResourceFile(this.dataFolder);

        if (RuntimeService.isSpigot())
            this.basePluginDescription = new BasePluginDescription(resourceFile.getResource("plugin.yml"));
        else if (RuntimeService.isBungeeCord())
            this.basePluginDescription = new BasePluginDescription(resourceFile.getResource("bungee.yml"));
        else
            throw new RuntimeException("Failed to load the correct runtime service. Is this plugin running on an Spigot or BungeeCord Server?");

        this.configFile = new File(this.dataFolder, "config.yml");
        resourceFile.saveResource("config.yml", true);
        resourceFile.saveResource("language/en_US.yml", true);
        this.config = new BaseConfig();
        this.config.load(this.configFile);

        GlobalLogger.getLogger().setDebug(config.getBoolean("logging.debug", false));
        GlobalLogger.getLogger().setWriteLogsToFile(config.getBoolean("logging.logfile", false));

        String configPrefix = this.config.getString("chat.prefix", "§6[§cBetterBanSystem§$6]§r");
        if (configPrefix.isEmpty() || configPrefix.equalsIgnoreCase("none")) {
            this.prefix = "";
        } else {
            this.prefix = ChatColor.translateAlternateColorCodes('&', configPrefix);
        }

        this.loadLanguage(this.config.getString("chat.language", "en_US"));
        this.loadPermissionsSystem(PermissionsHandlerType.valueOf(this.config.getString("permissions.system", RuntimeService.isSpigot() ? "SPIGOT" : "BUNGEECORD").toUpperCase()));

        this.commandHandler = new BaseCommandHandler();

        if (!this.config.getString("database.type", "none").equalsIgnoreCase("none")) {
            try {
                DriverType type = DriverType.valueOf(Objects.requireNonNull(this.config.getString("database.type")).toUpperCase());
                if (type == DriverType.MYSQL) {
                    this.database = new CachedMySQLDatabase();
                    this.database.connect(this.config.getString("database.hostname") + ":" + this.config.getInt("database.port") + "/betterbansystem", this.config.getString("database.user"), this.config.getString("database.password"));
                } else if (type == DriverType.SQLITE) {
                    this.database = new CachedSQLiteDatabase();
                    this.database.connect(this.config.getString("database.dbFile"), null, null);
                } else if (type == DriverType.MONGODB) {
                    this.database = new CachedMongoDBDatabase();
                    this.database.connect(this.config.getString("database.hostname") + ":" + this.config.getInt("database.port") + "/betterbansystem", this.config.getString("database.user"), this.config.getString("database.password"));
                }
                if (this.database != null)
                    this.database.createDatabaseAndTables();
            } catch (IllegalArgumentException ex) {
                GlobalLogger.getLogger().error("Failed to find database type", this.config.getString("database.type", "none").toUpperCase(), "going back to default file handling.", ex);
            }
        }
        new BanManager().start();
        new MuteManager().start();
        new WarnManager().start();
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
     * Sends a message to a player.
     *
     * @param player  The player object to send the message to. Must be an instance of org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer.
     * @param message The message to send.
     * @throws IllegalArgumentException If player object is not an instance of org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer.
     */
    public static void sendMessage(@NotNull Object player, String message) {
        message = getInstance().prefix + message;
        if (RuntimeService.isSpigot()) {
            try {
                player.getClass().getMethod("sendMessage", String.class).invoke(player, message);
            } catch (ReflectiveOperationException ignored) {
            }
        } else if (RuntimeService.isBungeeCord()) {
            try {
                TextComponent textComponent = new TextComponent(message);
                player.getClass().getMethod("sendMessage", TextComponent.class).invoke(player, textComponent);
            } catch (ReflectiveOperationException ignored) {
            }
        } else {
            throw new IllegalArgumentException("Player object is not an instance of org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer. | Player object class: " + player.getClass());
        }
    }

    /**
     * Send a message to a player. The message will be prefixed with the plugin's prefix.
     * The player parameter can be an instance of org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer.
     * If it is any other type, an IllegalArgumentException will be thrown.
     *
     * @param player   The player object to send the message to.
     * @param messages The messages to send. Each message will be sent separately.
     *                 The messages will be prefixed with the plugin's prefix before sending.
     * @throws IllegalArgumentException If the player object is not an instance of org.bukkit.entity.Player
     *                                  or net.md_5.bungee.api.connection.ProxiedPlayer.
     */
    public static void sendMessage(Object player, String @NotNull ... messages) {
        for (String message : messages) {
            sendMessage(player, message);
        }
    }

    /**
     * Sends a message to a player using the given text component.
     *
     * @param player    the player object to send the message to
     * @param component the text component to send as the message
     * @throws IllegalArgumentException if the player object is not an instance of org.bukkit.entity.Player
     *                                  or net.md_5.bungee.api.connection.ProxiedPlayer
     */
    public static void sendMessage(@NotNull Object player, @NotNull TextComponent component) {
        component.setText(getInstance().getPrefix() + component.getText());
        if (RuntimeService.isSpigot()) {
            try {
                Object spigot = player.getClass().getMethod("spigot").invoke(player);
                spigot.getClass().getMethod("sendMessage", BaseComponent.class).invoke(spigot, component);
            } catch (ReflectiveOperationException ignored) {
            }
        } else if (RuntimeService.isBungeeCord()) {
            try {
                player.getClass().getMethod("sendMessage", BaseComponent.class).invoke(player, component);
            } catch (ReflectiveOperationException ignored) {

            }
        } else {
            throw new IllegalArgumentException("Player object is not an instance of org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer. | Player object class: " + player.getClass());
        }
    }

    /**
     * Sends a message to a player using the given text components. Each component will be sent separately and will be prefixed with the plugin's prefix.
     *
     * @param player     The player object to send the message to. Must be an instance of org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer.
     * @param components The text components to send. Each component will be sent separately and will be prefixed with the plugin's prefix.
     * @throws IllegalArgumentException If the player object is not an instance of org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer.
     */
    public static void sendMessage(Object player, @NotNull TextComponent @NotNull ... components) {
        for (TextComponent component : components) {
            component.setText(BetterBanSystem.getInstance().getPrefix() + component.getText());
            sendMessage(player, component);
        }
    }

    /**
     * Kicks a player from the server.
     * The method first checks if the given player object is of type 'org.bukkit.entity.Player',
     * in which case it uses reflection to invoke the 'kickPlayer' method on the player object
     * with the given message parameter.
     * If the player object is of type 'net.md_5.bungee.api.connection.ProxiedPlayer', it uses reflection
     * to invoke the 'disconnect' method on the player object with a 'BaseComponent' object created
     * from the message parameter.
     * If the player object is of any other type, the method does nothing.
     *
     * @param player  The player to be kicked. Must be of type 'org.bukkit.entity.Player'
     *                or 'net.md_5.bungee.api.connection.ProxiedPlayer'.
     * @param message The message to be displayed to the kicked player.
     */
    public static void kickPlayer(@NotNull Object player, String message) {
        if (player.getClass().getName().equals("org.bukkit.entity.Player")) {
            try {
                player.getClass().getMethod("kickPlayer", String.class).invoke(player, message);
            } catch (ReflectiveOperationException ignored) {
            }
        } else if (player.getClass().getName().equals("net.md_5.bungee.api.connection.ProxiedPlayer")) {
            try {
                TextComponent textComponent = new TextComponent(message);
                player.getClass().getMethod("disconnect", BaseComponent.class).invoke(player, textComponent);
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }

    /**
     * Retrieves the player object associated with the given player name. If the player is online, the player object will be returned. If the player is offline or cannot be found
     * , null will be returned.
     *
     * @param playerName The name of the player to retrieve.
     * @return The player object associated with the given name, or null if the player is offline or cannot be found.
     */
    public static @Nullable Object getPlayer(@NotNull String playerName) {
        if (RuntimeService.isSpigot()) {
            try {
                Class<?> bukkit = Class.forName("org.bukkit.Bukkit");
                return bukkit.getMethod("getPlayer", String.class).invoke(null, playerName);
            } catch (ReflectiveOperationException ignored) {
            }
        } else if (RuntimeService.isBungeeCord()) {
            try {
                Class<?> proxyServer = Class.forName("net.md_5.bungee.api.ProxyServer");
                Object instance = proxyServer.getMethod("getInstance").invoke(null);
                return instance.getClass().getMethod("getPlayer", String.class).invoke(instance, playerName);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    /**
     * Retrieves the player object associated with the given player ID.
     *
     * @param playerId The ID of the player.
     * @return The player object, or null if the player is not found.
     */
    public static @Nullable Object getPlayer(@NotNull UUID playerId) {
        if (RuntimeService.isSpigot()) {
            try {
                Class<?> bukkit = Class.forName("org.bukkit.Bukkit");
                return bukkit.getMethod("getPlayer", UUID.class).invoke(null, playerId);
            } catch (ReflectiveOperationException ignored) {
            }
        } else if (RuntimeService.isBungeeCord()) {
            try {
                Class<?> proxyServer = Class.forName("net.md_5.bungee.api.ProxyServer");
                Object instance = proxyServer.getMethod("getInstance").invoke(null);
                return instance.getClass().getMethod("getPlayer", UUID.class).invoke(instance, playerId);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    /**
     * Checks if the specified player has played before.
     *
     * @param offlinePlayer The player to check.
     * @return True if the player has played before, false otherwise.
     * @throws IllegalArgumentException If the player object is not an instance of org.bukkit.OfflinePlayer or net.md_5.bungee.api.connection.ProxiedPlayer.
     */
    public static boolean hasPlayedBefore(@NotNull Object offlinePlayer) {
        if (RuntimeService.isSpigot()) {
            try {
                return (boolean) offlinePlayer.getClass().getMethod("hasPlayedBefore").invoke(offlinePlayer);
            } catch (ReflectiveOperationException ignored) {
            }
        } else if (RuntimeService.isBungeeCord()) {
            return true;
        }
        throw new IllegalArgumentException("Player object is not an instance of org.bukkit.OfflinePlayer or net.md_5.bungee.api.connection.ProxiedPlayer");
    }

    /**
     * Retrieves the {@link org.bukkit.OfflinePlayer} object associated with the given UUID.
     *
     * @param uuid The UUID of the player.
     * @return The {@link org.bukkit.OfflinePlayer} object associated with the UUID,
     * or null if the player does not exist or the runtime environment is not Spigot.
     */
    public static @Nullable Object getOfflinePlayer(UUID uuid) {
        if (RuntimeService.isSpigot()) {
            try {
                Class<?> bukkit = Class.forName("org.bukkit.Bukkit");
                return bukkit.getMethod("getOfflinePlayer", UUID.class).invoke(null, uuid);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    /**
     * Retrieves the console sender object based on the runtime environment.
     *
     * @return The console sender object.
     * @throws RuntimeException If the Spigot or BungeeCord library cannot be found.
     */
    public static Object getConsole() {
        if (RuntimeService.isSpigot()) {
            try {
                Class<?> bukkit = Class.forName("org.bukkit.Bukkit");
                return bukkit.getMethod("getConsoleSender").invoke(null);
            } catch (ReflectiveOperationException ignored) {
            }
        } else if (RuntimeService.isBungeeCord()) {
            try {
                Class<?> proxyServer = Class.forName("net.md_5.bungee.api.ProxyServer");
                Object instance = proxyServer.getMethod("getInstance").invoke(null);
                return instance.getClass().getMethod("getConsole").invoke(instance);
            } catch (ReflectiveOperationException ignored) {
            }
        }

        throw new RuntimeException("Failed to find the Spigot or BungeeCord library to get an CommandSender instance.");
    }

    /**
     * Retrieves the console sender object for executing commands from the console.
     *
     * @return The console sender object.
     * @throws IllegalArgumentException if the console object is not an instance of org.bukkit.command.CommandSender or net.md_5.bungee.api.CommandSender.
     */
    public static @NotNull BaseCommandSender getConsoleSender() {
        Object console = getConsole();
        if (RuntimeService.isSpigot() && console.getClass().getName().equals("org.bukkit.command.CommandSender")) {
            try {
                cachedSender = (BaseCommandSender) Class.forName("me.github.simonplays15.betterbansystem.core.player.SpigotCommandSender").getConstructor(console.getClass()).newInstance((console));
            } catch (ReflectiveOperationException ignored) {
            }
        } else if (RuntimeService.isBungeeCord() && console.getClass().getName().equals("net.md_5.bungee.api.CommandSender")) {
            try {
                cachedSender = (BaseCommandSender) Class.forName("me.github.simonplays15.betterbansystem.core.player.BungeeCordCommandSender").getConstructor(console.getClass()).newInstance(console);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        if (cachedSender == null)
            throw new IllegalArgumentException("Console object is not an instance of org.bukkit.command.CommandSender or net.md_5.bungee.api.CommandSender");

        return cachedSender;
    }

    /**
     * Dispatches a command to be executed.
     *
     * @param command The command to be executed.
     */
    public abstract void dispatchCommand(String command);

    /**
     * Retrieves the currently running plugin.
     *
     * @return The running plugin object.
     */
    public abstract Object getRunningPlugin();

    /**
     * Retrieves the database object associated with the BetterBanSystem instance.
     *
     * @return The database object.
     */
    public IDatabase getDatabase() {
        return database;
    }

    /**
     * Retrieves the command handler of the BetterBanSystem instance.
     *
     * @return The command handler used by BetterBanSystem.
     */
    public BaseCommandHandler getCommandHandler() {
        return commandHandler;
    }

    /**
     * Retrieves the prefix of the BetterBanSystem instance.
     *
     * @return the prefix if it is not null and not empty, otherwise returns a default prefix
     */
    public String getPrefix() {
        if (this.prefix == null) {
            return "§6[§cBetterBanSystem§6]§r";
        }
        return prefix + "§r";
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
            return;
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
