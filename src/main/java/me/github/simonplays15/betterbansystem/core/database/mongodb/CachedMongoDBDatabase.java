package me.github.simonplays15.betterbansystem.core.database.mongodb;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A subclass of MongoDBDatabase that implements caching for enhanced performance.
 * This class maintains a cache of query results to avoid accessing the database unnecessarily.
 */
public class CachedMongoDBDatabase extends MongoDBDatabase {

    /**
     *
     */
    private final Map<String, List<Map<String, Object>>> cache = new HashMap<>();

    /**
     * Retrieves a list of rows from a database table based on the given condition.
     * If the result is already present in the cache, it is returned from the cache instead of querying the database.
     *
     * @param tableName the name of the table to select from
     * @param condition the condition to use for selecting rows
     * @return a list of maps representing the selected rows, where each map contains column names as keys and column values as values
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
     * Retrieves all records from the specified table in the MongoDB database.
     *
     * @param tableName the name of the table to select from
     * @return a list of maps representing the selected records
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
     * Inserts a new document into the specified table in the MongoDB database.
     *
     * @param tableName the name of the table to insert the document into
     * @param data      a map containing the data to be inserted, with the field names as keys and the field values as values
     */
    @Override
    public void insert(String tableName, @NotNull Map<String, Object> data) {
        super.insert(tableName, data);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
    }

    /**
     * Updates a document in the specified MongoDB collection and clears the cache for the updated collection.
     *
     * @param tableName       the name of the collection
     * @param primaryKey      the name of the primary key field
     * @param primaryKeyValue the value of the primary key for the document to update
     * @param newData         a {@link Map} of field names and their updated values
     */
    @Override
    public void update(String tableName, String primaryKey, Object primaryKeyValue, @NotNull Map<String, Object> newData) {
        super.update(tableName, primaryKey, primaryKeyValue, newData);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
    }

    /**
     * Deletes a record from the specified table based on the primary key value. This method overrides the delete
     * method in the super class and additionally clears the cache of query results that are associated with the
     * deleted table.
     *
     * @param tableName       the name of the table
     * @param primaryKey      the name of the primary key column
     * @param primaryKeyValue the value of the primary key to match
     */
    @Override
    public void delete(String tableName, String primaryKey, Object primaryKeyValue) {
        super.delete(tableName, primaryKey, primaryKeyValue);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
    }

    /**
     * Executes a query on the MongoDB database and clears the cache.
     *
     * @param queryString the query string to execute
     */
    @Override
    public void query(String queryString) {
        super.query(queryString);
        cache.clear();
    }

    /**
     * Creates an index in the specified collection on the given field.
     *
     * @param collectionName the name of the collection where the index is to be created
     * @param fieldName      the name of the field on which the index is to be created
     * @param unique         true if the index should enforce a unique constraint, false otherwise
     */
    @Override
    public void createIndex(String collectionName, String fieldName, boolean unique) {
        super.createIndex(collectionName, fieldName, unique);
        cache.keySet().removeIf(key -> key.startsWith("select_" + collectionName) || key.startsWith("select_all_" + collectionName));
    }
}
