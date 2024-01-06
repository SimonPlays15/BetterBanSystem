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

public class SQLiteDatabase extends Database {

    private Connection connection;

    /**
     * Connects to the database using the specified connection string, username, and password.
     *
     * @param connectionstring the connection string to the database
     * @param username         the username for the database connection
     * @param password         the password for the database connection
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
     * Creates the database and tables required for the BetterBanSystem application.
     * If the database does not exist, it will be created. If the tables do not exist, they will be created.
     * The tables include the "bannedplayers", "bannedips", "warnedplayers", "warns", and "mutedplayers" tables.
     * Each table has its own structure and columns.
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
     * Closes the database connection.
     * If the connection is not null and not closed, it will be closed.
     * If an SQLException occurs while closing the connection, an error message will be logged.
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
     * @param data      a Map containing the column names and their corresponding values
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
     * Updates a record in the specified table with new data based on the primary key.
     *
     * @param tableName       the name of the table to update
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key of the record to update
     * @param newData         a map containing the new column-value pairs to update
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
     * Deletes a record from the specified table based on the primary key.
     *
     * @param tableName       the name of the table where the record will be deleted
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key for the record to be deleted
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
     * Executes a SELECT query on the specified table with the given condition.
     * Returns a list of maps, where each map represents a row of the result set
     * with column names as keys and column values as values.
     *
     * @param tableName the name of the table to query
     * @param condition the condition to apply in the WHERE clause of the query
     * @return a list of maps representing the result set of the SELECT query
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
     * @param tableName the name of the table
     * @return a list of maps representing each record in the table
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
     * Executes the given SQL query string and returns the result as a list of maps,
     * where each map represents a row and contains column names as keys and
     * corresponding column values as values.
     *
     * @param queryString the SQL query string to execute
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
     * Executes a database query with the given query string.
     *
     * @param queryString The SQL query string to execute
     */
    @Override
    public void query(String queryString) {
        this.executeQuery(queryString); // didn't want to completly new write the code lol
    }

    /**
     * Creates an index on a specified field in a given collection.
     *
     * @param collectionName The name of the collection on which to create the index.
     * @param fieldName      The name of the field on which to create the index.
     * @param unique         Indicates whether the index should enforce uniqueness.
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
     *
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
     * Commits the current transaction and sets auto-commit to true.
     * If an SQLException occurs during the commit, it will be logged with the GlobalLogger.
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
     * Rolls back the current transaction.
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
