package me.github.simonplays15.betterbansystem.core.database.mysql;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.core.database.Database;
import me.github.simonplays15.betterbansystem.core.logging.GlobalLogger;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MySQLDatabase is a class that extends the abstract class Database and provides methods for managing an MySQL database.
 */
public class MySQLDatabase extends Database {

    /**
     * The Connection variable represents the connection to a database.
     */
    private Connection connection;

    /**
     * Creates the database and tables required for the BetterBanSystem.
     */
    @Override
    public void createDatabaseAndTables() {
        Statement statement = null;
        try {
            String createDbQuery = "CREATE DATABASE IF NOT EXISTS betterbansystem";
            String createBannedPlayersTable = "CREATE TABLE IF NOT EXISTS bannedplayers (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "name TEXT," +
                    "source TEXT," +
                    "created TEXT," +
                    "expires TEXT," +
                    "reason TEXT" +
                    ");";
            String createBannedIpsTable = "CREATE TABLE IF NOT EXISTS bannedips (" +
                    "ip VARCHAR(15) PRIMARY KEY," +
                    "source TEXT," +
                    "created TEXT," + // For storing date as TEXT
                    "expires TEXT," +
                    "reason TEXT" +
                    ");";
            String createWarnedPlayersTable = "CREATE TABLE IF NOT EXISTS warnedplayers (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "name TEXT," +
                    ");";
            String createWarnsTable = "CREATE TABLE IF NOT EXISTS warns (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "source TEXT," +
                    "created TEXT," +
                    "reason TEXT," +
                    "uuid VARCHAR(36), FOREIGN KEY (uuid) REFERENCES warnedplayers(uuid)" +
                    ");";
            String createMutedPlayersTable = "CREATE TABLE IF NOT EXISTS mutedplayers (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "name TEXT," +
                    "source TEXT," +
                    "created TEXT," +
                    "expires TEXT," +
                    "reason TEXT" +
                    ");";

            // Execute table create queries
            statement = connection.createStatement();
            statement.executeUpdate(createDbQuery);
            statement.executeUpdate(createBannedPlayersTable);
            statement.executeUpdate(createBannedIpsTable);
            statement.executeUpdate(createWarnedPlayersTable);
            statement.executeUpdate(createWarnsTable);
            statement.executeUpdate(createMutedPlayersTable);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error(ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Establishes a connection to the database using the provided connection string, username, and password.
     *
     * @param connectionstring the connection string used to connect to the database
     * @param username         the username for authentication
     * @param password         the password for authentication
     */
    @Override
    public void connect(String connectionstring, String username, String password) {
        try {
            String url = "jdbc:mysql://" + connectionstring;
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            GlobalLogger.getLogger().error("An error occurred while connecting to database", ex);
        }
    }

    /**
     * Disconnects from the database.
     * <p>
     * This method closes the active connection to the database, if it is not already closed.
     * </p>
     * <p>
     * If an error occurs while closing the connection, an error message is logged using the global logger.
     * </p>
     */
    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to close the database connection", ex);
        }
    }

    /**
     * Inserts a new row into the specified table with the given data.
     *
     * @param tableName the name of the table to insert into
     * @param data      a Map containing the column names and corresponding values for the new row
     * @throws NullPointerException if tableName or data is null
     */
    public void insert(String tableName, @NotNull Map<String, Object> data) {
        StringBuilder coloumns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            coloumns.append(entry.getKey()).append(", ");
            values.append("?, ");
        }

        coloumns.delete(coloumns.length() - 2, coloumns.length());
        values.delete(values.length() - 2, values.length());

        String sql = "INSERT INTO %s (%s) VALUES (%s)".formatted(tableName, coloumns, values);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (Object value : data.values()) {
                statement.setObject(index++, value);
            }

            statement.executeUpdate();
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to execute SQLStatement:", sql, ex);
        }
    }

    /**
     * Updates a record in the specified table with the new data provided.
     *
     * @param tableName       the name of the table to update
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key of the record to update
     * @param newData         a map of column names and their corresponding new values
     */
    @Override
    public void update(String tableName, String primaryKey, Object primaryKeyValue, @NotNull Map<String, Object> newData) {
        StringBuilder setClause = new StringBuilder();

        for (Map.Entry<String, Object> entry : newData.entrySet()) {
            setClause.append(entry.getKey()).append(" = ?, ");
        }

        setClause.delete(setClause.length() - 2, setClause.length());
        String sql = "UPDATE %s SET %s WHERE %s = ?".formatted(tableName, setClause, primaryKey);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 2;
            for (Object value : newData.values()) {
                statement.setObject(index++, value);
            }

            statement.setObject(index, primaryKeyValue);

            statement.executeUpdate();
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to execute SQLStatement:", sql, ex);
        }

    }

    /**
     * Deletes a record from the specified table based on the given primary key and primary key value.
     *
     * @param tableName       the name of the table from which to delete the record
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key of the record to be deleted
     */
    @Override
    public void delete(String tableName, String primaryKey, Object primaryKeyValue) {
        String sql = "DELETE FROM %s WHERE %s = ?".formatted(tableName, primaryKey);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, primaryKeyValue);

            statement.executeUpdate();
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to execute SQLStatement:", sql, ex);
        }
    }

    /**
     * Executes a SELECT statement on the specified table with a given condition and returns the result as a list of maps.
     *
     * @param tableName the name of the table to select from
     * @param condition the condition to apply in the WHERE clause
     * @return a list of maps representing the rows selected from the table,
     * where each map contains column names as keys and corresponding values as values
     */
    @Override
    public List<Map<String, Object>> select(String tableName, String condition) {
        List<Map<String, Object>> result = new ArrayList<>();

        String sql = "SELECT * FROM %s WHERE %s".formatted(tableName, condition);

        try (Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery(sql);
            ResultSetMetaData metaData = set.getMetaData();
            int c = metaData.getColumnCount();
            while (set.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= c; i++) {
                    String a = metaData.getColumnName(i);
                    Object b = set.getObject(i);
                    row.put(a, b);
                }
                result.add(row);
            }

        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to execute SQLStatement:", sql, ex);
        }

        return result;
    }

    /**
     * Retrieves all rows from the specified table in the database.
     *
     * @param tableName the name of the table from which to retrieve the rows
     * @return a list of maps representing the rows in the table, where each map contains the column name as the key
     * and the corresponding value of the column as the value
     */
    @Override
    public List<Map<String, Object>> selectAll(String tableName) {
        List<Map<String, Object>> result = new ArrayList<>();

        String sql = "SELECT * FROM %s".formatted(tableName);

        try (Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery(sql);
            ResultSetMetaData metaData = set.getMetaData();
            int c = metaData.getColumnCount();
            while (set.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= c; i++) {
                    String a = metaData.getColumnName(i);
                    Object b = set.getObject(i);
                    row.put(a, b);
                }
                result.add(row);
            }

        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to execute SQLStatement:", sql, ex);
        }

        return result;
    }

    /**
     * Executes a query and returns the result as a list of maps.
     * Each map represents a row in the query result, where the keys are the column names
     * and the values are the corresponding column values.
     *
     * @param queryString the SQL query to be executed
     * @return a list of maps, where each map represents a row in the query result
     */
    @Override
    public List<Map<String, Object>> executeQuery(String queryString) {
        List<Map<String, Object>> result = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(queryString);
             ResultSet resultSet = statement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int c = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i <= c; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                result.add(row);
            }
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to execute SQLStatement:", queryString, ex);
        }

        return result;
    }

    /**
     * Executes the given query string.
     *
     * @param queryString the query string to execute
     */
    @Override
    public void query(String queryString) {
        this.executeQuery(queryString); // didn't want to completly new write the code lol
    }

    /**
     * Creates an index on a specified field in a collection.
     *
     * @param collectionName The name of the collection.
     * @param fieldName      The name of the field to create the index on.
     * @param unique         True if the index should enforce uniqueness, false otherwise.
     */
    @Override
    public void createIndex(String collectionName, String fieldName, boolean unique) {
        String indexType = unique ? "UNIQUE" : "";
        String indexName = "idx_%s_%s".formatted(collectionName, fieldName);
        String sql = "CREATE %s INDEX %s ON %s (%s)".formatted(indexType, indexName, collectionName, fieldName);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to execute SQLStatement:", sql, ex);
        }
    }

    /**
     * Starts a transaction by disabling the auto-commit mode of the database connection.
     * Once the transaction is started, all database operations until the transaction is committed or rolled back
     * will be treated as a single atomic unit of work.
     * If an exception occurs while starting the transaction, an error message will be logged.
     *
     * @throws SQLException if an error occurs while disabling the auto-commit mode of the database connection
     */
    @Override
    public void startTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to start transaction", ex);
        }
    }

    /**
     * Commits the current transaction by calling the 'commit()' method on the database connection.
     * Sets auto-commit mode to true after the commit operation is successful.
     * If an SQLException occurs during the commit operation, it is logged as an error.
     */
    @Override
    public void commitTransaction() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to commit transaction", ex);
        }
    }

    /**
     * Rolls back the current transaction and sets auto-commit mode to true.
     * If an SQLException is caught during the rollback, an error message will be logged.
     */
    @Override
    public void rollbackTransaction() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to rollback transaction", ex);
        }
    }
}
