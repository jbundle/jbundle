/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.mem.base;

/**
 * @(#)KeyArea.java   1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.Serializable;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.KeyAreaInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;


/**
 * Definition of this raw data key area.
 * An KeyArea describes a particular key area (fields and order)
 *
 * @version 1.0.0
 * @author Don Corley
 */
public class PKeyArea extends Object
    implements Serializable
{
	private static final long serialVersionUID = 1L;

    /**
     * The parent raw data table.
     */
    protected PTable m_VTable = null;

    /**
     * Constructor.
     */
    public PKeyArea()
    {
        super();
    }
    /**
     * Constructor.
     * @param pTable The raw data table that this raw data key area is being added to.
     */
    public PKeyArea(PTable pTable)
    {
        this();
        this.init(pTable);
    }
    /**
     * Initialize the class.
     * @param pTable The raw data table that this raw data key area is being added to.
     */
    public void init(PTable pTable)
    {
        m_VTable = pTable;
        pTable.addPKeyArea(this);
    }
    /**
     * Release the Objects in this KeyArea.
     */
    public void free()
    {
        m_VTable.removePKeyArea(this);
        m_VTable = null;
    }
    /**
     * Compare these two keys and return the compare result.
     * @param areaDesc The area that the key to compare is in.
     * @param strSeekSign The seek sign.
     * @param table The table.
     * @param keyArea The table's key area.
     * @return The compare result (-1, 0, or 1).
     */
    public int compareKeys(int iAreaDesc, String strSeekSign, FieldTable table, KeyAreaInfo keyArea)
    {
        int iCompareValue = keyArea.compareKeys(iAreaDesc);
        return iCompareValue;
    }
    /**
     * Am I still pointing at this buffer?
     * Override to make this more efficient.
     * @param buffer The buffer to compare.
     * @exception DBException File exception.
     */
    public boolean atCurrent(BaseBuffer buffer) throws DBException
    {
        return false;
    }
    /**
     * Delete the key from this buffer.
     * @param table The basetable.
     * @param keyArea The basetable's key area.
     * @param buffer The buffer to compare.
     * @exception DBException File exception.
     */
    public void doRemove(FieldTable table, KeyAreaInfo keyArea, BaseBuffer buffer) throws DBException
    {
        if (!this.atCurrent(buffer))
        {
            buffer = this.doSeek("==", table, keyArea);
            if (buffer == null)
                throw new DBException(Constants.FILE_INCONSISTENCY);
        }
        this.removeCurrent(buffer);
    }
    /**
     * Remove the entry at the current location.
     * @param buffer The buffer to remove.
     * @exception DBException File exception.
     */
    public void removeCurrent(BaseBuffer buffer) throws DBException
    {
        // Must override
    }
    /**
     * Move the position of the record.
     * @param iRelPosition The number and order of record to read through.
     * @param table The basetable.
     * @param keyArea The key area.
     * @exception DBException File exception.
     */
    public BaseBuffer doMove(int iRelPosition, FieldTable table, KeyAreaInfo keyArea) throws DBException
    {
        return null;
    }
    /**
     * Read the record that matches this record's temp key area.<p>
     * WARNING - This method changes the current record's buffer.
     *  @param strSeekSign - Seek sign:<p>
     *  <pre>
     *  "=" - Look for the first match.
     *  "==" - Look for an exact match (On non-unique keys, must match primary key also).
     *  ">" - Look for the first record greater than this.
     *  ">=" - Look for the first record greater than or equal to this.
     *  "<" - Look for the first record less than or equal to this.
     *  "<=" - Look for the first record less than or equal to this.
     *  </pre>
     *      returns: success/failure (true/false).
     * @param table The basetable.
     * @param keyArea The key area.
     * @exception DBException File exception.
     */
    public BaseBuffer doSeek(String strSeekSign, FieldTable table, KeyAreaInfo keyArea) throws DBException
    {
        return null;
    }
    /**
     * Add this record (Always called from the record class).
     * @param table The basetable.
     * @param keyArea The key area.
     * @param bufferNew The buffer to add.
     * @exception DBException File exception.
     */
    public void doWrite(FieldTable table, KeyAreaInfo keyArea, BaseBuffer bufferNew) throws DBException
    {
        BaseBuffer bufferSeek = this.doSeek("==", table, keyArea);
        if (bufferSeek != null) // if (this.GetUniqueKeyCode() == DBConstants.UNIQUE) // Success means this one already exists
            throw new DBException(Constants.DUPLICATE_KEY);
        this.insertCurrent(table, keyArea, bufferNew, +1);  // Point to next HIGHER key
    }
    /**
     * Insert this record at the current location.
     * @param table The basetable.
     * @param keyArea The key area.
     * @param bufferNew The buffer to add.
     * @param iRelPosition relative position to add the record.
     * @exception DBException File exception.
     */
    public void insertCurrent(FieldTable table, KeyAreaInfo keyArea, BaseBuffer bufferNew, int iRelPosition) throws DBException
    {
        // Must override
    }
    /**
     * Get the record count.
     * @return The record count (or -1 if unknown).
     */
    public int getRecordCount()
    {
        return -1;
    }
    /**
     * Get the physical table that goes with this key.
     * @return The raw data table.
     */
    public PTable getPTable()
    {
        return m_VTable;
    }
}
