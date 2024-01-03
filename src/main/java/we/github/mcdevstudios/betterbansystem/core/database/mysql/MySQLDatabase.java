/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.database.mysql;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.database.Database;
import we.github.mcdevstudios.betterbansystem.core.logging.GlobalLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLDatabase extends Database {

    private Connection connection;

    /**
     * Creates the database and necessary tables for the BetterBanSystem.
     * This method will execute the necessary SQL queries to create the database and tables if they do not already exist.
     * <p>
     * The database will be named "betterbansystem".
     * <p>
     * The tables that will be created include:
     * - bannedplayers: Stores information about banned players
     * - bannedips: Stores information about banned IPs
     * - warnedplayers: Stores information about warned players
     * - warns: Stores information about warns issued to players
     * - mutedplayers: Stores information about muted players
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
     * Connects to the database using the given connection string, username, and password.
     *
     * @param connectionstring the connection string for the database
     * @param username         the username for the database connection
     * @param password         the password for the database connection
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
     * Closes the database connection if it is open.
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
     * Inserts data into the specified table.
     *
     * @param tableName the name of the table
     * @param data      a map containing the column names and their corresponding values to be inserted
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
     * Updates a record in the specified table with the given primary key and new data.
     *
     * @param tableName       the name of the table to update
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key for the record to update
     * @param newData         a map of column names to new values for the record
     * @throws SQLException if a database error occurs
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
     * Deletes a record from the specified table based on the primary key and its value.
     *
     * @param tableName       the name of the table
     * @param primaryKey      the primary key column name
     * @param primaryKeyValue the value of the primary key to match for deletion
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
     * Executes a SELECT query on a specified table with a provided condition.
     *
     * @param tableName the name of the table to select from
     * @param condition the condition to apply in the WHERE clause
     * @return a List of Map objects where each Map represents a row in the result set,
     * with column names as keys and column values as values
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
     * @param tableName the name of the table to select from
     * @return a list of maps containing the rows from the table
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
     * Executes a SQL query and returns the result as a list of maps.
     * Each map represents a row from the query result with column names as the keys and column values as the values.
     *
     * @param queryString the SQL query string to be executed
     * @return a list of maps representing the result of the query
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
     * @param collectionName the name of the collection on which to create the index
     * @param fieldName      the name of the field on which to create the index
     * @param unique         {@code true} if the index should be unique, {@code false} otherwise
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
     * Starts a transaction by disabling auto-commit mode for the connection.
     * If an SQLException occurs, it will be logged using the GlobalLogger.
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
     * Commits the current transaction and sets the auto-commit mode of the connection to true.
     * If an SQLException occurs during the commit, it is logged as an error.
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
     * Rollbacks the current transaction and sets the auto-commit mode to true.
     * If an exception occurs during the rollback, it will be logged using the GlobalLogger.
     * This method does not return any value.
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
