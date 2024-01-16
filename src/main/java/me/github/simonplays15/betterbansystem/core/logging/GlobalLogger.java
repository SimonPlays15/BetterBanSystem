package me.github.simonplays15.betterbansystem.core.logging;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.chat.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class GlobalLogger extends Logger {
    /**
     * The LOG_FOLDER variable represents the directory location where the log files are stored.
     * It is a private, static, and final string variable whose value cannot be modified once set.
     * <p>
     * The log folder path is set to "plugins/BetterBanSystem/logs/".
     * This is the default location where the log files for the BetterBanSystem plugin are stored.
     * It is recommended to keep the log files organized and easily accessible in this folder.
     * <p>
     * Note that the value of LOG_FOLDER should not be modified without careful consideration,
     * as it may result in incorrect file paths and potential errors in the functionality of the plugin.
     *
     * @since 1.0
     */
    private static final String LOG_FOLDER = "plugins/BetterBanSystem/logs/";
    /**
     * Represents a global logger instance.
     * <p>
     * This class provides a singleton instance of the logger, allowing for a single logger
     * instance to be accessed and used throughout the application.
     * <p>
     * The logger instance is private and can only be accessed within the class itself.
     * <p>
     * Usage:
     * GlobalLogger logger = GlobalLogger.getInstance();
     * logger.log("This is a log message.");
     *
     * @see Logger
     */
    private static GlobalLogger instance;
    /**
     * Represents the debugging mode of the software.
     * If it set to true, every debugging message is printed to the console.
     */
    private boolean debug;
    /**
     * Determines whether logs should be written to a file.
     * Default value is false.
     */
    private boolean writeLogsToFile;
    /**
     * Holds the reference to the private FileHandler object.
     * <p>
     * The fileHandler variable is used to manage file access and perform operations such as reading, writing, and manipulating files.
     * It provides a convenient way to interact with files in the system.
     * <p>
     * This variable is private to ensure encapsulation and to maintain data integrity.
     * It should only be accessed and modified through the provided getter and setter methods.
     * <p>
     * Usage example:
     * <p>
     * // Create a new FileHandler instance using a specified file path
     * FileHandler = new FileHandler("path/to/file.txt");
     * <p>
     * // Perform file operations using the fileHandler object
     * fileHandler.read(); // Read the content of the file
     * fileHandler.write("Hello, World!"); // Write content to the file
     * fileHandler.append("Goodbye!"); // Append content to the file
     * <p>
     * This documentation provides a general understanding of the purpose and usage of the fileHandler variable,
     * which helps developers effectively utilize it in their code.
     */
    private FileHandler fileHandler;
    /**
     * Private variable representing the current date.
     */
    private Date currentDate;


    /**
     * A global logger class for logging messages to the console or to log files.
     *
     * @param loggerName The name of the logger.
     */
    public GlobalLogger(String loggerName) {
        this(loggerName, false, false);
    }

    /**
     * Initializes the GlobalLogger with the specified logger name,
     * debug mode, and whether log files should be written.
     *
     * @param loggerName      the name of the logger
     * @param debug           whether debug mode is enabled
     * @param writeLogsToFile whether log files should be written
     */
    public GlobalLogger(String loggerName, boolean debug, boolean writeLogsToFile) {
        super(loggerName, null);
        instance = this;
        this.currentDate = new Date();
        this.writeLogsToFile = writeLogsToFile;
        this.debug = debug;
        setLevel(Level.ALL);
        setUseParentHandlers(true);
        setParent(Logger.getGlobal());

        if (writeLogsToFile) {
            createLogFolder();
            try {
                configureFileHandler();
            } catch (IOException e) {
                this.log(Level.SEVERE, "Error setting up fileHandler", e);
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroyLogger));
    }

    /**
     * Get the logger instance for BetterBanSystem.
     *
     * @return The GlobalLogger instance.
     */
    @Contract(" -> new")
    public static @NotNull GlobalLogger getLogger() {
        return instance == null ? new GlobalLogger("BetterBanSystem", false, false) : instance;
    }

    /**
     * Retrieves the GlobalLogger instance with the provided name, creating it if it doesn't exist.
     *
     * @param name The name of the logger.
     * @return The GlobalLogger instance.
     */
    @Contract("_ -> new")
    public static @NotNull GlobalLogger getLogger(String name) {
        return instance == null ? new GlobalLogger(name, false, false) : instance;
    }

    /**
     * Creates a named logger.
     *
     * @param name the name of the logger
     * @return a new instance of GlobalLogger if an instance does not exist, otherwise returns the existing instance
     */
    @Contract("_ -> new")
    public static @NotNull GlobalLogger createNamedLogger(String name) {
        return instance == null ? new GlobalLogger(name) : instance;
    }

    /**
     *
     */
    @Contract("_, _, _ -> new")
    public static @NotNull GlobalLogger createNamedLogger(String name, boolean debug, boolean writeLogsToFile) {
        return instance == null ? new GlobalLogger(name, debug, writeLogsToFile) : instance;
    }

    /**
     *
     */
    private void createLogFolder() {
        Path logFolderPath = Paths.get(LOG_FOLDER);
        if (!Files.exists(logFolderPath)) {
            try {
                Files.createDirectories(logFolderPath);
            } catch (IOException ex) {
                this.log(LogLevel.ERROR, "Failed to create logFolder:", LOG_FOLDER, ex);
            }
        }
    }

    /**
     * Configures the file handler for logging.
     *
     * @throws IOException if an error occurs while creating the file handler
     * @see FileHandler#FileHandler(String, boolean)
     */
    private void configureFileHandler() throws IOException {
        if (!this.writeLogsToFile)
            return;
        if (new Date().equals(this.currentDate)) {
            return;
        } else {
            if (this.fileHandler != null) {
                this.fileHandler.close();
                this.fileHandler = null;
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.currentDate = new Date();
        String toDayString = dateFormat.format(this.currentDate);
        String currentLogFileName = LOG_FOLDER + toDayString + "-%g.log";
        fileHandler = new FileHandler(currentLogFileName, true);
        fileHandler.setFormatter(new LogFormatter());
        fileHandler.setLevel(Level.ALL);
        this.addHandler(fileHandler);
    }

    /**
     * Destroys the logger instance.
     * If the logger instance is already null, the method does nothing.
     * Otherwise,
     * it sets the logger instance to null and logs a debug message
     * indicating that the logger has been destroyed.
     * It also closes all the associated log handlers.
     */
    public void destroyLogger() {
        if (instance == null)
            return;
        instance = null;
        this.log(LogLevel.DEBUG, "Logger destroyed");
        for (Handler handler : this.getHandlers()) {
            handler.close();
        }
    }

    /**
     * Logs a fatal level message with the specified arguments.
     *
     * @param args the arguments to be logged
     */
    public void fatal(Object... args) {
        this.log(LogLevel.FATAL, args);
    }

    /**
     * Logs the given information with LogLevel.INFO.
     *
     * @param args The objects to be logged.
     */
    public void info(Object... args) {
        this.log(LogLevel.INFO, args);
    }

    /**
     * Logs an information message.
     *
     * @param msg The message to log.
     */
    @Override
    public void info(String msg) {
        this.log(LogLevel.INFO, msg);
    }

    /**
     * Logs a trace-level message.
     *
     * @param args the objects to be logged
     */
    public void trace(Object... args) {
        this.log(LogLevel.TRACE, args);
    }

    /**
     *
     */
    public void debug(Object... args) {
        if (this.debug)
            this.log(LogLevel.DEBUG, args);
    }

    /**
     * Logs an error message with the provided arguments.
     *
     * @param args The arguments to be logged.
     */
    public void error(Object... args) {
        this.log(LogLevel.ERROR, args);
    }

    /**
     * Adds a log record of warning level to the logger.
     *
     * @param args the objects to be logged as warning messages
     */
    public void warn(Object... args) {
        this.log(LogLevel.WARNING, args);
    }

    /**
     * Displays a warning message.
     *
     * @param string the warning message to be displayed
     */
    @Override
    public void warning(String string) {
        this.warn(string);
    }

    /**
     * Logs a message with the given log level and arguments.
     *
     * @param level the log level to use, must not be null
     * @param args  the arguments to log, must not be null
     */
    public void log(@NotNull LogLevel level, Object @NotNull ... args) {
        StringBuilder message = new StringBuilder();
        message.append("[").append(this.getName()).append("]").append(" ").append("[").append(level.name()).append("]").append(" ");
        for (Object arg : args) {
            if (arg instanceof Throwable) {
                message.append("\n").append(this.buildStackTrace((Throwable) arg));
                continue;
            }
            message.append(arg).append(" ");
        }
        LogRecord record = new LogRecord(level.realLevel, message.toString());
        record.setLoggerName(this.getName());
        this.log(record);
        // TODO check if database logging is enabled

        try {
            configureFileHandler();
        } catch (IOException e) {
            System.err.println("[BetterBanSystem] Failed to create the FileHandler\n" + e);
        }
    }

    /**
     *
     */
    @Contract(pure = true)
    private @NotNull String buildStackTrace(Throwable throwable) {
        if (throwable == null)
            return "";
        StringBuilder builder = new StringBuilder();

        builder.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");

        for (StackTraceElement elem : throwable.getStackTrace()) {
            builder.append("\tat ").append(elem.toString()).append("\n");
        }

        String result = builder.toString();
        if (result.endsWith("\n")) {
            builder.replace(builder.length() - 2, builder.length(), "");
        }
        return builder.toString();

    }

    /**
     * Determines if the application is currently running in debug mode.
     *
     * @return true if the application is running in debug mode, false otherwise
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets the debug mode for the application.
     *
     * @param debug the boolean value to indicate whether to enable debug mode or not
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Determines whether to write log files.
     *
     * @return True if log files should be written, otherwise False.
     */
    public boolean writeLogFiles() {
        return writeLogsToFile;
    }

    /**
     * Sets the flag for writing logs to a file.
     *
     * @param writeLogsToFile if true, logs will be written to a file;
     *                        if false, logs will not be written to a file.
     */
    public void setWriteLogsToFile(boolean writeLogsToFile) {
        this.writeLogsToFile = writeLogsToFile;
        if (writeLogsToFile) {
            createLogFolder();
            try {
                configureFileHandler();
            } catch (IOException e) {
                log(Level.SEVERE, "Error setting up fileHandler", e);
            }
        }
    }

    private static class LogFormatter extends Formatter {
        @Override
        public String format(@NotNull LogRecord record) {
            return String.format("%s %s%n", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()), ChatColor.stripColor(record.getMessage()));
        }
    }
}
