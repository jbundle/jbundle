/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.xml;

/**
 * @(#)XDatabase.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
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
public class XDatabase extends MDatabase
{
    /**
     * The extension to use for the persistent store.
     */
    public static final String XFILE_EXTENSION = "xml";

    /**
     * Constructor.
     */
    public XDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param pDatabaseOwner The raw data database owner (optional).
     * @param strDBName The database name (The key for lookup).
     */
    public XDatabase(PhysicalDatabaseParent pDatabaseOwner, String strDbName)
    {
        this();
        this.init(pDatabaseOwner, strDbName, ThinPhysicalDatabase.XML_TYPE);
        this.setFilePath(XFILE_EXTENSION, XFILE_EXTENSION);
    }
    /**
     * Create a raw data table for this table.
     * @param table The table to create a raw data table for.
     * @param key The lookup key that will be passed (on initialization) to the new raw data table.
     * @return The new raw data table.
     */
    public PTable makeNewPTable(FieldList record, Object key)
    {
        XTable xTable = new XTable(this, record, key);  // New empty table
        return xTable;
    }
}
