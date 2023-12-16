/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.utils;

import org.bukkit.ChatColor;
import we.github.mcdevstudios.betterbansystem.BetterBanSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class GlobalLogger extends Logger {
    private static final String LOG_FOLDER = BetterBanSystem.getInstance().getDataFolder() + "/logs/";
    private final boolean debug;
    private FileHandler fileHandler;
    private String currentLogFileName;

    /**
     * @param loggerName the Logger name
     * @apiNote {@link GlobalLogger#GlobalLogger(String, boolean, boolean)} is default loggerName, true, false
     */
    public GlobalLogger(String loggerName) {
        this(loggerName, true, false);
    }

    /**
     * @param loggerName      the Logger name
     * @param writeLogsToFile if you want to enable or disable logfile writing
     */
    public GlobalLogger(String loggerName, boolean debug, boolean writeLogsToFile) {
        super(loggerName, null);
        this.debug = debug;
        setLevel(Level.ALL);
        setUseParentHandlers(false);

        if (writeLogsToFile) {
            createLogFolder();
            try {
                configureFileHandler();
            } catch (IOException e) {
                log(Level.SEVERE, "Error setting up fileHandler", e);
            }
        }

    }

    public static GlobalLogger createNamedLogger(String name) {
        return new GlobalLogger(name);
    }

    public static GlobalLogger createNamedLogger(String name, boolean debug, boolean writeLogsToFile) {
        return new GlobalLogger(name, debug, writeLogsToFile);
    }

    private void createLogFolder() {
        Path logFolderPath = Paths.get(LOG_FOLDER);
        if (!Files.exists(logFolderPath)) {
            try {
                Files.createDirectories(logFolderPath);
            } catch (IOException ex) {
                log(Level.SEVERE, "Failed to create logFolder:", LOG_FOLDER, ex);
            }
        }
    }

    /**
     * @throws IOException if {@link FileHandler#FileHandler(String, boolean) } throws an error
     * @see FileHandler#FileHandler(String, boolean)
     */
    private void configureFileHandler() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String toDayString = dateFormat.format(new Date());
        currentLogFileName = LOG_FOLDER + toDayString + "_log.log";
        fileHandler = new FileHandler(currentLogFileName, true);
        fileHandler.setFormatter(new LogFormatter());
        addHandler(fileHandler);
    }

    /**
     * @throws IOException if fileHandler != null
     */
    private void checkAndRotateLogFile() {
        if (fileHandler == null)
            return;
        try {
            fileHandler.close();

            Path currentLogFile = Paths.get(currentLogFileName);
            long fileSize = Files.size(currentLogFile);

            if (fileSize > 10 * 1024 * 1024) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String todayString = dateFormat.format(new Date());
                int fileNumber = 1;

                while (Files.exists(Paths.get(LOG_FOLDER + todayString + "_log-" + fileNumber + ".log"))) {
                    fileNumber++;
                }
                currentLogFileName = LOG_FOLDER + todayString + "_log-" + fileNumber + ".log";
                fileHandler = new FileHandler(currentLogFileName, true);
                fileHandler.setFormatter(new LogFormatter());
                addHandler(fileHandler);
            }
        } catch (IOException ex) {
            log(Level.SEVERE, "Error rotating log file", ex);
        }
    }

    public void fatal(Object... args) {
        log(Level.SEVERE, args);
    }

    public void trace(Object... args) {
        log(Level.FINEST, args);
    }

    public void debug(Object... args) {
        if (!debug)
            return;
        log(Level.FINE, args);
    }

    public void error(Object... args) {
        log(Level.SEVERE, args);
    }

    public void warn(Object... args) {
        log(Level.WARNING, args);
    }

    public void log(Level level, Object... args) {
        if (level == Level.FINE)
            return;
        StringBuilder message = new StringBuilder();
        for (Object arg : args) {
            message.append(arg).append(" ");
        }
        LogRecord record = new LogRecord(level, message.toString());
        record.setLoggerName(getName());
        log(record);

        // TODO check if database logging is enabled
        // Database.saveLogToDatabase(level, message.toString());

        checkAndRotateLogFile();
    }

    private static class LogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("[%s] %s%n", record.getLevel(), ChatColor.stripColor(record.getMessage()));
        }
    }

}
