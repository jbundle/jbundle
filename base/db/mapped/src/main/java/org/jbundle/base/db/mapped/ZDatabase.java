/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.mapped;

/**
 * @(#)XDatabase.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.memory.MDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;

/**
 * Implement the Vector database.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ZDatabase extends MDatabase
{

    /**
     * Constructor.
     */
    public ZDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param pDatabaseOwner The raw data database owner (optional).
     * @param strDBName The database name (The key for lookup).
     */
    public ZDatabase(PhysicalDatabaseParent pDatabaseOwner, String strDbName)
    {
        this();
        this.init(pDatabaseOwner, strDbName, ThinPhysicalDatabase.MAPPED_TYPE);
    }
    /**
     * Create a raw data table for this table.
     * @param table The table to create a raw data table for.
     * @param key The lookup key that will be passed (on initialization) to the new raw data table.
     * @return The new raw data table.
     */
    public PTable makeNewPTable(FieldList record, Object key)
    {
        ZTable xTable = new ZTable(this, record, key);  // New empty table
        return xTable;
    }
}
