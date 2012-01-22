/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.serial;

/**
 * @(#)SerialDatabase.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.memory.MemoryDatabase;
import org.jbundle.base.db.physical.PhysicalTable;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DatabaseOwner;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.serial.SDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;


/**
 * SerialDatabase - Implement the Serial database.
 *  The Serial database is EXACTLY the same as the Vector Database, except when it
 *  closes, data is searalized to the Serial file. When it opens, data is extracted from
 *  the Serial file.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SerialDatabase extends MemoryDatabase
{
    /**
     * Constructor
     */
    public SerialDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public SerialDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)
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

        this.setProperty(DBConstants.READ_ONLY_DB, DBConstants.TRUE);   // This is always read-only
    }
    /**
     * Make a table for this database.
     * @param record The record to make a raw data table for.
     * @return The new raw data table.
     */
    public PhysicalTable makePhysicalTable(Record record)
    {
        return new SerialTable(this, record);
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
        return new SDatabase(pDatabaseOwner, strDbName);
    }
    /**
     * Get the physical database type.
     */
    public char getPDatabaseType()
    {
        return ThinPhysicalDatabase.SERIAL_TYPE;
    }
}
