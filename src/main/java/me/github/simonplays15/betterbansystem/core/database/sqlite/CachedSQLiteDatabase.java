package me.github.simonplays15.betterbansystem.core.database.sqlite;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class extends the SQLiteDatabase class and provides caching capabilities for select queries.
 * It maintains a cache map that stores the results of previous select queries based on the table name and condition.
 * The cache is cleared whenever an insert, update, delete or query operation is performed.
 * Additionally, the cache is also cleared when an index is created on a collection.
 */
public class CachedSQLiteDatabase extends SQLiteDatabase {

    /**
     *
     */
    private final Map<String, List<Map<String, Object>>> cache = new HashMap<>();

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
        String cacheKey = "select_" + tableName + "_" + condition;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        List<Map<String, Object>> result = super.select(tableName, condition);
        cache.put(cacheKey, result);
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
        String cacheKey = "select_all_" + tableName;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        List<Map<String, Object>> result = super.selectAll(tableName);
        cache.put(cacheKey, result);
        return result;
    }

    /**
     * Inserts data into the specified table.
     *
     * @param tableName the name of the table
     * @param data      a Map containing the column names and their corresponding values
     */
    @Override
    public void insert(String tableName, @NotNull Map<String, Object> data) {
        super.insert(tableName, data);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
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
        super.update(tableName, primaryKey, primaryKeyValue, newData);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
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
        super.delete(tableName, primaryKey, primaryKeyValue);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
    }

    /**
     * Executes a database query with the given query string.
     *
     * @param queryString The SQL query string to execute
     */
    @Override
    public void query(String queryString) {
        super.query(queryString);
        cache.clear();
    }

    /**
     * Creates an index on a specified field in a given collection. This method overrides the createIndex method
     * from the SQLiteDatabase class in order to provide caching capabilities for select queries.
     * It maintains a cache map that stores the results of previous select queries based on the table name and condition.
     * The cache is cleared whenever an insert, update, delete or query operation is performed.
     * Additionally, the cache is also cleared when an index is created on a collection.
     *
     * @param collectionName The name of the collection on which to create the index.
     * @param fieldName      The name of the field on which to create the index.
     * @param unique         Indicates whether the index should enforce uniqueness.
     */
    @Override
    public void createIndex(String collectionName, String fieldName, boolean unique) {
        super.createIndex(collectionName, fieldName, unique);
        cache.keySet().removeIf(key -> key.startsWith("select_" + collectionName) || key.startsWith("select_all_" + collectionName));
    }
}
