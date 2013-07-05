/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.physical;

/**
 * @(#)PhysicalDatabase.java    1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.PassThruTable;
import org.jbundle.base.db.QueryTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.util.Environment;
import org.jbundle.model.DBException;
import org.jbundle.model.db.DatabaseOwner;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.net.NDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabaseOwner;


/**
 * Implement the Physical database.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class PhysicalDatabase extends BaseDatabase
    implements ThinPhysicalDatabaseOwner
{
    /**
     * The database for the physical tables.
     */
    protected PDatabase m_pDatabase = null;

    /**
     * Constructor.
     */
    public PhysicalDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public PhysicalDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)
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
        m_pDatabase = null;
    }
    /**
     * Free the objects.
     */
    public void free()
    {
        super.free(); // Do any inherited
        if (m_pDatabase != null)
        {
            this.close();
            m_pDatabase.removePDatabaseOwner(this, true);
        }
        m_pDatabase = null;
    } 
    /**
     * Open the database.
     * If this is the first time, a raw data database is created.
     * @exception DBException.
     */
    public void open() throws DBException
    {
        if (m_pDatabase == null)
        {
            PhysicalDatabaseParent pDBParent = null;
            Environment env = (Environment)this.getDatabaseOwner().getEnvironment();
            if (env != null)
                pDBParent = (PhysicalDatabaseParent)env.getPDatabaseParent(mapDBParentProperties, true);
            String databaseName = this.getDatabaseName(true);
            if (databaseName.endsWith(BaseDatabase.SHARED_SUFFIX))
                databaseName = databaseName.substring(0, databaseName.length() - BaseDatabase.SHARED_SUFFIX.length());
            else if (databaseName.endsWith(BaseDatabase.USER_SUFFIX))
                databaseName = databaseName.substring(0, databaseName.length() - BaseDatabase.USER_SUFFIX.length());
            char strPDatabaseType = this.getPDatabaseType();
            m_pDatabase = pDBParent.getPDatabase(databaseName, strPDatabaseType, false);
            if (m_pDatabase == null)
                m_pDatabase = this.makePDatabase(pDBParent, databaseName);
            m_pDatabase.addPDatabaseOwner(this);
        }
        m_pDatabase.open();
        
        super.open(); // Do any inherited
    }
    public static Map<String,Object> mapDBParentProperties = new Hashtable<String,Object>();
    static
    {
        mapDBParentProperties.put(PhysicalDatabaseParent.TIME, "-1");   // Default time
        mapDBParentProperties.put(PhysicalDatabaseParent.DBCLASS, NDatabase.class.getName());   // Hybrid (net) database.        
    }
    /**
     * Close the physical database (usually overridden).
     */
    public void close()
    {
        super.close();  // Do any inherited
        if (m_pDatabase != null)
            m_pDatabase.close();
    }
    /**
     * Set the ptable that I am an owner of.
     * @param pTable
     */
    public void setPDatabase(PDatabase pDatabase)
    {
        m_pDatabase = pDatabase;
    }
    /**
     * Make a table for this database.
     * @param record The record to make a table for.
     * @return BaseTable The new table.
     */
    public BaseTable doMakeTable(Record record)
    {
        BaseTable table = null;
        boolean bIsQueryRecord = record.isQueryRecord();
        if (m_pDatabase == null)
        {
            try   {
                this.open();
            } catch (DBException ex)    {
                return null;        // No database
            }
        }
        if (bIsQueryRecord)
        { // Physical Tables cannot process SQL queries, so use QueryTable!
            PassThruTable passThruTable = new QueryTable(this, record);
            passThruTable.addTable(record.getRecordlistAt(0).getTable());
            return passThruTable;
        }
        table = this.makePhysicalTable(record);
        return table;
    }
    /**
     * Get the raw data database.
     * @return The raw data database.
     */
    public PDatabase getPDatabase()
    {
        return m_pDatabase;
    }
    /**
     * Create the raw data database.
     * Always OVERRIDE THIS.
     * @param strDbName The database name.
     * @param env Environment (optional).
     * @param strDatabaseType The database type.
     * @return The raw data database.
     */
    public PDatabase makePDatabase(PhysicalDatabaseParent pDatabaseOwner, String strDbName)
    {
        return new PDatabase(pDatabaseOwner, strDbName);
    }
    /**
     * Get the physical database type.
     */
    public char getPDatabaseType()
    {
        return ThinPhysicalDatabase.PHYSICAL_TYPE;
    }
    /**
     * Make a table for this database.
     * Always OVERRIDE THIS.
     * @param record The record to make the physical table for.
     * @return The physical table.
     */
    public PhysicalTable makePhysicalTable(Record record)
    {
        return new PhysicalTable(this, record);
    }
}
