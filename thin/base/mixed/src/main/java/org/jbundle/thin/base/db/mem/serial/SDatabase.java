package org.jbundle.thin.base.db.mem.serial;

/**
 * @(#)SDatabase.java 1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.ThinPhysicalDatabase;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.memory.MDatabase;
import org.jbundle.thin.base.db.mem.memory.MTable;


/**
 * Implement the Serial raw data database.
 * This database is exactly the same as a MDatabase, except
 * it de-serialized data on open, and seralizes on close.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SDatabase extends MDatabase
{
    /**
     * Extension and prefix for SDatabase tables.
     */
    public static final String SFILE_EXTENSION = "ser";
    /**
     * Constructor.
     */
    public SDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param pDatabaseOwner The raw data database owner (optional).
     * @param strDBName The database name (The key for lookup).
     */
    public SDatabase(PhysicalDatabaseParent pDatabaseOwner, String strDbName)
    {
        this();
        this.init(pDatabaseOwner, strDbName, ThinPhysicalDatabase.SERIAL_TYPE);
        this.setFilePath(SFILE_EXTENSION, SFILE_EXTENSION);
    }
    /**
     * Create a raw data table for this table.
     * @param table The table to create a raw data table for.
     * @param key The lookup key that will be passed (on initialization) to the new raw data table.
     * @return The new raw data table.
     */
    public PTable makeNewPTable(FieldList record, Object lookupKey)
    {
        InputStream istream = null;
        try
        {
            if (lookupKey instanceof String)
                istream = this.openSerialStream(record, (String)lookupKey);
            if (istream != null)
            {
                ObjectInputStream q = new ObjectInputStream(istream);
                
                MTable sTable = (MTable)q.readObject();
                q.close();      // Is this necessary?
                sTable.init(this, record, lookupKey);
                return sTable;
            }
             
        } catch (Exception ex) {
        	System.out.println("Error " + ex.getMessage() + " on read serial file: " + lookupKey);
        	//+Util.getLogger().warning("Error " + ex.getMessage() + " on read serial file: " + lookupKey);
        } finally {
            try {
                if (istream != null)
                    istream.close();
            } catch (IOException e) {
            }            
        }
        return new MTable(this, record, lookupKey);   // New empty table
    }
    /**
     * Get the physical table that matches this BaseTable and return an Input
     * stream to it if it exists.
     * @param table The table to get the serial stream for (named ser/dbname/tablename.ser).
     * @return The FileInputStream or null if it doesn't exist.
     */
    public InputStream openSerialStream(FieldList record, String strLookupKey)
    {
        try
        {
            String strFilePathName = this.getFilename(strLookupKey, false);
            File file = new File(strFilePathName);
            if (file.exists())
            {
                return new FileInputStream(strFilePathName);
            }
             
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Write the serial table to the file.
     * @param pTable the raw data table.
     */
    public void flushPTable(PTable pTable)
    {
        if (pTable.getLookupKey() instanceof String)
        {
            try   {
                String strFilePath = (String)pTable.getLookupKey();
                String strFilename = this.getFilename(strFilePath, true);
                FileOutputStream ostream = new FileOutputStream(strFilename);
                ObjectOutputStream p = new ObjectOutputStream(ostream);

                p.writeObject(pTable); // Write the tree to the stream.
                p.flush();
                p.close();
                ostream.close();    // close the file.
            } catch (IOException ex)    {
                ex.printStackTrace();
            }
        }
    }
}
