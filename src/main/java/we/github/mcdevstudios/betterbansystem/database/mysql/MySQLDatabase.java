/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.database.mysql;

import we.github.mcdevstudios.betterbansystem.database.Database;
import we.github.mcdevstudios.betterbansystem.database.IDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLDatabase extends Database {

    private Connection connection;

    /**
     * Connect to a specific database
     *
     * @param connectionstring String
     * @param username         String
     * @param password         String
     * @apiNote connectionstring example: {@snippet String connectionString = host+":"+port+"/"+database}
     */
    @Override
    public void connect(String connectionstring, String username, String password) {
        try {
            String url = "jdbc:mysql://" + connectionstring;
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
        }
    }

    /**
     * Disconnect from the database
     */
    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException ex) {
            // TODO ADd correct logging
            ex.printStackTrace();
        }
    }

    /**
     * Insert (KEY) with VALUES (VALUE) into Database
     *
     * @param tableName String
     * @param data      Map
     */
    public void insert(String tableName, Map<String, Object> data) {
        StringBuilder coloumns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            coloumns.append(entry.getKey()).append(", ");
            values.append("?, ");
        }

        coloumns.delete(coloumns.length() - 2, coloumns.length());
        values.delete(coloumns.length() - 2, values.length());

        String sql = "INSERT INTO " + tableName + " (" + coloumns + ") VALUES (" + values + ")";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (Object value : data.values()) {
                statement.setObject(index++, value);
            }

            statement.executeUpdate();
        } catch (SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
        }
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
        StringBuilder setClause = new StringBuilder();

        for (Map.Entry<String, Object> entry : newData.entrySet()) {
            setClause.append(entry.getKey()).append(" = ?, ");
        }

        setClause.delete(setClause.length() - 2, setClause.length());
        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + primaryKey + " = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 2;
            for (Object value : newData.values()) {
                statement.setObject(index++, value);
            }

            statement.setObject(index, primaryKeyValue);

            statement.executeUpdate();
        } catch (SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
        }

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
        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKey + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, primaryKeyValue);

            statement.executeUpdate();
        } catch (SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
        }
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

        String sql = "SELECT * FROM " + tableName + " WHERE " + condition;

        try (Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery(sql);
            ResultSetMetaData metaData = set.getMetaData();
            int c = metaData.getColumnCount();
            while (set.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int i = 0; i <= c; i++) {
                    String a = metaData.getColumnName(i);
                    Object b = set.getObject(i);
                    row.put(a, b);
                }
                result.add(row);
            }

        } catch (SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
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

        String sql = "SELECT * FROM " + tableName;

        try (Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery(sql);
            ResultSetMetaData metaData = set.getMetaData();
            int c = metaData.getColumnCount();
            while (set.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int i = 0; i <= c; i++) {
                    String a = metaData.getColumnName(i);
                    Object b = set.getObject(i);
                    row.put(a, b);
                }
                result.add(row);
            }

        } catch (SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
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
            // TODO add correct logging
            ex.printStackTrace();
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
        String indexType = unique ? "UNIQUE" : "";
        String indexName = "idx_" + collectionName + "_" + fieldName;
        String sql = "CREATE " + indexType + " INDEX " + indexName + " ON " + collectionName + " (" + fieldName + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
        }
    }

    /**
     * Transaction start
     */
    @Override
    public void startTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
        }
    }

    /**
     * Transaction commit
     */
    @Override
    public void commitTransaction() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
        }
    }

    /**
     * Transaction Rollback
     */
    @Override
    public void rollbackTransaction() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            // TODO add correct logging
            ex.printStackTrace();
        }
    }
}
