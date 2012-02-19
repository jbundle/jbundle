/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.xml;

/**
 * @(#)XTable.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.File;
import java.io.Serializable;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.xmlutil.XmlInOut;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.mem.memory.MDatabase;
import org.jbundle.thin.base.db.mem.memory.MTable;


/**
 * The physical XmlTable.
 * an XML table is exactly the same as a MTable, except the data
 * is retrieved/stored from an XML table on open/close.
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public class XTable extends MTable
    implements Serializable
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor (Don't call this one).
     */
    public XTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The raw data database for this raw data table.
     * @param table The table this raw data table will supply data to.
     * @param The unique lookup key for this raw data table.
     */
    public XTable(MDatabase pDatabase, FieldList record, Object key)
    {
        this();
        this.init(pDatabase, record, key);
    }
    /**
     * Init this object.
     * @param database The raw data database for this raw data table.
     * @param table The table this raw data table will supply data to.
     * @param The unique lookup key for this raw data table.
     */
    public void init(MDatabase pDatabase, FieldList record, Object key)
    {
        super.init(pDatabase, record, key);
    }
    /**
     * Read the data into the table.
     */
    public void readData(FieldTable table) throws DBException
    {
        if (this.getLookupKey() instanceof String)
        {
            try
            {
                FieldList record = table.getRecord();
                String strFilePath = (String)this.getLookupKey();
                String strFilePathName = this.getPDatabase().getFilename(strFilePath, false);
                File file = new File(strFilePathName);
                if (file.exists())
                {
                    XmlInOut inOut = new XmlInOut(null, null, null);
                    boolean bSuccess = inOut.importXML(((Record)record).getTable(), strFilePathName, null);
                    inOut.free();
                    if (!bSuccess)
                    {
                        // Ignore (for now)
                    }
                    this.fixCounter(table);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Write the data.
     * If this is the last copy being closed, write the XML data to the file.
     * <p />Notice I flush on close, rather than free, so the record object is
     * still usable - This will create a performance problem for tasks that are
     * processing the file, but it should be rare to be using an XML file for
     * processing.
     * @param table The table.
     */
    public void writeData(FieldTable table) throws Exception
    {
        if (this.getLookupKey() instanceof String)
        {
            FieldList record = table.getRecord();
            /** Write a tree object, and all the subtrees */
            String strFilePath = (String)this.getLookupKey();
            String strFilename = this.getPDatabase().getFilename(strFilePath, true);
            if (record != null) if (((Record)record).isInFree())
            {
                // Read the XML records
                XmlInOut inOut = new XmlInOut(null, null, null);
                inOut.exportXML(((Record)record).getTable(), strFilename);
                inOut.free();
            }
        }
    }
    /**
     * Is this record read-only?
     * @return True if it is read-only.
     */
    public boolean isReadOnly()
    {
        return false;
    }
}
