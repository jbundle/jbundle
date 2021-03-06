/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.mapped;

/**
 * @(#)SerialDatabase.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.db.memory.MemoryDatabase;
import org.jbundle.base.db.physical.PhysicalTable;
import org.jbundle.model.db.DatabaseOwner;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;


/**
 * SerialDatabase - Implement the Serial database.
 *  The Serial database is EXACTLY the same as the Memory Database, except when it
 *  closes, data is searalized to the Serial file. When it opens, data is extracted from
 *  the Serial file.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MappedDatabase extends MemoryDatabase
{
    /**
     * Constructor
     */
    public MappedDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public MappedDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)
    {
        this();
        this.init(databaseOwner, strDbName, iDatabaseType, null);
    }
    /**
     * Make a table for this database.
     * @param record The record to make a raw data table for.
     * @return The new raw data table.
     */
    public PhysicalTable makePhysicalTable(Record record)
    {
        return new MappedTable(this, record);
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
        return new ZDatabase(pDatabaseOwner, strDbName);
    }
    /**
     * Get the physical database type.
     */
    public char getPDatabaseType()
    {
        return ThinPhysicalDatabase.MAPPED_TYPE;
    }
}
