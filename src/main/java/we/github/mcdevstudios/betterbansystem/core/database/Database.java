/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.database;

import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import we.github.mcdevstudios.betterbansystem.core.database.mongodb.MongoDBDatabase;
import we.github.mcdevstudios.betterbansystem.core.database.mysql.MySQLDatabase;
import we.github.mcdevstudios.betterbansystem.core.database.sqlite.SQLiteDatabase;

public abstract class Database implements IDatabase {

    public Database() {
        throw new NotImplementedException("No database Driver given");
    }

    /**
     * Retrieves the appropriate database driver based on the specified driver type.
     *
     * @param driver the driver type
     * @return the corresponding database driver
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
