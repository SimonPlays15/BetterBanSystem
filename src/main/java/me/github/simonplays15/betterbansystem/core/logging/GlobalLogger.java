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

/**
 * The GlobalLogger class provides a global logger instance that can be used to log messages to the console or to log files.
 * It extends the Logger class.
 */
public class GlobalLogger extends Logger {
    /**
     * The LOG_FOLDER variable represents the folder path for the log files.
     * It is a private static final String and its value is "plugins/BetterBanSystem/logs/".
     */
    private static final String LOG_FOLDER = "plugins/BetterBanSystem/logs/";
    /**
     *
     */
    private static GlobalLogger instance;
    /**
     *
     */
    private boolean debug;
    /**
     *
     */
    private boolean writeLogsToFile;
    /**
     * This class represents a FileHandler for writing log messages to a file.
     * It is used in the GlobalLogger class to handle file logging if enabled.
     */
    private FileHandler fileHandler;
    /**
     * The currentDate variable represents the current date and time when the GlobalLogger object is created.
     * It is used to track the date changes for logging purposes.
     */
    private Date currentDate;


    /**
     * GlobalLogger class is responsible for creating and configuring a global logger instance.
     * It provides logging functionality at different log levels and supports logging to both console and file.
     * <p>
     * Usage:
     * GlobalLogger logger = new GlobalLogger(loggerName, debug, writeLogsToFile);
     * <p>
     * Parameters:
     * - loggerName: The name of the logger.
     * - debug: Flag to enable/disable debug mode. Default is false.
     * - writeLogsToFile: Flag to enable/disable writing logs to file. Default is false.
     * <p>
     * Example usage:
     * GlobalLogger logger = new GlobalLogger("MyLogger", true, true);
     * logger.info("This is an information message");
     * logger.error("This is an error message");
     * <p>
     * Note: The class inherits from java.util.logging.Logger class.
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
     * Creates a named logger instance or returns the existing global logger instance.
     *
     * @param name            the name of the logger
     * @param debug           a boolean indicating if debug mode is enabled
     * @param writeLogsToFile a boolean indicating if logs should be written to a file
     * @return the global logger instance
     */
    @Contract("_, _, _ -> new")
    public static @NotNull GlobalLogger createNamedLogger(String name, boolean debug, boolean writeLogsToFile) {
        return instance == null ? new GlobalLogger(name, debug, writeLogsToFile) : instance;
    }

    /**
     * Creates a log folder if it does not already exist.
     * <p>
     * This method is responsible for creating the log folder at the specified location.
     *
     * @throws IOException If an I/O error occurs while creating the log folder.
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
     * Configures the file handler for writing logs to a file.
     *
     * @throws IOException if an I/O error occurs while configuring the file handler
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
     * Destroys the logger, closing all handlers and cleaning up resources.
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
     * Logs a message at the FATAL level.
     *
     * @param args the objects to include in the log message
     */
    public void fatal(Object... args) {
        this.log(LogLevel.FATAL, args);
    }

    /**
     * Logs an informational message.
     *
     * @param args the message arguments
     */
    public void info(Object... args) {
        this.log(LogLevel.INFO, args);
    }

    /**
     * Logs an informational message with the given message.
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
     * Debug method.
     *
     * @param args The objects to be logged. Can be multiple.
     */
    public void debug(Object... args) {
        if (this.debug)
            this.log(LogLevel.DEBUG, args);
    }

    /**
     * Logs an error message.
     *
     * @param args the error message arguments
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
     * Logs a warning message.
     *
     * @param string the warning message to be logged
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
     * Builds a stack trace string.
     *
     * @param throwable the {@code Throwable} to build the stack trace from
     * @return a {@code String} representing the stack trace
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
     * Sets the debug mode for the GlobalLogger.
     *
     * @param debug The boolean value indicating whether debug mode should be enabled or not.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Determines whether to write log files.
     *
     * @return {@code true} if log files should be written, otherwise {@code false}.
     */
    public boolean writeLogFiles() {
        return writeLogsToFile;
    }

    /**
     * Sets the flag for writing logs to a file.
     *
     * @param writeLogsToFile if true, logs will be written to a file; if false, logs will not be written to a file.
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

    /**
     * The `LogFormatter` class is a custom formatter used to format log records in a specific way. It extends the `Formatter` class provided by the Java logging framework. This class
     * overrides the `format` method to define the formatting logic for log records.
     * <p>
     * The `LogFormatter` class formats the log record by concatenating the log message with the current date and time in the format "dd.MM.yyyy HH:mm:ss". It also strips any color
     * formatting from the log message using the `ChatColor.stripColor` method provided by the `ChatColor` class.
     * <p>
     * Example usage:
     * <p>
     * ```
     * Handler = new ConsoleHandler();
     * handler.setFormatter(new LogFormatter());
     * logger.addHandler(handler);
     * ```
     *
     * @see Formatter
     * @see LogRecord
     */
    private static class LogFormatter extends Formatter {
        /**
         * Formats a log record by concatenating the log message with the current date and time in the format "dd.MM.yyyy HH:mm:ss". It also strips any color formatting from the log
         * message.
         *
         * @param record the log record to format
         * @return the formatted log record
         */
        @Override
        public String format(@NotNull LogRecord record) {
            return String.format("%s %s%n", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()), ChatColor.stripColor(record.getMessage()));
        }
    }
}
