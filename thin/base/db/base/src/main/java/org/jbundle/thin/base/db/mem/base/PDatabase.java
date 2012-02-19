/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.mem.base;

/**
 * @(#)PDatabase.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.mem.GridMemoryFieldTable;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabaseOwner;


/**
 * Implement the Raw data database.
 * If you want tables shared between all tasks in an environment,
 * you need to specify a shared raw data PDatabaseOwner, so the tables will be
 * looked up correctly.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class PDatabase extends Object
    implements ThinPhysicalDatabase
{
    /**
     * The raw data database owner.
     */
    protected PhysicalDatabaseParent m_pDatabaseParent = null;
    /**
     * List of all the tables.
     */
    protected Hashtable<Object,PTable> m_htTableList = null;
    /**
     * 
     */
    protected Set<ThinPhysicalDatabaseOwner> m_setPDatabaseOwners = new HashSet<ThinPhysicalDatabaseOwner>();
    /**
     * This database name (for lookup in the map).
     */
    protected String m_strDBName = null;
    /**
     * Filename path separator.
     */
    public static char PATH_SEPARATOR = '/';
    /**
     * Filename extension separator.
     */
    public static char EXTENSION_SEPARATOR = '.';
    /**
     * The extension to use for the persistent store.
     */
    protected String m_strFileExtension = "ser";
    /**
     * The prefix to use for the persistent store.
     */
    protected String m_strFilePrefix = "ser";
    /**
     * The database type.
     */
    protected char m_charPDatabaseType = ThinPhysicalDatabase.PHYSICAL_TYPE;   // Never
    
    /**
     * Constructor.
     */
    public PDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param pDatabaseOwner The raw data database owner (optional).
     * @param strDBName The database name (The key for lookup).
     */
    public PDatabase(PhysicalDatabaseParent pDatabaseOwner, String strDbName)
    {
        this();
        this.init(pDatabaseOwner, strDbName, ThinPhysicalDatabase.PHYSICAL_TYPE);
    }
    /**
     * Constructor.
     * @param pDatabaseOwner The raw data database owner (optional).
     * @param strDBName The database name (The key for lookup).
     * @param charPDatabaseType The database type.
     */
    public void init(PhysicalDatabaseParent pDatabaseOwner, String strDbName, char charPDatabaseType)
    {
        m_htTableList = new Hashtable<Object,PTable>();
        m_strDBName = strDbName;
        m_charPDatabaseType = charPDatabaseType;
        this.setPDatabaseParent(pDatabaseOwner);
    }
    /**
     * Free the objects.
     * There is no need to call this, as the raw tables will automatically be removed
     * when their physical tables are freed.
     */
    public void free()
    {
        this.setPDatabaseParent(null); // Remove from database
        
        Object[] keys = m_htTableList.keySet().toArray();
        for (int i = 0; i < keys.length; i++)
        { 
            Object key = keys[i];
            PTable pTable = m_htTableList.get(key);
            if (pTable != null)
                pTable.free();      // This can be dangerous if the PhysicalTables still have references to the PTables
        } 
        m_htTableList.clear();
        m_htTableList = null;
        this.close();
    } 
    /**
     * Set the raw database owner.
     * Automatically adds/removes this entry.
     * @param The raw database owner.
     */
    public void setPDatabaseParent(PhysicalDatabaseParent pDatabaseParent)
    {
        if (m_pDatabaseParent != null)
            m_pDatabaseParent.removePDatabase(this);
        m_pDatabaseParent = pDatabaseParent;
        if (m_pDatabaseParent != null)
            m_pDatabaseParent.addPDatabase(this);
    }
    /**
     * Get the db owner.
     * @return My database owner.
     */
    public PhysicalDatabaseParent getPDatabaseParent()
    {
        return m_pDatabaseParent;
    }
    /**
     * Get the db name.
     * The db name is my key if a Pdatabase owner is specified.
     * @return The database name.
     */
    public String getDatabaseName()
    {
        return m_strDBName;
    }
    /**
     * Get the physical database type of this database.
     * @param iDBType
     * @return The one letter type of physical database.
     */
    public char getPDatabaseType()
    {
        return m_charPDatabaseType;
    }
    /**
     * Get the physical db type from the record db type.
     * @param iDatabaseType
     * @return
     */
    public static char getPDBTypeFromDBType(int iDatabaseType)
    {
        char strPDBType = ThinPhysicalDatabase.MEMORY_TYPE;
        if ((iDatabaseType & Constants.MAPPED) != 0)
            ;
        iDatabaseType = iDatabaseType & Constants.TABLE_TYPE_MASK;
        if (iDatabaseType == Constants.LOCAL)
            strPDBType = ThinPhysicalDatabase.NET_TYPE;  // Never asked
        if (iDatabaseType == Constants.REMOTE)
            strPDBType = ThinPhysicalDatabase.NET_TYPE;  // Never asked
        if (iDatabaseType == Constants.TABLE)
            strPDBType = ThinPhysicalDatabase.NET_TYPE;
        return strPDBType;
    }
    /**
     * Get the database key that the database parent has this filed under.
     * @return The database key.
     */
    public String getDatabaseKey()
    {
        String strKey = this.getPDatabaseType() + ":" + this.getDatabaseName();
        return strKey;
    }

    /**
     * Close the raw data database (usually overridden).
     */
    public void close()
    {
    } 
    /**
     * Open the raw data database
     */
    public void open()
    {
    }
    /**
     * List of all the tables.
     */
    protected Hashtable<Object,PTable> getTableList()
    {
        return m_htTableList;
    }
    /**
     * Add this raw data table to my table list.
     * @param physicalTable The raw data table to add.
     */
    public void addPTable(PTable physicalTable)
    {
        Object strKey = physicalTable.getLookupKey();
        m_htTableList.put(strKey, physicalTable);
        this.getPDatabaseParent().addTableToCache(physicalTable);   // If cache is on
    }
    /**
     * Add this physical database owner.
     * @param pDatabaseOwner
     * @return
     */
    public int addPDatabaseOwner(ThinPhysicalDatabaseOwner pDatabaseOwner)
    {
        m_setPDatabaseOwners.add(pDatabaseOwner);
        return m_setPDatabaseOwners.size();
    }
    /**
     * Remove this physical database owner.
     * @param pDatabaseOwner
     * @return
     */
    public int removePDatabaseOwner(ThinPhysicalDatabaseOwner pDatabaseOwner, boolean bFreeIfEmpty)
    {
        m_setPDatabaseOwners.remove(pDatabaseOwner);
        if (bFreeIfEmpty)
            if (m_setPDatabaseOwners.size() == 0)
                if (m_htTableList.size() == 0)
        {
            this.free();
            return 0;
        }
        return m_setPDatabaseOwners.size();
    }
    /**
     * Get the physical table that matches this BaseTable and create it if it doesn't exist.
     * Note: If the bCreateIfNotFound flag was set, create the new table or bump the use count.
     * @param table The table to create a raw data table from.
     * @return The raw data table (creates a new one if it doesn't already exist).
     */
    public PTable getPTable(FieldList record, boolean bCreateIfNotFound)
    {
        return this.getPTable(record, bCreateIfNotFound, false);
    }
    /**
     * Get the physical table that matches this BaseTable and create it if it doesn't exist.
     * Note: If the bCreateIfNotFound flag was set, create the new table or bump the use count.
     * @param table The table to create a raw data table from.
     * @return The raw data table (creates a new one if it doesn't already exist).
     */
    public synchronized PTable getPTable(FieldList record, boolean bCreateIfNotFound, boolean  bEnableGridAccess)
    {
        Object objKey = this.generateKey(record);
        PTable physicalTable = (PTable)m_htTableList.get(objKey);
        if (bCreateIfNotFound)
            if (physicalTable == null)
                physicalTable = this.makeNewPTable(record, objKey);
        if (bEnableGridAccess)
            new GridMemoryFieldTable(record, physicalTable, null);
        return physicalTable;
    }
    /**
     * Get the unique key used to find this record's physical table in the hash table.
     * Query records and UNSHARABLE_MEMORY are not shared, so their key is the record object.
     * <p />Note: If the key is a string, it is a unique file path.
     * @param The record to generate a unique key for.
     * @return The unique object to put in a hash table.
     */
    public Object generateKey(FieldList record)
    {
        if (record.isQueryRecord())
            return record;      // These can't be shared
        else if ((record.getDatabaseType() & Constants.TABLE_TYPE_MASK) == Constants.UNSHAREABLE_MEMORY)   // Special sharable temp memory file
            return record;      // These can't be shared
        else
            return this.getUniqueFilePath(record);  // These can be shared
    }
    /**
     * Remove this raw data table from this record.
     * <p/>Do not call this method, it is called automatically from rawtable.free().
     * @param table The table to create a raw data table from.
     * @return True if successfully removed.
     */
    public boolean removePTable(PTable pTable)
    {
        Object strKey = pTable.getLookupKey();
        Object obj = m_htTableList.get(strKey);
        if (obj == null)
            return false;
        this.flushPTable(pTable); // Write it to the data store (override must implement).
        return (m_htTableList.remove(strKey) != null);
    }
    /**
     * Get a unique file path for this file.
     * The unique file path is: database/filename.
     * This is used to create a unique key AND to assemble a pathname to
     * the physical data for persistent implementations.
     * @param table The table to generate a filename from.
     * @param The file extension.
     * @returns The full file path.
     */
    public String getUniqueFilePath(FieldList record)
    {
        return this.getDatabaseName() + PATH_SEPARATOR + record.getTableNames(false);
    }
    /**
     * Create a raw data table for this table.
     * Override this!
     * @param table The table to create a raw data table for.
     * @param key The lookup key that will be passed (on initialization) to the new raw data table.
     * @return The new raw data table.
     */
    public PTable makeNewPTable(FieldList record, Object key)
    {
        return null;    //new PTable(this, table, key);
    }
    /**
     * Write this table to the physical file.
     * Note: This is not implemented in PDatabase (You must override).
     * @param pTable The raw data table.
     */
    public void flushPTable(PTable pTable)
    {
    }
    /**
     * Set the filename prefix and extension.
     * The file path ends up being prefix/database/filename.extension.
     * @param strPrefix The path prefix.
     * @param strExtension The filename suffix.
     */
    public void setFilePath(String strPrefix, String strExtension)
    {
        m_strFileExtension = strExtension;
        m_strFilePrefix = strPrefix;
    }
    /**
     * Get the physical table that matches this BaseTable and create it if it doesn't exist.
     * This is just a utility for those implementations that use a physical file.
     * Open the file and prepare to read objects from it.
     * This method also creates the entire directory structure, but does not create
     * the file. The file name is: extension/database/filename.extension.
     * @param table The table to generate a filename from.
     * @param The file extension.
     * @param bCreatePath If true, create the file path (not the file name).
     * @returns The full file path.
     */
    public String getFilename(String strFilePath, boolean bCreatePath)
    {
        String strFilename = null;
        try
        {
            if (bCreatePath)
            {
                strFilename = m_strFilePrefix;
                File file = new File(strFilename);
                if (!file.isDirectory())
                {
                    file.mkdir();
                }
                int iPos = strFilePath.lastIndexOf(PATH_SEPARATOR);
                if (iPos != -1) if (iPos < strFilePath.length())
                {
                    strFilename = strFilename + PATH_SEPARATOR + strFilePath.substring(0, iPos);    // Cut off file name
                    file = new File(strFilename);
                    if (!file.isDirectory())
                    {
                        file.mkdir();
                    }
                }
            }
            strFilename = m_strFilePrefix + PATH_SEPARATOR + strFilePath + EXTENSION_SEPARATOR + m_strFileExtension;
             
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strFilename;
    }
}
