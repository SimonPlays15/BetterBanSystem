package me.github.simonplays15.betterbansystem.core.database.sqlite;

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
 * The SQLiteDatabase class represents a database connection and provides methods for interacting with the database.
 * It extends the Database class and overrides several methods for executing SQL queries and transactions.
 */
public class SQLiteDatabase extends Database {

    /**
     * The connection variable represents the connection to the database.
     * It is a private instance variable of type Connection.
     * <p>
     * Class Hierarchy:
     * SQLiteDatabase -> Database -> IDatabase
     * <p>
     * Class Fields:
     * SQLiteDatabase:
     * - connection (private)
     * <p>
     * Class Methods:
     * SQLiteDatabase:
     * - connect(String connectionstring, String username, String password) : void (Override)
     * - createDatabaseAndTables() : void (Override)
     * - disconnect() : void (Override)
     * - insert(String tableName, Map<String, Object> data) : void
     * - update(String tableName, String primaryKey, Object primaryKeyValue, Map<String, Object> newData) : void (Override)
     * - delete(String tableName, String primaryKey, Object primaryKeyValue) : void (Override)
     * - select(String tableName, String condition) : List<Map<String, Object>> (Override)
     * - selectAll(String tableName) : List<Map<String, Object>> (Override)
     * - executeQuery(String queryString) : List<Map<String, Object>> (Override)
     * - query(String queryString) : void (Override)
     * - createIndex(String collectionName, String fieldName, boolean unique) : void (Override)
     * - startTransaction() : void (Override)
     * - commitTransaction() : void (Override)
     * - rollbackTransaction() : void (Override)
     * <p>
     * Superclass:
     * Database (abstract class)
     * - Contains the declaration of connection variable.
     * <p>
     * Example usage:
     * <p>
     * ```
     * SQLiteDatabase database = new SQLiteDatabase();
     * database.connect("url", "username", "password");
     * // Do something with the connection
     * database.disconnect();
     * ```
     */
    private Connection connection;

    /**
     * Connects to the database using the given connection string, username, and password.
     *
     * @param connectionstring the connection string to connect to the database
     * @param username         the username to authenticate with the database
     * @param password         the password to authenticate with the database
     */
    @Override
    public void connect(String connectionstring, String username, String password) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + connectionstring);
        } catch (ClassNotFoundException | SQLException ex) {
            GlobalLogger.getLogger().error("Failed to connect to the database", ex);
        }
    }


    /**
     * Creates the necessary database tables if they don't already exist.
     */
    @Override
    public void createDatabaseAndTables() {
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
                "created TEXT," +
                "expires TEXT," +
                "reason TEXT" +
                ");";
        String createWarnedPlayersTable = "CREATE TABLE IF NOT EXISTS warnedplayers (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "name TEXT" +
                ");";
        String createWarnsTable = "CREATE TABLE IF NOT EXISTS warns (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
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
        try (Statement statement = connection.createStatement()) {
            statement.execute(createBannedPlayersTable);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error(createBannedPlayersTable, "|", ex.getSQLState() + ":" + ex.getErrorCode() + "/" + ex.getMessage(), ex);
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute(createBannedIpsTable);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error(createBannedIpsTable, "|", ex.getSQLState() + ":" + ex.getErrorCode() + "/" + ex.getMessage(), ex);
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute(createWarnedPlayersTable);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error(createWarnedPlayersTable, "|", ex.getSQLState() + ":" + ex.getErrorCode() + "/" + ex.getMessage(), ex);
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute(createWarnsTable);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error(createWarnsTable, "|", ex.getSQLState() + ":" + ex.getErrorCode() + "/" + ex.getMessage(), ex);
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute(createMutedPlayersTable);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error(createMutedPlayersTable, "|", ex.getSQLState() + ":" + ex.getErrorCode() + "/" + ex.getMessage(), ex);
        }
    }

    /**
     * Disconnects from the database by closing the connection.
     * If the connection is already closed or null, no action is performed.
     * Any exception that occurs during the process will be logged to the global logger.
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
     * Inserts a new row into the specified table with the provided data.
     *
     * @param tableName the name of the table to insert into
     * @param data      a map containing the column names as keys and the values to insert as values
     * @throws NullPointerException if tableName is null or if data is null or contains null keys
     * @throws SQLException         if an error occurs while executing the SQL statement
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
     * Updates a record in the specified table.
     *
     * @param tableName       the name of the table to update the record in
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key for the record to update
     * @param newData         a map representing the new data to update the record with
     * @throws NullPointerException if tableName, primaryKey, primaryKeyValue, or newData is null
     * @throws SQLException         if a database access error occurs
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
     * Deletes a record from the specified table using the given primary key and value.
     *
     * @param tableName       the name of the table to delete from
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key for the record to delete
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
     * Executes a SELECT SQL statement on the specified table with the given condition.
     *
     * @param tableName the name of the table to select from
     * @param condition the condition for selecting rows from the table
     * @return a {@link List} of {@link Map}s representing the selected rows, where each map contains the column names as keys and the corresponding column values as values
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
     * Retrieves all records from the specified table.
     *
     * @param tableName the name of the table to select records from.
     * @return a list of maps representing the records retrieved from the table. Each map contains the column names as keys and the corresponding column values as values.
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
     * Executes the given SQL query and returns a list of maps containing the query results.
     *
     * @param queryString the SQL query to execute
     * @return a list of maps where each map represents a row from the query result, with column names as keys
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
     * Executes a database query with the given query string.
     *
     * @param queryString The SQL query string to execute
     */
    @Override
    public void query(String queryString) {
        this.executeQuery(queryString); // didn't want to completly new write the code lol
    }

    /**
     * Creates an index on a specified field in a collection.
     *
     * @param collectionName the name of the collection
     * @param fieldName      the name of the field to create the index on
     * @param unique         indicates whether the index should be unique or not
     */
    @Override
    public void createIndex(String collectionName, String fieldName, boolean unique) {
        String indexType = unique ? "UNIQUE" : "";
        String indexName = "idx_" + collectionName + "_" + fieldName;
        String sql = "CREATE %s INDEX %s ON %s (%s)".formatted(indexType, indexName, collectionName, fieldName);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            GlobalLogger.getLogger().error("Failed to execute SQLStatement:", sql, ex);
        }
    }

    /**
     * Starts a transaction by setting the auto commit value to false.
     * If an SQLException occurs, it is logged.
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
     * Commits the current transaction.
     * <p>
     * This method commits the changes made during the current transaction. It calls the {@link Connection#commit()}
     * method to commit the changes and then sets the auto-commit mode back to true using {@link Connection#setAutoCommit(boolean)}.
     * <p>
     * If an error occurs during the commit process, an error message will be logged using the {@link GlobalLogger} class.
     *
     * @see Connection#commit()
     * @see Connection#setAutoCommit(boolean)
     * @see GlobalLogger
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
     * Rollbacks the current transaction and restores the auto-commit behavior of the database connection.
     * If an SQLException occurs during the rollback, it is logged as an error.
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
