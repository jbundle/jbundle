package org.jbundle.base.db.proxy;

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
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DatabaseOwner;
import org.jbundle.thin.base.db.ThinPhysicalDatabase;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.proxy.YDatabase;


/**
 * ProxyDatabase - A Proxy database is the same as a net database, except data transfers are done through the proxy instead of direct http.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ProxyDatabase extends MemoryDatabase
{
    /**
     * Constructor
     */
    public ProxyDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public ProxyDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)
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

        this.setProperty(DBConstants.READ_ONLY_DB, DBConstants.TRUE);   // For now this is always read-only
    }
    /**
     * Make a table for this database.
     * @param record The record to make a raw data table for.
     * @return The new raw data table.
     */
    public PhysicalTable makePhysicalTable(Record record)
    {
        return new ProxyTable(this, record);
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
        return new YDatabase(pDatabaseOwner, strDbName);
    }
    /**
     * Get the physical database type.
     */
    public char getPDatabaseType()
    {
        return ThinPhysicalDatabase.PROXY_TYPE;
    }
}
