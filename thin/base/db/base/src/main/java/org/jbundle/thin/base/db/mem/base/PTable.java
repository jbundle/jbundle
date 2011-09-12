/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.mem.base;

/**
 * @(#)VTable.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.KeyAreaInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.model.ThinPhysicalTable;
import org.jbundle.thin.base.db.model.ThinPhysicalTableOwner;


/**
 * The Physical Table.
 * There is only one PhysicalTable. All the PTable(s) must share this object.
 * This is the base for all raw data tables, it has everything you need except
 * a place to store the raw data.
 *
 * @version 1.0.0
 * @author Don Corley
 */
public class PTable extends Object
    implements Serializable, ThinPhysicalTable
{
	private static final long serialVersionUID = 1L;

    /**
     * The physical database for this table.
     */
    protected transient PDatabase m_PDatabase = null;
    /**
     * For (optional) auto-counter field.
     */
    protected int m_iCounter = 1;
    /**
     * The lookup key for this table.
     */
    protected transient Object m_lookupKey = null;
    /**
     * The list of raw data key areas.
     */
    protected Vector<PKeyArea> m_VKeyList = null;
    /**
     * Timestamp this table was last closed (for cache flushing).
     */
    protected transient long m_lTimeLastUsed = 0;
    /**
     * 
     */
    protected transient Set<ThinPhysicalTableOwner> m_setPTableOwners = null;

    /**
     * Constructor (Don't call this one).
     */
    public PTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The raw data database for this raw data table.
     * @param table The table this raw data table will supply data to.
     * @param lookupKey The unique lookup key for this raw data table.
     */
    public PTable(PDatabase database, FieldList record, Object lookupKey)
    {
        this();
        this.init(database, record, lookupKey);
    }
    /**
     * Init this object.
     * Note: The auto-counter field is not initialized here, as in the serialized
     * version it is read from the data stream.
     * @param database The raw data database for this raw data table.
     * @param table The table this raw data table will supply data to.
     * @param lookupKey The unique lookup key for this raw data table.
     */
    public void init(PDatabase database, FieldList record, Object lookupKey)
    {
        m_PDatabase = database;
        m_lookupKey = lookupKey;
        m_setPTableOwners = new HashSet<ThinPhysicalTableOwner>();
        database.addPTable(this);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        if (m_PDatabase != null)
            m_PDatabase.removePTable(this);
        m_PDatabase = null;

        if (m_VKeyList != null)
        {
            for (int i = m_VKeyList.size() - 1 ; i >= 0 ;i--) {
                PKeyArea vKeyArea = (PKeyArea)m_VKeyList.elementAt(i);
                vKeyArea.free();
            } 
            m_VKeyList.removeAllElements();
        }
        m_VKeyList = null;
        
        if (m_setPTableOwners != null)
        {
            for (ThinPhysicalTableOwner owner : m_setPTableOwners)
            {
                this.removePTableOwner(owner, false);
            }
        }
    }
    /**
     * Bump the use count.
     * This doesn't have to be synchronized because getPTable in PDatabase is.
     * @param pTableOwner The table owner to add.
     * @return The new use count.
     */
    public int addPTableOwner(ThinPhysicalTableOwner pTableOwner)
    {
        if (pTableOwner != null)
        {
            m_lTimeLastUsed = System.currentTimeMillis();
            m_setPTableOwners.add(pTableOwner);
            pTableOwner.setPTable(this);
        }
        return m_setPTableOwners.size();
    }
    /**
     * Free this table if it is no longer being used.
     * @param pTableOwner The table owner to remove.
     */
    public synchronized int removePTableOwner(ThinPhysicalTableOwner pTableOwner, boolean bFreeIfEmpty)
    {
        if (pTableOwner != null)
        {
            m_setPTableOwners.remove(pTableOwner);
            pTableOwner.setPTable(null);
        }
        if (m_setPTableOwners.size() == 0)
            if (bFreeIfEmpty)
        {
            this.free();
            return 0;
        }
        m_lTimeLastUsed = System.currentTimeMillis();
        return m_setPTableOwners.size();
    }
    /**
     * Set the raw data Database for this raw data table.
     * Remember to call vDatabase.addPTable(this).
     * @param vDatabase The raw data database to set.
     */
    public void setPDatabase(PDatabase vDatabase)
    {
        m_PDatabase = vDatabase;
    }
    /**
     * Get the raw data Database for this raw data table.
     * @return The raw data database that owns me.
     */
    public PDatabase getPDatabase()
    {
        return m_PDatabase;
    }
    /**
     * Get the key in the VDatabase lookup table.
     * @return My unique key.
     */
    public Object getLookupKey()
    {
        return m_lookupKey;
    }
    /**
     * Get the raw data key area.
     * @param The key area to retrieve.
     * @return The raw data key area.
     */
    public PKeyArea getPKeyArea(int iKeyNumber)
    {
        return m_VKeyList.elementAt(iKeyNumber);
    }
    /**
     * Add this raw data key area to this raw data record.
     * @param vKeyArea The raw data key area to add.
     */
    public void addPKeyArea(PKeyArea vKeyArea)
    {
        m_VKeyList.addElement(vKeyArea);
    }
    /**
     * Remove this raw data key area to this raw data record.
     * @param vKeyArea The raw data key area to remove.
     */
    public boolean removePKeyArea(PKeyArea vKeyArea)
    {
        return m_VKeyList.removeElement(vKeyArea);
    }
    /**
     * Open this table (requery the table).
     * @param table The table.
     * @exception DBException File exception.
     */
    public synchronized void open(FieldTable table) throws DBException
    {
        if (m_VKeyList == null)
            this.initKeyAreas(table);
        this.saveCurrentKeys(table, null, true);
    }
    /**
     * Close this raw data table.
     * @param table The table.
     */
    public void close(FieldTable table)
    {
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * You shouldn't have to do anything here, as the PhysicalTable should have set up the buffer.
     * @param table The table.
     * @exception DBException File exception.
     */
    public void addNew(FieldTable table) throws DBException
    {
    }
    /**
     * Add this record (Always called from the record class).
     * @param table The table.
     * @exception DBException File exception.
     */
    public synchronized void add(FieldTable table) throws DBException
    {
        if (m_VKeyList == null)
            this.initKeyAreas(table);

        BaseBuffer bufferNew = (BaseBuffer)table.getDataSource(); // same as .getHandle(Constants.DATA_SOURCE_HANDLE);
        if (table.getRecord().getEditMode() == Constants.EDIT_ADD)
        {
            for (int iKeyArea = Constants.MAIN_KEY_AREA; iKeyArea < table.getRecord().getKeyAreaCount() + Constants.MAIN_KEY_AREA; iKeyArea++)
            {
                try   {
                    if (iKeyArea == Constants.MAIN_KEY_AREA)
                    {   // Special logic to set auto-counter field
                        FieldInfo field = table.getRecord().getCounterField();
                        if (field != null)
                            if (!field.isNull())
                                if (!table.getRecord().isAutoSequence())
                        {   // Autosequence disabled
                            int iValue = (int)field.getValue();
                            if (iValue >= m_iCounter)
                                m_iCounter = iValue + 1;    // New next counter value
                            field = null;   // Special case - autosequence is disabled, so write the field as is.
                        }
                        if (field != null)
                        {
                            field.setValue(m_iCounter++, Constants.DONT_DISPLAY, Constants.READ_MOVE);  // ReadMove keeps this method from being called again
                            table.fieldsToData(table.getRecord()); // Update buffer
                            bufferNew = (BaseBuffer)table.getDataSource();  // same as .getHandle(Constants.DATA_SOURCE_HANDLE);
                        }
                    }
                    bufferNew.bufferToFields(table.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
                    this.getPKeyArea(iKeyArea).doWrite(table, table.getRecord().getKeyArea(iKeyArea), bufferNew);
                } catch (DBException ex)    {
                    if (ex.getErrorCode() == Constants.DUPLICATE_KEY)
                    {
                        iKeyArea--;     // Duplicate key, delete all keys added so far!!!
                        while (iKeyArea >= Constants.MAIN_KEY_AREA)
                        {
                            bufferNew.bufferToFields(table.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
                            this.getPKeyArea(iKeyArea).doRemove(table, table.getRecord().getKeyArea(iKeyArea), bufferNew);
                            iKeyArea--;
                        }
                    }
                    throw ex;
                }
            }
            // Restore the record before returing (key.doWrite probably changed it).
            bufferNew.bufferToFields(table.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
        }
        else
            throw new DBException(Constants.INVALID_RECORD);
    }
    /**
     * Update this record (Always called from the record class).
     * @param table The table.
     * @exception DBException File exception.
     */
    public synchronized void set(FieldTable table) throws DBException
    {
        BaseBuffer bufferNew = (BaseBuffer)table.getDataSource(); // same as .getHandle(Constants.DATA_SOURCE_HANDLE);
        if (table.getRecord().getEditMode() == Constants.EDIT_IN_PROGRESS)
        {
            PKeyArea vKeyArea = this.getPKeyArea(Constants.MAIN_KEY_AREA);
            KeyAreaInfo primaryKeyArea = table.getRecord().getKeyArea(Constants.MAIN_KEY_AREA);
            primaryKeyArea.reverseKeyBuffer(null, Constants.TEMP_KEY_AREA);     // Move these keys back to the record
            BaseBuffer bufferOld = vKeyArea.doSeek("==", table, primaryKeyArea);    // Get the old record back
            if ((bufferOld == null) || (bufferOld.getPhysicalData() == null))
                throw new DBException(Constants.INVALID_RECORD);    // This record had been freed (deleted)

            for (int iKeyArea = Constants.MAIN_KEY_AREA; iKeyArea < table.getRecord().getKeyAreaCount() + Constants.MAIN_KEY_AREA; iKeyArea++)
            {
                try   {
                    vKeyArea = this.getPKeyArea(iKeyArea);
                    KeyAreaInfo keyArea = table.getRecord().getKeyArea(iKeyArea);
                    keyArea.reverseKeyBuffer(null, Constants.TEMP_KEY_AREA);        // Move these keys back to the record
                    if (keyArea.getUniqueKeyCode() != Constants.UNIQUE)   // The main key is part of the comparison
                        primaryKeyArea.reverseKeyBuffer(null, Constants.TEMP_KEY_AREA);     // Move these keys back to the record
                    vKeyArea.doRemove(table, table.getRecord().getKeyArea(iKeyArea), bufferOld);
                    bufferNew.bufferToFields(table.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
                    vKeyArea.doWrite(table, table.getRecord().getKeyArea(iKeyArea), bufferNew);
                } catch (DBException e)   {
                    if (e.getErrorCode() == Constants.DUPLICATE_KEY)
                    {
                        bufferOld.bufferToFields(table.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
                        this.getPKeyArea(iKeyArea).doWrite(table, table.getRecord().getKeyArea(iKeyArea), bufferOld);
                        iKeyArea--;     // Duplicate key, back out all keys added so far!!!
                        while (iKeyArea >= Constants.MAIN_KEY_AREA)
                        {
                            vKeyArea = this.getPKeyArea(iKeyArea);
                            KeyAreaInfo keyArea = table.getRecord().getKeyArea(iKeyArea);
                            bufferNew.bufferToFields(table.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
                            vKeyArea.doRemove(table, table.getRecord().getKeyArea(iKeyArea), bufferNew);
                            keyArea.reverseKeyBuffer(null, Constants.TEMP_KEY_AREA);        // Move these keys back to the record
                            if (keyArea.getUniqueKeyCode() != Constants.UNIQUE)   // The main key is part of the comparison
                                primaryKeyArea.reverseKeyBuffer(null, Constants.TEMP_KEY_AREA);     // Move these keys back to the record
                            vKeyArea.doWrite(table, table.getRecord().getKeyArea(iKeyArea), bufferOld);
                            iKeyArea--;
                        }
                    }
                    throw e;
                }
            }
            if (bufferNew != bufferOld)
                bufferOld.free();   // Get rid of the old one
            bufferOld = null;
            // Restore the record before returing (key.doWrite probably changed it).
            bufferNew.bufferToFields(table.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
        }
        else
            throw new DBException(Constants.INVALID_RECORD);
    }
    /**
     * Delete this record.
     * @param table The table.
     * @exception DBException File exception.
     */
    public synchronized void remove(FieldTable table) throws DBException
    {
        BaseBuffer bufferOld = (BaseBuffer)table.getDataSource(); // same as .getHandle(Constants.DATA_SOURCE_HANDLE);
        for (int iKeyArea = Constants.MAIN_KEY_AREA; iKeyArea < table.getRecord().getKeyAreaCount() + Constants.MAIN_KEY_AREA; iKeyArea++)
        {   // Delete the current key.
            KeyAreaInfo keyArea = table.getRecord().getKeyArea(iKeyArea);
            keyArea.reverseKeyBuffer(null, Constants.TEMP_KEY_AREA);
            this.getPKeyArea(iKeyArea).doRemove(table, table.getRecord().getKeyArea(iKeyArea), bufferOld);
        }
        bufferOld.free();   // Get rid of the old one
        table.setDataSource(null);  // same as .doSetHandle(null, Constants.DATA_SOURCE_HANDLE);
    }
    /**
     * Lock the current object (Always called from the record class).
     * Override this to return a valid flag. but remember to call inherited.
     * @param table The table.
     * @return true if successful, false is lock failed.
     * @exception DBException INVALID_RECORD - Record not current.
     */
    public synchronized int edit(FieldTable table) throws DBException
    {
        this.saveCurrentKeys(table, null, false);
        return Constants.NORMAL_RETURN;
    }
    /**
     * Get the record count.
     * Override this to return a valid count.
     * @return The record count (or -1 if unknown).
     */
    public int getRecordCount(FieldTable table)
    {
        return -1;
    }
    /**
     * Move the position of the record.
     * See PKeyArea for the logic behind this method.
     * @param iRelPosition The relative position to move.
     * @param table The table.
     * @return The buffer containing the data at that location or null if EOF/BOF.
     * @exception DBException File exception.
     */
    public synchronized BaseBuffer move(int iRelPosition, FieldTable table) throws DBException
    {
        int iKeyOrder = table.getRecord().getDefaultOrder();
        if (iKeyOrder == -1)
            iKeyOrder = Constants.MAIN_KEY_AREA;
        KeyAreaInfo keyArea = table.getRecord().getKeyArea(iKeyOrder);
        PKeyArea vKeyArea = this.getPKeyArea(iKeyOrder);

        // First, compare the current position with the record you are trying to read past.
        BaseBuffer buffer = (BaseBuffer)table.getDataSource();
        if ((iRelPosition != Constants.FIRST_RECORD)
            && (iRelPosition != Constants.LAST_RECORD))
                if (!vKeyArea.atCurrent(buffer))
        {
            keyArea.reverseKeyBuffer(null, Constants.TEMP_KEY_AREA);        // Move these keys back to the record
            if (keyArea.getUniqueKeyCode() != Constants.UNIQUE)   // The main key is part of the comparison
                table.getRecord().getKeyArea(Constants.MAIN_KEY_AREA).reverseKeyBuffer(null, Constants.TEMP_KEY_AREA);   // Move these keys back to the record
            buffer = vKeyArea.doSeek("==", table, keyArea);   // Reposition the key here
        }

        buffer = vKeyArea.doMove(iRelPosition, table, keyArea);
        
        this.saveCurrentKeys(table, buffer, (buffer == null));

        return buffer;
    }
    /**
     * Read the record that matches this record's current key.
     * @param strSeekSign The seek sign.
     * @param table The table.
     * @exception DBException File exception.
     */
    public synchronized BaseBuffer seek(String strSeekSign, FieldTable table) throws DBException
    {
        int iKeyOrder = table.getRecord().getDefaultOrder();
        if (iKeyOrder == -1)
            iKeyOrder = Constants.MAIN_KEY_AREA;
        if (m_VKeyList == null)
            this.open(table);   // First time, set up
        PKeyArea vKeyArea = this.getPKeyArea(iKeyOrder);
        KeyAreaInfo keyArea = table.getRecord().getKeyArea(iKeyOrder);
        if (keyArea == null)
            throw new DBException(Constants.INVALID_KEY);
        try   {
            BaseBuffer buffer = vKeyArea.doSeek(strSeekSign, table, keyArea);
            this.saveCurrentKeys(table, buffer, (buffer == null));
            return buffer;
        } catch (DBException ex)    {
            throw ex;
        }
    }
    /**
     * Set up the raw data key areas.
     * @param table The table.
     * @exception DBException File exception.
     */
    public void initKeyAreas(FieldTable table) throws DBException
    {
        if (m_VKeyList == null)
        {
            m_VKeyList = new Vector<PKeyArea>();
            // Now, copy the keys
            for (int iKeyArea = Constants.MAIN_KEY_AREA; iKeyArea <= table.getRecord().getKeyAreaCount() + Constants.MAIN_KEY_AREA - 1; iKeyArea++)
            {
                this.makePKeyArea(table);
            }
        }
    }
    /**
     * Set the counter to max + 1 again.
     * Note: The position is undefined after this call (and the table is closed).
     * @param table The table to fix the counter on.
     */
    public synchronized void fixCounter(FieldTable table)
    {
        PKeyArea vKeyArea = this.getPKeyArea(Constants.MAIN_KEY_AREA);
        KeyAreaInfo keyArea = table.getRecord().getKeyArea(Constants.MAIN_KEY_AREA);
        try   {
            BaseBuffer buffer = vKeyArea.doMove(Constants.LAST_RECORD, table, keyArea);
            if (buffer == null)
                m_iCounter = table.getRecord().getStartingID();
            else
            {
                buffer.bufferToFields(table.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
                Object data = table.getRecord().getCounterField().getData();
                if (data instanceof Integer)
                    m_iCounter = Math.max(m_iCounter, ((Integer)data).intValue() + 1);
            }
            // Now set the pointer back at the first record
            buffer = vKeyArea.doMove(Constants.FIRST_RECORD, table, keyArea);
            if (buffer != null)     // If not at EOF (empty) back up one record
                vKeyArea.doMove(Constants.PREVIOUS_RECORD, table, keyArea);
        } catch (DBException ex)    {
            // Ignore error
        }
    }
    /**
     * Save the current key value's in the user's keyinfo space, so you can retrieve them later.
     * This is used primarly for updates and move()s.
     * @param table The table.
     * @return true if successful.
     * @exception DBException File exception.
     */
    public void saveCurrentKeys(FieldTable table, BaseBuffer bufferToSave, boolean bResetKeys) throws DBException
    {
        if (bufferToSave != null)
            bufferToSave.bufferToFields(table.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
        for (int iKeyArea = Constants.MAIN_KEY_AREA; iKeyArea < table.getRecord().getKeyAreaCount() + Constants.MAIN_KEY_AREA; iKeyArea++)
        { // Save the current keys
            KeyAreaInfo keyArea = table.getRecord().getKeyArea(iKeyArea);
            if (bResetKeys)
                keyArea.zeroKeyFields(Constants.TEMP_KEY_AREA);
            else
                keyArea.setupKeyBuffer(null, Constants.TEMP_KEY_AREA);
        }
    }
    /**
     * Create the raw data key area.
     * Override this!
     * @param The raw data table.
     * @return The new raw data key area.
     */
    public PKeyArea makePKeyArea(FieldTable table)
    {
        return null;    //new PKeyArea(this);
    }
    /**
     * Return the timestamp since this table was last closed.
     */
    public long getLastUsed()
    {
        return m_lTimeLastUsed;
    }
    /**
     * Is this record read-only?
     * @return True if it is read-only.
     */
    public boolean isReadOnly()
    {
        return false;   // Override if different
    }
}
