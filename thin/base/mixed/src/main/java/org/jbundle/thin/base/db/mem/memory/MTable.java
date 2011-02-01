package org.jbundle.thin.base.db.mem.memory;

/**
 * @(#)MTable.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.Serializable;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.mem.base.PKeyArea;
import org.jbundle.thin.base.db.mem.base.PTable;


/**
 * The physical MemoryTable.
 * There is only one MemoryTable. All the MTable(s) must share this object.
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public class MTable extends PTable
    implements Serializable
{
	private static final long serialVersionUID = 1L;

    /**
     * Flag indicating the data has been transfered in.
     */
    private transient boolean m_bDataIn = false;
    /**
     * Flag indicating that I'm doing a close.
     */
    private transient boolean m_bInClose = false;

    /**
     * Constructor (Don't call this one).
     */
    public MTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The raw data database for this raw data table.
     * @param table The table this raw data table will supply data to.
     * @param The unique lookup key for this raw data table.
     */
    public MTable(MDatabase pDatabase, FieldList record, Object key)
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
        m_bDataIn = false;
    }
    /**
     * Get the record count.
     * Just returns the count of the default key area.
     * @return The record count (or -1 if unknown).
     */
    public int getRecordCount(FieldTable table)
    {
        int iKeyOrder = table.getRecord().getDefaultOrder();
        if (iKeyOrder == -1)
            iKeyOrder = Constants.MAIN_KEY_AREA;
        return this.getPKeyArea(iKeyOrder).getRecordCount();
    }
    /**
     * Create a new key area.
     * @param table The physical table to link this new MKeyArea to.
     * @return The new PKeyArea.
     */
    public PKeyArea makePKeyArea(FieldTable table)
    {
        return new MKeyArea(this);
    }
    /**
     * Init this object
     */
    public void open(FieldTable table) throws DBException
    {
        super.open(table);
        if (!m_bDataIn)
        {   // Import all data on first open
            m_bDataIn = true;
            this.readData(table);
        }
    }
    /**
     * Read the data into the table.
     */
    public void readData(FieldTable table) throws DBException
    {
        // Override this!
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Close this table.
     * If this is the last copy being closed, write the XML data to the file.
     * <p />Notice I flush on close, rather than free, so the record object is
     * still usable - This will create a performance problem for tasks that are
     * processing the file, but it should be rare to be using an XML file for
     * processing.
     * @param table The table.
     */
    public void close(FieldTable table)
    {
        super.close(table);
        if (m_setPTableOwners.size() == 1) if (!m_bInClose)
        {
            m_bInClose = true; // Eliminate echo
            try
            {
                this.writeData(table);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            m_bInClose = false;
        }
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
        //Override this
    }
    /**
     * Is this record read-only?
     * @return True if it is read-only.
     */
    public boolean isReadOnly()
    {
        return true;   // Override if different
    }
}
