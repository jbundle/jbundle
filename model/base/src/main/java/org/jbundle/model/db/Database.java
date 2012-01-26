/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model.db;

import java.util.Map;

import org.jbundle.model.Freeable;


/**
 * List of Records (FieldList[s]).
 */
public interface Database
    extends Freeable
{
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public void init(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType, Map<String, Object> properties);
    /**
     * Get the databaseOwner.
     * @return The databaseOwner.
     */
    public DatabaseOwner getDatabaseOwner();
    /**
     * Get the Database Name.
     * @param bPhysicalName Return the full physical name of the database
     * @return The db name.
     */
    public String getDatabaseName(boolean bPhysicalName);
    /**
     * Get the database type.
     * @return The db type (REMOTE/LOCAL).
     */
    public int getDatabaseType();
    /**
     * Set the m_databaseOwner.
     * @param databaseOwner The databaseOwner to set.
     */
    public void setDatabaseOwner(DatabaseOwner databaseOwner);
}
