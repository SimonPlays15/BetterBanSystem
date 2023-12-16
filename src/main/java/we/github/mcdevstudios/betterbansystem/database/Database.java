/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.database;

import we.github.mcdevstudios.betterbansystem.database.mongodb.MongoDBDatabase;
import we.github.mcdevstudios.betterbansystem.database.mysql.MySQLDatabase;
import we.github.mcdevstudios.betterbansystem.database.sqlite.SQLiteDatabase;

public abstract class Database implements IDatabase {

    public Database() {
    }

    /**
     * @param driver DriverType
     * @see DriverType
     */
    public IDatabase getDatabaseDriver(DriverType driver) {
        IDatabase h = null;

        switch (driver) {
            case MySQL -> h = new MySQLDatabase();
            case SQLite -> h = new SQLiteDatabase();
            case MongoDB -> new MongoDBDatabase();
        }

        return h;
    }

}
