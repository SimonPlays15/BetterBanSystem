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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents the BetterBanSystem class.
 */
public abstract class BetterBanSystem {

    /**
     * The BetterBanSystem class represents a singleton instance of the BetterBanSystem system.
     * It provides methods for sending messages, getting players, managing permissions, loading configurations,
     * and interacting with the database.
     */
    private static BetterBanSystem instance;
    /**
     * Represents a cached instance of a command sender.
     * It is used to store a reference to a command sender object for efficient access.
     * The reference is volatile to ensure atomicity and visibility across multiple threads.
     */
    private static volatile BaseCommandSender cachedSender = null;
    /**
     * Represents the folder where the data for the BetterBanSystem plugin is stored.
     */
    private final File dataFolder;
    /**
     * The configFile variable represents the file object that holds the configuration data for the BetterBanSystem.
     */
    private final File configFile;
    /**
     * Represents the description of the base plugin.
     */
    private final BasePluginDescription basePluginDescription;
    /**
     * The commandHandler variable is a private final member of the BetterBanSystem class.
     * It is an instance of the BaseCommandHandler class, which is responsible for handling commands in a command-based system.
     * <p>
     * The commandHandler variable is initialized in the constructor of the BetterBanSystem class.
     * During initialization, several example commands are registered using the registerCommand() method of the BaseCommandHandler class.
     * <p>
     * Example usage:
     * <p>
     * // Create an instance of BetterBanSystem
     * BetterBanSystem betterBanSystem = new BetterBanSystem(dataFolder);
     * <p>
     * // Get the command handler
     * BaseCommandHandler commandHandler = betterBanSystem.getCommandHandler();
     * <p>
     * // Get all registered commands
     * Map<String, BaseCommand> commands = commandHandler.getCommands();
     * <p>
     * // Retrieve a specific command by its name
     * BaseCommand command = commands.get("commandName");
     * <p>
     * Note: This documentation only covers the purpose and usage of the commandHandler variable and does not provide example code.
     */
    private final BaseCommandHandler commandHandler;
    /**
     * The prefix variable stores a string that represents a prefix for messages or commands.
     */
    public String prefix;
    /**
     * Represents the configuration settings for the BetterBanSystem.
     */
    public BaseConfig config;
    /**
     * The LanguageFile class represents a language file that extends the BaseConfig class.
     * It provides methods to load and retrieve messages from the language file.
     */
    private LanguageFile languageFile;
    /**
     * The PermissionsManager variable is an instance of the PermissionsManager class.
     * <p>
     * It is a private field in the BetterBanSystem class.
     * <p>
     * The PermissionsManager class is responsible for managing permissions within the BetterBanSystem.
     * It provides methods for checking permissions, assigning permissions to players, and handling permission-related operations.
     * <p>
     * To access the PermissionsManager object, use the getter method 'getPermissionsManager()' of the BetterBanSystem class.
     * Examples:
     * PermissionsManager = betterBanSystem.getPermissionsManager();
     * <p>
     * Note that this variable is declared as private, which means it can only be accessed within the BetterBanSystem class.
     **/
    private PermissionsManager manager;
    /**
     * Represents a database and provides methods to interact with it.
     * Use this variable to perform various database operations such as creating, connecting, disconnecting, inserting, updating,
     * deleting, and selecting data. It also supports executing custom SQL queries, creating indexes, and managing transactions.
     * You can access the methods and functionality of the database through this variable.
     * <p>
     * Example usage:
     * <pre>{@code
     *     // Create an instance of IDatabase
     *     IDatabase database = new Database();
     *
     *     // Connect to the database
     *     database.connect("connectionstring", "username", "password");
     *
     *     // Insert data into a table
     *     Map<String, Object> data = new HashMap<>();
     *     data.put("column1", value1);
     *     data.put("column2", value2);
     *     database.insert("tableName", data);
     *
     *     // Update a record in a table
     *     Map<String, Object> newData = new HashMap<>();
     *     newData.put("column1", newValue1);
     *     newData.put("column2", newValue2);
     *     database.update("tableName", "primaryKey", primaryKeyValue, newData);
     *
     *     // Delete a record from a table
     *     database.delete("tableName", "primaryKey", primaryKeyValue);
     *
     *     // Execute a SELECT query
     *     List<Map<String, Object>> result = database.select("tableName", "condition");
     *
     *     // Execute a custom SQL query
     *     List<Map<String, Object>> queryResult = database.executeQuery("SELECT * FROM tableName WHERE condition");
     *
     *     // Create an index on a field
     *     database.createIndex("collectionName", "fieldName", true);
     *
     *     // Start a transaction
     *     database.startTransaction();
     *     try {
     *         // Perform database operations within the transaction
     *         database.insert("tableName", data);
     *         database.update("tableName", "primaryKey", primaryKeyValue, newData);
     *         database.delete("tableName", "primaryKey", primaryKeyValue);
     *         // Commit the transaction
     *         database.commitTransaction();
     *     } catch (Exception e) {
     *         // Handle exception
     *         // Roll back the transaction
     *         database.rollbackTransaction();
     *     }
     *
     *     // Disconnect from the database
     *     database.disconnect();
     * }</pre>
     */
    private IDatabase database;

