/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.database.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.apache.commons.lang.NotImplementedException;
import org.bson.Document;
import we.github.mcdevstudios.betterbansystem.database.Database;
import we.github.mcdevstudios.betterbansystem.database.IDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoDBDatabase extends Database {

    private MongoClient mongoClient;
    private MongoDatabase database;

    /**
     * Connect to a specific database
     *
     * @param connectionstring String
     * @param username         String
     * @param password         String
     * @apiNote {@code connectionString = host+":"+port+"/"+database;}
     */
    @Override
    public void connect(String connectionstring, String username, String password) {
        String url = "mongodb://" + username + ":" + password + "@" + connectionstring;
        mongoClient = MongoClients.create(url);
        database = mongoClient.getDatabase(connectionstring.split("/")[1]);
    }

    /**
     * Disconnect from the database
     */
    @Override
    public void disconnect() {
        if (mongoClient != null)
            mongoClient.close();
    }

    /**
     * Insert (KEY) with VALUES (VALUE) into Database
     *
     * @param tableName String
     * @param data      Map
     */
    public void insert(String tableName, Map<String, Object> data) {
        MongoCollection<Document> collection = database.getCollection(tableName);
        Document doc = new Document(data);
        collection.insertOne(doc);
    }

    /**
     * Update (KEY) with VALUES (VALUE) in database
     *
     * @param tableName       String
     * @param primaryKey      String
     * @param primaryKeyValue String
     * @param newData         Map
     */
    @Override
    public void update(String tableName, String primaryKey, Object primaryKeyValue, Map<String, Object> newData) {
        MongoCollection<Document> collection = database.getCollection(tableName);
        Document filter = new Document(primaryKey, primaryKeyValue);
        Document update = new Document("$set", new Document(newData));

        collection.updateOne(filter, update);
    }

    /**
     * Delete (KEY) with specific "VALUE"
     *
     * @param tableName       String
     * @param primaryKey      String
     * @param primaryKeyValue Object
     */
    @Override
    public void delete(String tableName, String primaryKey, Object primaryKeyValue) {
        MongoCollection<Document> collection = database.getCollection(tableName);
        collection.deleteOne(Filters.eq(primaryKey, primaryKeyValue));
    }

    /**
     * Select a value from table with condition
     *
     * @param tableName String
     * @param condition String
     * @return List
     */
    @Override
    public List<Map<String, Object>> select(String tableName, String condition) {
        List<Map<String, Object>> result = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection(tableName);

        for (Document document : collection.find(Document.parse(condition))) {
            Map<String, Object> row = new HashMap<>(document);
            result.add(row);
        }


        return result;
    }

    /**
     * Select everything from a table name
     *
     * @param tableName String
     * @return List
     */
    @Override
    public List<Map<String, Object>> selectAll(String tableName) {
        List<Map<String, Object>> result = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection(tableName);

        for (Document document : collection.find()) {
            Map<String, Object> row = new HashMap<>(document);
            result.add(row);
        }

        return result;
    }

    /**
     * Execute a query
     *
     * @param queryString String
     * @return List
     */
    @Override
    public List<Map<String, Object>> executeQuery(String queryString) {
        List<Map<String, Object>> result = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection(queryString);

        for (Document document : collection.find()) {
            Map<String, Object> row = new HashMap<>(document);
            result.add(row);
        }

        return result;
    }

    /**
     * @param queryString String
     * @apiNote same like {@link  IDatabase#executeQuery} but without returning anything
     * @see IDatabase#executeQuery(String)
     */
    @Override
    public void query(String queryString) {
        this.executeQuery(queryString); // didn't want to completly new write the code lol
    }

    /**
     * @param collectionName String
     * @param fieldName      String
     * @param unique         boolean
     */
    @Override
    public void createIndex(String collectionName, String fieldName, boolean unique) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        IndexOptions indexOptions = new IndexOptions().unique(unique);
        collection.createIndex(Indexes.ascending(fieldName), indexOptions);
    }

    /**
     * Transaction start
     */
    @Override
    public void startTransaction() {
        throw new NotImplementedException();
    }

    /**
     * Transaction commit
     */
    @Override
    public void commitTransaction() {
        throw new NotImplementedException();
    }

    /**
     * Transaction Rollback
     */
    @Override
    public void rollbackTransaction() {
        throw new NotImplementedException();
    }
}
