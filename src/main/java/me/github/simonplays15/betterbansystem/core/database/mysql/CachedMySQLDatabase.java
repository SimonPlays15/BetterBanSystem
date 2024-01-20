package me.github.simonplays15.betterbansystem.core.database.mysql;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A cached implementation of MySQLDatabase that provides caching for database queries.
 * This class extends MySQLDatabase.
 */
public class CachedMySQLDatabase extends MySQLDatabase {

    /**
     * The cache variable is a private final Map that stores data retrieved from the database.
     * It is used for efficient data retrieval and manipulation operations.
     * <p>
     * The key of the cache Map is a String representing the name of the table in the database.
     * The value associated with each key is a List of Map objects, where each Map represents a row in the result set.
     * The keys in the inner Map represent column names, and the values represent column values.
     * <p>
     * It is recommended to use the cache variable when performing SELECT queries or other read operations on the database.
     * The cached data can be accessed and manipulated easily, without the need to query the database multiple times.
     * <p>
     * Example usage:
     * <p>
     * // Adding data to the cache
     * List<Map<String, Object>> data = new ArrayList<>();
     * Map<String, Object> row = new HashMap<>();
     * row.put("id", 1);
     * row.put("name", "John");
     * data.add(row);
     * cache.put("users", data);
     * <p>
     * // Retrieving data from the cache
     * List<Map<String, Object>> userData = cache.get("users");
     * for (Map<String, Object> user : userData) {
     * int id = (int) user.get("id");
     * String name = (String) user.get("name");
     * System.out.println("User ID: " + id + ", Name: " + name);
     * }
     * <p>
     * // Updating data in the cache
     * Map<String, Object> newUser = new HashMap<>();
     * newUser.put("id", 2);
     * newUser.put("name", "Jane");
     * data.add(newUser);
     * cache.put("users", data);
     * <p>
     * // Removing data from the cache
     * cache.remove("users");
     * <p>
     * Note: It is important to keep the cache variable updated with the latest data from the database
     * to ensure data consistency and accuracy.
     */
    private final Map<String, List<Map<String, Object>>> cache = new HashMap<>();

    /**
     * Executes a SELECT query on a specified table with a provided condition.
     *
     * @param tableName the name of the table to select from
     * @param condition the condition to apply in the WHERE clause
     * @return a List of Map objects where each Map represents a row in the result set, with column names as keys and column
     * values as values
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
     * Retrieves all rows from the specified table in the database.
     *
     * @param tableName the name of the table to select from
     * @return a list of maps containing the rows from the table
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
     * Inserts data into the specified table and clears the corresponding cache entries.
     *
     * @param tableName the name of the table
     * @param data      a map containing the column names and their corresponding values to be inserted
     */
    @Override
    public void insert(String tableName, @NotNull Map<String, Object> data) {
        super.insert(tableName, data);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
    }

    /**
     * Updates a record in the specified table with the given primary key and new data.
     *
     * @param tableName       the name of the table to update
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key for the record to update
     * @param newData         a map of column names to new values for the record
     */
    @Override
    public void update(String tableName, String primaryKey, Object primaryKeyValue, @NotNull Map<String, Object> newData) {
        super.update(tableName, primaryKey, primaryKeyValue, newData);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
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
        super.delete(tableName, primaryKey, primaryKeyValue);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
    }

    /**
     * Clears the cache and executes the given query string.
     *
     * @param queryString the query string to execute
     */
    @Override
    public void query(String queryString) {
        super.query(queryString);
        cache.clear();
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
        super.createIndex(collectionName, fieldName, unique);
        cache.keySet().removeIf(key -> key.startsWith("select_" + collectionName) || key.startsWith("select_all_" + collectionName));
    }
}
