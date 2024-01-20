package me.github.simonplays15.betterbansystem.core.logging;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.logging.Level;

/**
 * The LogLevel enum represents different levels of logging.
 * It provides mappings to the standard log levels defined in the java.util.logging.Level class.
 */
public enum LogLevel {

    /**
     * The INFO variable is of type LogLevel and represents the logging level for information messages.
     * It has a corresponding mapping to the Level.INFO level defined in the java.util.logging.Level class.
     * <p>
     * Example usage:
     * INFO(Level.INFO)
     * <p>
     * The INFO variable should be used when logging information messages that are relevant for the normal flow of application execution.
     * Information messages typically provide insights into the current state of the application or important events.
     * These messages are less severe than warning or error messages, but still provide valuable information for debugging and monitoring purposes.
     * <p>
     * Note: The INFO variable is part of the LogLevel enum, which provides mappings to various logging levels, including INFO.
     * Other levels in the enum include DEBUG, TRACE, WARNING, FATAL, and ERROR.
     * <p>
     * For more information on the different logging levels and how to use them effectively, refer to the java.util.logging package documentation.
     */
    INFO(Level.INFO),
    /**
     * DEBUG represents the debug level for logging purposes.
     *
     * <p>
     * The DEBUG variable is used to define the debug level for logging. It helps control the amount of information
     * being logged for debugging purposes.
     *
     * <p>
     * The value of the DEBUG variable is set using the Level enum class. The Level enum represents the severity
     * levels for logging - from least severe to most severe, the levels are:
     * - TRACE
     * - DEBUG
     * - INFO
     * - WARN
     * - ERROR
     *
     * <p>
     * The default value for DEBUG is Level.INFO, which provides informational-level logging.
     * <p>
     * Example usage:
     * <pre>
     *     DEBUG(Level.DEBUG);
     * </pre>
     */
    DEBUG(Level.INFO),
    /**
     * This variable represents the tracing level to be used in the system.
     * <p>
     * The TRACE level provides detailed information about the execution flow
     * and state of the system. It can be used for debugging and troubleshooting
     * purposes.
     * <p>
     * The possible values for the TRACE level are:
     * - Level.OFF: No tracing information will be logged.
     * - Level.SEVERE: Only severe tracing information will be logged.
     * - Level.WARNING: Only warning and severe tracing information will be logged.
     * - Level.INFO: Information, warning, and severe tracing information will be logged.
     * - Level.CONFIG: Config, information, warning, and severe tracing information will be logged.
     * - Level.FINE: Fine, config, information, warning, and severe tracing information will be logged.
     * - Level.FINER: Finer, fine, config, information, warning, and severe tracing information will be logged.
     * - Level.FINEST: Finest, Finer, fine, config, information, warning, and severe tracing information will be logged.
     * - Level.ALL: All tracing information will be logged.
     * <p>
     * The default level is Level.INFO.
     * <p>
     * Example usage:
     * <p>
     * TRACE(Level.INFO)
     */
    TRACE(Level.INFO),
    /**
     * Represents a warning log level.
     *
     * <p>
     * The {@code WARNING} log level is used to indicate a potential issue or problem that
     * may require attention but does not prevent the system from functioning properly.
     * </p>
     *
     * <p>
     * The {@code WARNING} log level is mapped to the {@link Level#WARNING}
     * log level defined in the Java logging framework.
     * </p>
     *
     * @see LogLevel
     * @see Level#WARNING
     */
    WARNING(Level.WARNING),
    /**
     * The FATAL constant represents the highest level of logging.
     * It maps to the standard severe level defined in the java.util.logging.Level class.
     */
    FATAL(Level.SEVERE),
    /**
     * The ERROR variable represents the severity level for logging errors.
     */
    ERROR(Level.SEVERE);

    /**
     * The realLevel variable represents the logging level of a log message.
     * It is of type java.util.logging.Level and is used to determine the importance of a log message.
     * <p>
     * The realLevel variable is a final variable, meaning its value cannot be changed once assigned.
     * It is initialized with the corresponding java.util.logging.Level value defined in the LogLevel enum.
     * The LogLevel enum provides mappings to the standard log levels defined in the java.util.logging.Level class.
     */
    final Level realLevel;

    /**
     * Represents different levels of logging.
     * Provides mappings to the standard log levels defined in the java.util.logging.Level class.
     */
    LogLevel(Level realLevel) {
        this.realLevel = realLevel;
    }
}
