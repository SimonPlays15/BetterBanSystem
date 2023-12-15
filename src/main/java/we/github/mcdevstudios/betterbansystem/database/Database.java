/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.database;

import org.apache.commons.lang.NotImplementedException;

public abstract class Database implements IDatabase {

    public Database() {
    }

    /**
     * @throws NotImplementedException
     */
    public Database getDatabaseDriver() {
        throw new NotImplementedException();
    }

}
