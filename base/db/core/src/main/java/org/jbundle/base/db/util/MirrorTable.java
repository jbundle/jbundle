/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.util;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Iterator;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.PassThruTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;


/**
 * MirrorTable - A mirror table synchronizes two identical tables.
 * Every add, delete, etc. goes to both tables.
 */
public class MirrorTable extends PassThruTable
{

    /**
     * RecordList Constructor.
     */
    public MirrorTable()
    {
        super();
    }
    /**
     * RecordList Constructor.
     */
    public MirrorTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * init.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * This is usually called from thin code, so make sure you return the CURRENT record.
     * @return The record from the current table.
     */
    public Record getRecord()
    {
        return this.getNextTable().getRecord(); // Always use the main db's record
    }
    /**
     * Do the physical Open on this table (requery the table).
     * @exception DBException File exception.
     */
    public void open() throws DBException
    {
        super.open();
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            if ((table != null) && (table != this.getNextTable()))
            {
                table.open();
            }
        }
    }
    /**
     * Close the recordset.
     */
    public void close()
    {
        super.close();
        // All tables are closed in PassThruTable
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * @exception DBException File exception.
     */
    public void addNew() throws DBException
    {
        if (((this.getRecord().getOpenMode() & DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY) == 0)
            && ((this.getRecord().getOpenMode() & DBConstants.OPEN_READ_ONLY) != DBConstants.OPEN_READ_ONLY)
            && (this.getRecord().getCounterField() != null))
                this.getRecord().setOpenMode(this.getRecord().getOpenMode() | DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY); // Must have for MirrorTable to sync new records
        super.addNew();
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            if ((table != null) && (table != this.getNextTable()))
            {
                table.addNew();
            }
        }
    }
    /**
     * Add this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void add(Rec fieldList) throws DBException
    {
        Record record = (Record)fieldList;
        boolean bRefreshed = true;
        if ((this.getRecord().getOpenMode() & DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY) == DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY)
            bRefreshed = record.isRefreshedRecord();    // Only do this on the second write
        super.add(record);
        if (bRefreshed)
        {   // On second only
            Iterator<BaseTable> iterator = this.getTables();
            while (iterator.hasNext())
            {
                BaseTable table = iterator.next();
                if ((table != null) && (table != this.getNextTable()))
                {
                    if (table.getRecord().getEditMode() == Constants.EDIT_ADD)
                    {   // Should have been in sync, but it is now!
                        Record record2 = table.getRecord();
                        boolean bIsAutoSequence = record2.setAutoSequence(false);
                        this.copyRecord(record2, record);
                        try {
                            table.add(record2);
                        } catch (DBException ex) {
                            throw ex;
                        } finally {
                            record2.setAutoSequence(bIsAutoSequence);
                        }
                    }
                }
            }
        }
    }
    /**
     * Delete this record (Always called from the record class).
     * Always override this method.
     * @exception DBException File exception.
     */
    public void remove() throws DBException
    {
        super.remove();
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            if ((table != null) && (table != this.getNextTable()))
            {
                if ((table.getRecord().getEditMode() == Constants.EDIT_IN_PROGRESS) || (table.getRecord().getEditMode() == Constants.EDIT_CURRENT))
                    table.remove();
            }
        }
    }
    /**
     * Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void set(Rec fieldList) throws DBException
    {
        super.set(fieldList);
        Record record = (Record)fieldList;
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            if ((table != null) && (table != this.getNextTable()))
            {
                if ((table.getRecord().getEditMode() == Constants.EDIT_IN_PROGRESS) || (table.getRecord().getEditMode() == Constants.EDIT_CURRENT))
                {
                    Record record2 = table.getRecord();
                    this.copyRecord(record2, record);
                    table.set(record2);
                }
                else if (table.getRecord().getEditMode() == Constants.EDIT_ADD)
                {   // Should have been in sync, but it is now!
                    Record record2 = table.getRecord();
                    boolean bIsAutoSequence = record2.setAutoSequence(false);
                    this.copyRecord(record2, record);
                    try {
                        table.add(record2);
                    } catch (DBException ex) {
                        throw ex;
                    } finally {
                        record2.setAutoSequence(bIsAutoSequence);
                    }
                }
            }
        }
    }
    /**
     * Lock the current record.
     * This method responds differently depending on what open mode the record is in:
     * OPEN_DONT_LOCK - A physical lock is not done. This is usually where deadlocks are possible
     * (such as screens) and where transactions are in use (and locks are not needed).
     * OPEN_LOCK_ON_EDIT - Holds a lock until an update or close. (Update crucial data, or hold records for processing)
     * Returns false is someone alreay has a lock on this record.
     * OPEN_WAIT_FOR_LOCK - Don't return from edit until you get a lock. (ie., Add to the total).
     * Returns false if someone has a hard lock or time runs out.
     * @return true if successful, false is lock failed.
     * @exception DBException FILE_NOT_OPEN
     * @exception DBException INVALID_RECORD - Record not current.
     */
    public int edit() throws DBException
    {
        int iErrorCode = super.edit();
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            if ((table != null) && (table != this.getNextTable()))
            {
                if (table.getRecord().getEditMode() == Constants.EDIT_CURRENT)
                    table.edit();
            }
        }
        return iErrorCode;
    }
    /**
     * Is the first record in the file?
     * @return false if file position is at first record.
     */
    public boolean hasPrevious() throws DBException
    {
        boolean bSuccess = super.hasPrevious();
        return bSuccess;
    }
    /**
     * Is there another record (is this not the last one)?
     */
    public boolean hasNext() throws DBException
    {
        boolean bSuccess = super.hasNext();
        return bSuccess;
    }
    /**
     * Move the position of the record.
     * @exception DBException File exception.
     */
    public FieldList move(int iRelPosition) throws DBException
    {
        Record record = (Record)super.move(iRelPosition);
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            if ((table != null) && (table != this.getNextTable()))
                this.syncTables(table, record);
        }
        return record;
    }
    /**
     * Read the record that matches this record's current key.<p>
     * @exception DBException File exception.
     */
    public boolean seek(String strSeekSign) throws DBException
    {
        boolean bSuccess = super.seek(strSeekSign);
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            if ((table != null) && (table != this.getNextTable()))
                this.syncTables(table, this.getRecord());
        }
        return bSuccess;
    }
    /**
     * Get the ObjectIDHandle to the last modified or added record.
     * This uses some very inefficient code... override if possible.
     */
    public Object getLastModified(int iHandleType)
    {
        Object bookmark = super.getLastModified(iHandleType);
        return bookmark;
    }
    /**
     * Reposition to this record Using this bookmark.
     * @exception DBException File exception.
     */
    public FieldList setHandle(Object bookmark, int iHandleType) throws DBException
    {
        FieldList record = super.setHandle(bookmark, iHandleType);
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            if ((table != null) && (table != this.getNextTable()))
                this.syncTables(table, this.getRecord());
        }
        return record;
    }
    /**
     * Get a unique key that can be used to reposition to this record.
     * @exception DBException File exception.
     */
    public Object getHandle(int iHandleType) throws DBException
    {
        Object bookmark = super.getHandle(iHandleType);
        return bookmark;
    }
    /**
     * Move all the fields to the output buffer.
     * @exception DBException File exception.
     */
    public void fieldsToData(Rec record) throws DBException
    {
        super.fieldsToData(record);
    }
    /**
     * Move this Field's data to the record area (Override this for non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @exception DBException File exception.
     */
    public void fieldToData(Field field) throws DBException
    {
        super.fieldToData(field);
    } 
    /**
     * Move the data source buffer to all the fields.<p>
     *  Make sure you do the following steps:
     *  1) Move the data fields to the correct record data fields.
     *  2) Save the data source or set to null, so I can cache the source if necessary.
     *  3) Save the objectID if it is not an Integer type, so I can serialize the source of this object.
     * @exception DBException File exception.
     */
    public int dataToFields(Rec record) throws DBException
    {
        return super.dataToFields(record);
    }
    /**
     * Move the output buffer to this field (Override this to use non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the data to.
     * @return The error code (From field.setData()).
     * @exception DBException File exception.
     */
    public int dataToField(Field field) throws DBException
    {
        return super.dataToField(field);
    } 
    /**
     * Copy the fields from the (main) source to the (mirrored) destination record.
     * This is done before any write or set.
     * @param recAlt Destination record
     * @param recMain Source record
     */
    public void copyRecord(Record recAlt, Record recMain)
    {
        recAlt.moveFields(recMain, null, true, DBConstants.READ_MOVE, false, false, true);
    }
    /**
     * Read the mirrored record.
     * @param tblTarget The table to read the same record from.
     * @param recSource Source record
     * @return The target table's record.
     */
    public Record syncTables(BaseTable tblTarget, Record recSource) throws DBException
    {
        tblTarget.getRecord().readSameRecord(recSource, false, false);
        return tblTarget.getRecord();
    }
}
