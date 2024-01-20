package me.github.simonplays15.betterbansystem.core.database;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.List;
import java.util.Map;

/**
 * The IDatabase interface represents a generic database and provides methods to interact with the database.
 */
public interface IDatabase {


    /**
     * Creates a database and associated tables.
     */
    void createDatabaseAndTables();

    /**
     * Establishes a connection to the specified database using the provided connection string, username, and password.
     *
     * @param connectionstring A string containing the connection details for the database.
     * @param username         The username for the database connection.
     * @param password         The password for the database connection.
     */
    void connect(String connectionstring, String username, String password);

    /**
     * Disconnects from the database.
     * This method closes the connection to the database and releases any resources associated with it.
     * After calling this method, further database operations cannot be performed until a new connection is established.
     */
    void disconnect();

    /**
     * Inserts data into the specified table and clears the corresponding cache entries.
     *
     * @param tableName the name of the table
     * @param data      a map containing the column names and their corresponding values to be inserted
     */
    void insert(String tableName, Map<String, Object> data);

    /**
     * Updates a record in the specified table with the given primary key and new data.
     *
     * @param tableName       the name of the table to update
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key for the record to update
     * @param newData         a map of column names to new values for the record
     */
    void update(String tableName, String primaryKey, Object primaryKeyValue, Map<String, Object> newData);

    /**
     * Deletes a record from the specified table based on the primary key value.
     *
     * @param tableName       the name of the table from which to delete the record
     * @param primaryKey      the name of the primary key column in the table
     * @param primaryKeyValue the value of the primary key to identify the record to be deleted
     */
    void delete(String tableName, String primaryKey, Object primaryKeyValue);

    /**
     * Executes a SELECT query on a specified table with a provided condition.
     *
     * @param tableName the name of the table to select from
     * @param condition the condition to apply in the WHERE clause
     * @return a List of Map objects where each Map represents a row in the result set, with column names as keys and column
     * values as values
     */
    List<Map<String, Object>> select(String tableName, String condition);

    /**
     * Retrieves all records from the specified table.
     *
     * @param tableName the name of the table from which to retrieve the records
     * @return a list of maps, where each map represents a record from the table.
     * The keys in the map correspond to the column names, and the values
     * are the values of the corresponding columns in the record.
     */
    List<Map<String, Object>> selectAll(String tableName);

    /**
     * Executes a SQL query and returns the result as a List of Maps.
     *
     * @param queryString the SQL query to be executed
     * @return a List of Maps containing the query result. Each Map represents a row of the result,
     * with the column names as keys and the corresponding values as values.
     */
    List<Map<String, Object>> executeQuery(String queryString);

    /**
     * Executes a database query with the given query string and clears the cache.
     *
     * @param queryString the query string to execute
     */
    void query(String queryString);

    /**
     * Creates an index on a specified field in a collection.
     *
     * @param collectionName The name of the collection on which to create the index.
     * @param fieldName      The name of the field on which to create the index.
     * @param unique         True if the index should be unique, false otherwise.
     */
    void createIndex(String collectionName, String fieldName, boolean unique);

    /**
     * Starts a new transaction.
     * <p>
     * A transaction is a sequence of actions that are executed as a single unit of work.
     * In a transaction, multiple database operations can be performed, such as inserting, updating, and deleting data.
     * Changes made during a transaction are temporary and isolated from other transactions until the transaction is committed.
     * If a transaction is not committed, the changes will be rolled back and discarded.
     * <p>
     * Note that transactions are supported by the underlying database, and some databases may not support transactions.
     * If the database does not support transactions, calling this method will have no effect.
     * <p>
     * Example usage:
     * <pre>
     *     database.startTransaction();
     *     try {
     *         // Perform database operations
     *         database.insert("customers", customerData);
     *         database.update("products", "id", productId, updatedProductData);
     *         database.delete("orders", "id", orderId);
     *         // Commit the transaction
     *         database.commitTransaction();
     *     } catch (Exception e) {
     *         // Handle exception
     *         // Roll back the transaction
     *         database.rollbackTransaction();
     *     }
     * </pre>
     */
    void startTransaction();

    /**
     * Commits the current transaction.
     */
    void commitTransaction();

    /**
     * Rolls back the current transaction.
     * This method is used to undo changes made within a transaction and restore the database to its previous state.
     * If a rollback operation is successful, all changes made within the transaction are discarded and the database is returned to its state prior to starting the transaction
     * .
     * If the rollback operation fails, an exception may be thrown, indicating that the transaction was not successfully rolled back.
     * This method should only be called after starting a transaction using the {@link #startTransaction()} method.
     * <p>
     * Example usage:
     * <pre>{@code
     *     IDatabase database = new Database();
     *     ...
     *     database.startTransaction();
     *     try {
     *         // Perform database operations within the transaction
     *         ...
     *         database.rollbackTransaction();
     *     } catch (Exception ex) {
     *         // Handle exception
     *     }
     * }</pre>
     */
    void rollbackTransaction();

}
