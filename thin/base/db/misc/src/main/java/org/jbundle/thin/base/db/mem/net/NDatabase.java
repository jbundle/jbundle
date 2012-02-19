/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.mem.net;

/**
 * @(#)SDatabase.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jbundle.model.App;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.serial.SDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;


/**
 * Implement the Serial raw data database.
 * This database is exactly the same as a MDatabase, except
 * it de-serialized data on open, and seralizes on close.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class NDatabase extends SDatabase
{
    public static final String DATA_TYPE = "datatype";
    public static final String RECORD_CLASS = "recordclass";
    public static final String TABLE_NAME = "tablename";

    /**
     * Constructor.
     */
    public NDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param pDatabaseOwner The raw data database owner (optional).
     * @param strDBName The database name (The key for lookup).
     */
    public NDatabase(PhysicalDatabaseParent pDatabaseOwner, String strDbName)
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
            String strFilePathName = '/' + Params.TABLE_PATH;
            strFilePathName = Util.addURLParam(strFilePathName, DATA_TYPE, "table");
            strFilePathName = Util.addURLParam(strFilePathName, RECORD_CLASS, record.getClass().getName());
            strFilePathName = Util.addURLParam(strFilePathName, TABLE_NAME, strTableName);
            if (record.getOwner() instanceof RecordOwnerParent)
                if (((RecordOwnerParent)record.getOwner()).getTask() != null)
                    if (((RecordOwnerParent)record.getOwner()).getTask().getApplication() != null)
                        if (((RecordOwnerParent)record.getOwner()).getTask().getApplication().getLanguage(true) != null)
                            strFilePathName = Util.addURLParam(strFilePathName, Params.LANGUAGE, ((RecordOwnerParent)record.getOwner()).getTask().getApplication().getLanguage(true));
            App app = null;
            if (this.getPDatabaseParent() != null)
                app = (App)this.getPDatabaseParent().getProperty(PhysicalDatabaseParent.APP);
            URL url = null;
            if (app == null)
            {
                String strFilePrefix = "http://";
                strFilePrefix += "localhost/" + Constants.DEFAULT_SERVLET;   // Never
                strFilePathName = strFilePrefix + strFilePathName;
                url = new URL(strFilePathName);
            }
            else
            {
                String strServletName = app.getProperty(Params.SERVLET);
                if ((strServletName == null) || (strServletName.length() == 0))
                    if ((app.getProperty(Params.CODEBASE) !=  null) && (app.getProperty(Params.CODEBASE).length() > 0))
                        if (app.getProperty(Params.CODEBASE).indexOf('/') != -1)
                            strServletName = app.getProperty(Params.CODEBASE).substring(app.getProperty(Params.CODEBASE).lastIndexOf('/') + 1) + Constants.DEFAULT_SERVLET;
                if ((strServletName == null) || (strServletName.length() == 0))
                    strServletName = Constants.DEFAULT_SERVLET;
                strFilePathName = strServletName + strFilePathName;

                 url = app.getResourceURL(strFilePathName, null);
            }
            return url.openStream();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
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
