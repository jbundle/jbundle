package org.jbundle.base.db.xml;

/**
 * @(#)XmlDatabase.java   1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.db.memory.MemoryDatabase;
import org.jbundle.base.db.physical.PhysicalTable;
import org.jbundle.base.util.DatabaseOwner;
import org.jbundle.thin.base.db.ThinPhysicalDatabase;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;


/**
 * XmlDatabase - Implement the Xml database.
 *  The Xml database is EXACTLY the same as the Vector Database, except when it
 *  closes, data is written to the XML file. When it opens, data is extracted from
 *  the XML file.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class XmlDatabase extends MemoryDatabase
{
    /**
     * Constructor
     */
    public XmlDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public XmlDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)
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
        return new XmlTable(this, record);
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
        return new XDatabase(pDatabaseOwner, strDbName);
    }
    /**
     * Get the physical database type.
     */
    public char getPDatabaseType()
    {
        return ThinPhysicalDatabase.XML_TYPE;
    }
}
