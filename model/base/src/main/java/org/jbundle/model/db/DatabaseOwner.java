/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.db;

/**
 * @(#)Environment.java   1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.model.PropertyOwner;


/**
 * DatabaseOwner.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public interface DatabaseOwner extends PropertyOwner
{
    /**
     * Given the name, either get the open database, or open a new one.
     * @param strDBName The name of the database.
     * @param iDatabaseType The type of database/table.
     * @param properties The database properties
     * @return The database (new or current).
     */
    public Database getDatabase(String strDBName, int iDatabaseType, Map<String, Object> properties);
    /**
     * Add this database to my database list.<br />
     * Do not call these directly, used in database init.
     * @param database The database to add.
     */
    public void addDatabase(Database database);
    /**
     * Remove this database from my database list.
     * Do not call these directly, used in database free.
     * @param database The database to free.
     * @return true if successful.
     */
    public boolean removeDatabase(Database database);
    /**
     * Get the environment.
     * @return The Environment. NEVER return NULL!
     */
    public PropertyOwner getEnvironment();
}
