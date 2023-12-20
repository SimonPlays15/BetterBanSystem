/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.database;

import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.api.database.mongodb.MongoDBDatabase;
import we.github.mcdevstudios.betterbansystem.api.database.mysql.MySQLDatabase;
import we.github.mcdevstudios.betterbansystem.api.database.sqlite.SQLiteDatabase;

public abstract class Database implements IDatabase {

    /**
     * @param driver DriverType
     * @see DriverType
     */
    public IDatabase getDatabaseDriver(@NotNull DriverType driver) {
        IDatabase h = null;

        switch (driver) {
            case MySQL -> h = new MySQLDatabase();
            case SQLite -> h = new SQLiteDatabase();
            case MongoDB -> new MongoDBDatabase();
        }

        return h;
    }

}
