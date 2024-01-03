/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.database.mongodb;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedMongoDBDatabase extends MongoDBDatabase {

    private final Map<String, List<Map<String, Object>>> cache = new HashMap<>();

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

    @Override
    public void insert(String tableName, @NotNull Map<String, Object> data) {
        super.insert(tableName, data);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
    }

    @Override
    public void update(String tableName, String primaryKey, Object primaryKeyValue, @NotNull Map<String, Object> newData) {
        super.update(tableName, primaryKey, primaryKeyValue, newData);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
    }

    @Override
    public void delete(String tableName, String primaryKey, Object primaryKeyValue) {
        super.delete(tableName, primaryKey, primaryKeyValue);
        cache.keySet().removeIf(key -> key.startsWith("select_" + tableName) || key.startsWith("select_all_" + tableName));
    }

    @Override
    public void query(String queryString) {
        super.query(queryString);
        cache.clear();
    }

    @Override
    public void createIndex(String collectionName, String fieldName, boolean unique) {
        super.createIndex(collectionName, fieldName, unique);
        cache.keySet().removeIf(key -> key.startsWith("select_" + collectionName) || key.startsWith("select_all_" + collectionName));
    }
}
