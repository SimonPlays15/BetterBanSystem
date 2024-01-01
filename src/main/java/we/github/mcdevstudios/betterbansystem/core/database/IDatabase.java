/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.database;

import java.util.List;
import java.util.Map;

public interface IDatabase {


    /**
     * Connect to a specific database
     *
     * @param connectionstring string
     * @param username         string
     * @param password         string
     */
    void connect(String connectionstring, String username, String password);

    /**
     * Disconnect from the database
     */
    void disconnect();

    /**
     * Insert (KEY) with VALUES (VALUE) into Database
     *
     * @param tableName String
     * @param data      Map
     */
    void insert(String tableName, Map<String, Object> data);

    /**
     * Update (KEY) with VALUES (VALUE) in database
     *
     * @param tableName       String
     * @param primaryKey      String
     * @param primaryKeyValue String
     * @param newData         Map
     */
    void update(String tableName, String primaryKey, Object primaryKeyValue, Map<String, Object> newData);

    /**
     * Delete (KEY) with specific "VALUE"
     *
     * @param tableName       String
     * @param primaryKey      String
     * @param primaryKeyValue Object
     */
    void delete(String tableName, String primaryKey, Object primaryKeyValue);

    /**
     * Select a value from table with condition
     *
     * @param tableName String
     * @param condition String
     * @return List
     */
    List<Map<String, Object>> select(String tableName, String condition);

    /**
     * Select everything from a table name
     *
     * @param tableName String
     * @return List
     */
    List<Map<String, Object>> selectAll(String tableName);

    /**
     * Execute a query
     *
     * @param queryString String
     * @return List
     */
    List<Map<String, Object>> executeQuery(String queryString);

    /**
     * @param queryString String
     * @apiNote same like {@link  IDatabase#executeQuery} but without returning anything
     * @see IDatabase#executeQuery(String)
     */
    void query(String queryString);

    /**
     * Create a Index
     *
     * @param collectionName String
     * @param fieldName      String
     * @param unique         boolean
     */
    void createIndex(String collectionName, String fieldName, boolean unique);

    /**
     * Transaction start
     */
    void startTransaction();

    /**
     * Transaction commit
     */
    void commitTransaction();

    /**
     * Transaction Rollback
     */
    void rollbackTransaction();

}
