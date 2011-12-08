/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.mem.proxy;

/**
 * @(#)SDatabase.java 1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.net.NDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteTask;


/**
 * Implement the Proxy raw data database.
 * This database is exactly the same as a MDatabase, except
 * it de-serialized data on open, and seralizes on close
 * and data is retrieved through the proxy instead of http.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class YDatabase extends NDatabase
{

    /**
     * Constructor.
     */
    public YDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param pDatabaseOwner The raw data database owner (optional).
     * @param strDBName The database name (The key for lookup).
     */
    public YDatabase(PhysicalDatabaseParent pDatabaseOwner, String strDbName)
    {
        this();
        this.init(pDatabaseOwner, strDbName, ThinPhysicalDatabase.NET_TYPE);
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
            String strTableName = this.getFilename(strLookupKey, false);
            String strCommand = Params.TABLE_PATH;
            Map<String,Object> properties = new HashMap<String,Object>();
            properties.put(DATA_TYPE, "table");
            properties.put(RECORD_CLASS, record.getClass().getName());
            properties.put(TABLE_NAME, strTableName);
            if (record.getOwner() instanceof RecordOwnerParent)
                if (((RecordOwnerParent)record.getOwner()).getTask() != null)
                    if (((RecordOwnerParent)record.getOwner()).getTask().getApplication() != null)
                        if (((RecordOwnerParent)record.getOwner()).getTask().getApplication().getLanguage(true) != null)
                        	properties.put(Params.LANGUAGE, ((RecordOwnerParent)record.getOwner()).getTask().getApplication().getLanguage(true));
            App app = null;
            if (this.getPDatabaseParent() != null)
                app = (App)this.getPDatabaseParent().getProperty(PhysicalDatabaseParent.APP);
            RemoteTask remoteTask = null;
            if (app == null)
                return super.openSerialStream(record, strLookupKey);	// Never
            else
            	remoteTask = (RemoteTask)app.getRemoteTask(null);
            Object data = remoteTask.doRemoteAction(strCommand, properties);
            ByteArrayInputStream istream = new ByteArrayInputStream((byte[])data);
            return istream;
        } catch (DBException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Write the serial table to the file.
     * Note: Net tables are read-only.
     */
    public void flushPTable(PTable pTable)
    {
    }
}
