/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.logging;

import org.bukkit.ChatColor;
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
    private static final String LOG_FOLDER = "plugins/BetterBanSystem/logs/";
    private static GlobalLogger instance;
    private final boolean debug;
    private boolean writeLogsToFile;
    private FileHandler fileHandler;
    private Date currentDate;


    /**
     * @param loggerName the Logger name
     * @apiNote {@link GlobalLogger#GlobalLogger(String, boolean, boolean)} is default loggerName, true, false
     */
    public GlobalLogger(String loggerName) {
        this(loggerName, false, true);
    }

    /**
     * @param loggerName      the Logger name
     * @param writeLogsToFile if you want to enable or disable logfile writing
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

    }

    /**
     * Create GlobalLogger Instance with name "BetterBanSystem", debug: true, writeLogsToFile: false
     *
     * @return {@link GlobalLogger}
     */
    @Contract(" -> new")
    public static @NotNull GlobalLogger getLogger() {
        return instance == null ? new GlobalLogger("BetterBanSystem", true, true) : instance;
    }

    @Contract("_ -> new")
    public static @NotNull GlobalLogger createNamedLogger(String name) {
        return instance == null ? new GlobalLogger(name) : instance;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull GlobalLogger createNamedLogger(String name, boolean debug, boolean writeLogsToFile) {
        return instance == null ? new GlobalLogger(name, debug, writeLogsToFile) : instance;
    }

    public GlobalLogger withFileWrite() {
        this.setWriteLogsToFile(true);
        return this;
    }

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
     * @throws IOException if {@link FileHandler#FileHandler(String, boolean) } throws an error
     * @see FileHandler#FileHandler(String, boolean)
     */
    private void configureFileHandler() throws IOException {
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
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.ALL);
        this.addHandler(fileHandler);
    }

    public void destroyLogger() {
        instance = null;
        this.log(LogLevel.DEBUG, "Logger destroyed");
        for (Handler handler : this.getHandlers()) {
            handler.close();
        }
    }

    public void fatal(Object... args) {
        this.log(LogLevel.FATAL, args);
    }

    public void info(Object... args) {
        this.log(LogLevel.INFO, args);
    }

    public void info(String msg) {
        this.log(LogLevel.INFO, msg);
    }

    public void trace(Object... args) {
        this.log(LogLevel.TRACE, args);
    }

    public void debug(Object... args) {
        this.log(LogLevel.DEBUG, args);
    }

    public void error(Object... args) {
        this.log(LogLevel.ERROR, args);
    }

    public void warn(Object... args) {
        log(LogLevel.WARNING, args);
    }

    public void log(LogLevel level, Object... args) {
        if (!debug)
            return;
        StringBuilder message = new StringBuilder();
        // .append("[").append(this.getName()).append("]").append(" ").
        message.append("[").append(level.name()).append("]").append(" ");
        for (Object arg : args) {
            if (arg instanceof Throwable) {
                message.append("\n").append(this.buildStackTrace((Throwable) arg));
                continue;
            }
            message.append(arg).append(" ");
        }
        LogRecord record = new LogRecord(level.realLevel, message.toString());
        record.setLoggerName(getName());
        this.log(record);
        // TODO check if database logging is enabled
        // Database.saveLogToDatabase(level, message.toString());

        try {
            configureFileHandler();
        } catch (IOException e) {
            System.err.println("[BetterBanSystem] " + e.getCause().getClass().getName() + ": " + e.getMessage());
        }
    }

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

    public boolean isDebug() {
        return debug;
    }

    public boolean doWriteLogFiles() {
        return writeLogsToFile;
    }

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
        public String format(LogRecord record) {
            return String.format("%s [%s] [%s] %s%n", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()), record.getLoggerName(), record.getLevel(), ChatColor.stripColor(record.getMessage()));
        }
    }

}