    /**
     * Initializes the BetterBanSystem.
     *
     * @param dataFolder The data folder for storing configuration and data files.
     * @throws RuntimeException if the runtime service is not Spigot or BungeeCord.
     */
    public BetterBanSystem(File dataFolder) throws RuntimeException {
        instance = this;
        this.dataFolder = dataFolder;
        if (!this.dataFolder.exists())
            this.dataFolder.mkdirs();
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

        new Updater().getVersion(version -> {
            if (!this.getPluginDescription().getVersion().equals(version))
                CompletableFuture.delayedExecutor(4, TimeUnit.SECONDS).execute(() -> {
                    GlobalLogger.getLogger().info("A new update is available for BetterBanSystem: v" + version);
                });
        });
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
     * @param player  the player object to send the message to
     * @param message the message to send
     * @throws IllegalArgumentException if the player object is not an instance of org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer
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
     * Sends one or more messages to the specified player.
     *
     * @param player   The player object to send the messages to. Must not be null.
     * @param messages The messages to be sent. Can be one or more strings.
     */
    public static void sendMessage(Object player, String @NotNull ... messages) {
        for (String message : messages) {
            sendMessage(player, message);
        }
    }

    /**
     * Sends a message to a player or console.
     *
     * @param player    the player or console object to send the message to
     * @param component the text component to send
     * @throws IllegalArgumentException if player object is not an instance of org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer
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
     * Sends a message or multiple messages to a player.
     *
     * @param player     the player or command sender to send the message(s) to
     * @param components the text components representing the message(s) to be sent
     */
    public static void sendMessage(Object player, @NotNull TextComponent @NotNull ... components) {
        for (TextComponent component : components) {
            component.setText(BetterBanSystem.getInstance().getPrefix() + component.getText());
            sendMessage(player, component);
        }
    }

    /**
     * Kicks the specified player with the given message.
     *
     * @param player  The player object to kick. Must be an instance of either org.bukkit.entity.Player or net.md_5.bungee.api.connection.ProxiedPlayer.
     * @param message The message to be displayed to the kicked player.
     */
    public static void kickPlayer(@NotNull Object player, String message) {
        if (RuntimeService.isSpigot()) {
            try {
                player.getClass().getMethod("kickPlayer", String.class).invoke(player, message);
            } catch (ReflectiveOperationException ignored) {
            }
        } else if (RuntimeService.isBungeeCord()) {
            try {
                TextComponent textComponent = new TextComponent(message);
                player.getClass().getMethod("disconnect", BaseComponent.class).invoke(player, textComponent);
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }

    /**
     * Retrieves the player object based on the given player name.
     *
     * @param playerName the name of the player
     * @return the player object if found, otherwise null
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
     * Retrieves the player with the specified UUID.
     *
     * @param playerId the UUID of the player to retrieve
     * @return the player object with the specified UUID, or null if the player is not found or the runtime environment is not supported
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
     * Retrieves the OfflinePlayer object associated with the specified UUID.
     *
     * @param uuid The UUID of the player to fetch
     * @return The OfflinePlayer object for the specified UUID, or null if it cannot be found.
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
     * Retrieves the console object for the current runtime environment.
     *
     * @return The console object for the current runtime environment.
     * @throws RuntimeException if the Spigot or BungeeCord library is not found.
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
     * Retrieves the console sender for the current runtime environment.
     *
     * @return The console sender.
     * @throws IllegalArgumentException If the console object is not an instance of org.bukkit.command.CommandSender or net.md_5.bungee.api.CommandSender.
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
     * Dispatches a command for execution.
     *
     * @param command the command to be dispatched
     */
    public abstract void dispatchCommand(String command);

    /**
     * Retrieves the currently running plugin.
     *
     * @return The currently running plugin.
     */
    public abstract Object getRunningPlugin();

    /**
     * Retrieves the database associated with this BetterBanSystem instance.
     *
     * @return The IDatabase instance representing the database.
     */
    public IDatabase getDatabase() {
        return database;
    }

    /**
     * Retrieves the command handler for managing commands in the command-based system.
     *
     * @return The command handler, an instance of the BaseCommandHandler class.
     */
    public BaseCommandHandler getCommandHandler() {
        return commandHandler;
    }

    /**
     *
     */
    public String getPrefix() {
        if (this.prefix == null) {
            return "§6[§cBetterBanSystem§6]§r";
        }
        return prefix + "§r";
    }

    /**
     * Loads the specified language file.
     * If the language parameter does not end with ".yml", it will be appended with ".yml".
     * The language file will be loaded from the path specified in the data folder of the plugin.
     *
     * @param language The name of the language file to load.
     * @throws NullPointerException if the language parameter is null.
     */
    public void loadLanguage(@NotNull String language) {
        if (!language.endsWith(".yml"))
            language += ".yml";
        this.languageFile = new LanguageFile(this.getDataFolder().getPath() + "/language/" + language);
    }

    /**
     * Loads the permissions system based on the specified {@link PermissionsHandlerType}.
     * <p>
     * The permissions system is responsible for managing permissions in the system.
     * It provides functionality for creating, modifying, and deleting permissions,
     * as well as checking if a player has a specific permission.
     * <p>
     * If the specified handler cannot be obtained, the method falls back to the default
     * permissions handler based on the server runtime.
     * <p>
     * The available types of permissions handlers are defined in the {@link PermissionsHandlerType} enum.
     *
     * @param type The type of the permissions handler to load.
     *             Possible values are SPIGOT, LUCKPERMS, BUNGEECORD, CLOUDNET, VAULT, and DEFAULT_PERMISSION_HANDLING.
     * @see PermissionsHandlerType
     * @see PermissionsManager
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
     * Reloads the configuration by loading the config file into the current {@code config} object.
     * <p>
     * If the {@code config} is null, a new instance of {@link BaseConfig} is created.
     * After that, the {@link BaseConfig#load(File)} method is called to load the config file.
     */
    public void reloadConfig() {
        if (this.config == null)
            this.config = new BaseConfig();
        this.config.load(this.configFile);
    }

    /**
     * Saves the configuration to a file.
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
     * Retrieves the BaseConfig object associated with this BetterBanSystem instance.
     *
     * @return The BaseConfig object used for storing configuration settings.
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
     * Retrieves the permissions manager associated with this instance of BetterBanSystem.
     *
     * @return The permissions' manager.
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
