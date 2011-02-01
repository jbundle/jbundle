package org.jbundle.base.db.mapped;

/**
 * @(#)XTable.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.Serializable;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.util.DBConstants;
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
public class ZTable extends MTable
    implements Serializable
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor (Don't call this one).
     */
    public ZTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The raw data database for this raw data table.
     * @param table The table this raw data table will supply data to.
     * @param The unique lookup key for this raw data table.
     */
    public ZTable(MDatabase pDatabase, FieldList record, Object key)
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
            FieldList record = table.getRecord();
            String strTableName = (String)this.getLookupKey();
            String strDBName = null;
            if (strTableName != null)
            {
                if (strTableName.lastIndexOf('/') != -1)
                {
                    strDBName = strTableName.substring(0, strTableName.lastIndexOf('/'));
                    if (strDBName.lastIndexOf('/') != -1)
                        strDBName = strDBName.substring(strDBName.lastIndexOf('/') + 1);
                    strTableName = strTableName.substring(strTableName.lastIndexOf('/') + 1);
                    if (strTableName.indexOf('.') != -1)
                        strTableName = strTableName.substring(0, strTableName.indexOf('.'));
                }
            }

            String strThickRecordClass = record.getClass().getName();
            
            RecordOwner recordOwner = ((Record)record).getRecordOwner();
            BaseDatabase database = recordOwner.getDatabase(record.getDatabaseName(), record.getDatabaseType(), null);
            Record recSource = Record.makeRecordFromClassName(strThickRecordClass, recordOwner, false, true);
            if (recSource == null)
                return;
            if (strTableName != null)
                if (strTableName.length() > 0)
                    if ((!strTableName.equalsIgnoreCase(recSource.getTableNames(false))))
                        if ((recSource.getDatabaseType() & DBConstants.MAPPED) != 0)
                            recSource.setTableNames(strTableName);
            recSource.setTable(database.makeTable(recSource));
            recSource.init(recordOwner);

            recSource.populateThinTable(record, true);
            
            recSource.free();
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
//?            String strFilePath = (String)this.getLookupKey();
//?            String strFilename = this.getPDatabase().getFilename(strFilePath, true);
            if (record != null) if (((Record)record).isInFree())
            {
            // Currently, this is read-only
            }
        }
    }
    /**
     * Is this record read-only?
     * @return True if it is read-only.
     */
    public boolean isReadOnly()
    {
        return true;   // For now
    }
}