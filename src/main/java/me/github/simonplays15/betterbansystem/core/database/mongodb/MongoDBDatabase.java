package me.github.simonplays15.betterbansystem.core.database.mongodb;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import me.github.simonplays15.betterbansystem.core.database.Database;
import org.apache.commons.lang.NotImplementedException;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MongoDBDatabase is a concrete class that extends the abstract class Database. It provides
 * the implementation of the methods to interact with a MongoDB database. This class uses the
 * MongoDB driver to connect to and perform operations on the database.
 */
public class MongoDBDatabase extends Database {

    /**
     * The MongoClient instance used to connect to a MongoDB database.
     */
    private MongoClient mongoClient;
    /**
     * Private variable that represents the database for the MongoDBDatabase class.
     * The database is an instance of the MongoDatabase class from the MongoDB Java driver.
     */
    private MongoDatabase database;

    /**
     * Create the database and tables required for the application to function.
     */
    @Override
    public void createDatabaseAndTables() {
        MongoDatabase database = mongoClient.getDatabase("betterbansystem");

        database.getCollection("bannedplayers");
        database.getCollection("bannedips");
        database.getCollection("warnedplayers");
        database.getCollection("mutedplayers");
    }

    /**
     * Connects to a specific database using the provided connection string, username, and password.
     *
     * @param connectionstring the connection string to connect to the database
     * @param username         the username for authentication
     * @param password         the password for authentication
     */
    @Override
    public void connect(@NotNull String connectionstring, String username, String password) {
        String url = "mongodb://" + username + ":" + password + "@" + connectionstring;
        mongoClient = MongoClients.create(url);
        database = mongoClient.getDatabase(connectionstring.split("/")[1]);
    }

    /**
     * Disconnects from the database.
     */
    @Override
    public void disconnect() {
        if (mongoClient != null)
            mongoClient.close();
    }

    /**
     * Inserts a new document into the specified table in the MongoDB database.
     *
     * @param tableName the name of the table to insert the document into
     * @param data      a map containing the data to be inserted, with the field names as keys and the field values as values
     */
    public void insert(String tableName, Map<String, Object> data) {
        MongoCollection<Document> collection = database.getCollection(tableName);
        Document doc = new Document(data);
        collection.insertOne(doc);
    }

    /**
     * Updates a document in the specified MongoDB collection.
     *
     * @param tableName       the name of the collection
     * @param primaryKey      the name of the primary key field
     * @param primaryKeyValue the value of the primary key for the document to update
     * @param newData         a {@link Map} of field names and their updated values
     */
    @Override
    public void update(String tableName, String primaryKey, Object primaryKeyValue, Map<String, Object> newData) {
        MongoCollection<Document> collection = database.getCollection(tableName);
        Document filter = new Document(primaryKey, primaryKeyValue);
        Document update = new Document("$set", new Document(newData));

        collection.updateOne(filter, update);
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
        MongoCollection<Document> collection = database.getCollection(tableName);
        collection.deleteOne(Filters.eq(primaryKey, primaryKeyValue));
    }

    /**
     * Retrieves a list of rows from a database table based on the given condition.
     *
     * @param tableName the name of the table to select from
     * @param condition the condition to use for selecting rows
     * @return a list of maps representing the selected rows, where each map contains column names as keys and column values as values
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
     * Retrieves all records from the specified table in the MongoDB database.
     *
     * @param tableName the name of the table to select from
     * @return a list of maps representing the selected records
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
     * Executes a query on the MongoDB database.
     *
     * @param queryString The query string to be executed.
     * @return A list of maps representing the query result. Each map contains field-value pairs of the documents returned by the query.
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
     *
     */
    @Override
    public void query(String queryString) {
        this.executeQuery(queryString); // didn't want to completly new write the code lol
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
        MongoCollection<Document> collection = database.getCollection(collectionName);
        IndexOptions indexOptions = new IndexOptions().unique(unique);
        collection.createIndex(Indexes.ascending(fieldName), indexOptions);
    }

    /**
     * Starts a transaction.
     */
    @Override
    public void startTransaction() {
        throw new NotImplementedException();
    }

    /**
     * Commits the current transaction.
     */
    @Override
    public void commitTransaction() {
        throw new NotImplementedException();
    }

    /**
     * Rolls back the current transaction.
     */
    @Override
    public void rollbackTransaction() {
        throw new NotImplementedException();
    }
}
