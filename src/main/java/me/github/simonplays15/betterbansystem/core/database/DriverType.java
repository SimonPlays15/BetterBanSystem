package me.github.simonplays15.betterbansystem.core.database;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * Enum class representing different types of database drivers.
 * The DriverType enum is used to specify the driver type when establishing a database connection.
 */
public enum DriverType {
    /**
     * Global constant representing the MYSQL driver type.
     * <p>
     * This constant is used to specify the driver type when working with a MYSQL database.
     */
    MYSQL,
    /**
     * Represents the SQLITE driver type for database connections.
     * <p>
     * This variable is used to specify the database driver type when establishing a database connection.
     * The SQLITE driver type is used for connecting to a SQLite database.
     */
    SQLITE,
    /**
     * The constant MONGODB is used to represent the MongoDB driver type.
     * <p>
     * This driver type is used for connecting and interacting with a MongoDB database.
     * <p>
     * Usage:
     * To use the MONGODB driver type, you can pass it as a parameter to functions that require a DriverType.
     * <p>
     * Example:
     * DriverType = DriverType.MONGODB;
     * // Use the driverType in your code
     *
     * @see DriverType
     */
    MONGODB
}
