/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.memory;

/**
 * @(#)MemoryDatabase.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.physical.PhysicalDatabase;
import org.jbundle.base.db.physical.PhysicalTable;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.DatabaseOwner;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.memory.MDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;


/**
 * Implement the Memory database.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MemoryDatabase extends PhysicalDatabase
{

    /**
     * Constructor.
     */
    public MemoryDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public MemoryDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)
    {
        this();
        this.init(databaseOwner, strDbName, iDatabaseType, null);
    }
    /**
     * Init this database and add it to the databaseOwner.
     * @param databaseOwner My databaseOwner.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     * @param strDBName The database name.
     */
    public void init(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType, Map<String, Object> properties)
    {
        super.init(databaseOwner, strDbName, iDatabaseType, properties);

        if ((iDatabaseType & Constants.REMOTE_MEMORY) == 0)
        {   // Usually don't send messages (unless this is a remote copy, then I would)
            this.setProperty(DBParams.MESSAGES_TO_REMOTE, DBConstants.FALSE);   // Send by server version
            this.setProperty(DBParams.CREATE_REMOTE_FILTER, DBConstants.FALSE); // Create the remote filter
            this.setProperty(DBParams.UPDATE_REMOTE_FILTER, DBConstants.FALSE); // Updated by server version
        }
    }
    /**
     * Make a table for this database.
     * @param record The record to make a raw data table for.
     * @return The new raw data table.
     */
    public PhysicalTable makePhysicalTable(Record record)
    {
        return new MemoryTable(this, record);
    }
    /**
     * Create the raw data database.
     * @param strDbName The database name.
     * @param env Environment (optional).
     * @param strDatabaseType The database type.
     * @return The raw data database.
     */
    public PDatabase makePDatabase(PhysicalDatabaseParent pDatabaseOwner, String strDbName)
    {
        return new MDatabase(pDatabaseOwner, strDbName);
    }
    /**
     * Get the physical database type.
     */
    public char getPDatabaseType()
    {
        return ThinPhysicalDatabase.MEMORY_TYPE;
    }
    /**
     * If one exists, set up the Local version of this record.
     * Do this by opening a local version of this database and attaching a ResourceTable
     * to the record.
     * <p />Note:  Vector can not have a locale database (because it is not persisitent).
     * @param record The record.
     * @param table The current table.
     * @return The new table.
     */
    public BaseTable makeDBLocale(Record record, BaseTable table)
    {
        return table;
    }
}
