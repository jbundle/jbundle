/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.shared;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Vector;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.grid.DataRecord;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.event.InitOnceFieldHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldList;


/**
 * Access multiple tables as if they were overrides of the base record's table.
 * This is typically used to access multiple tables from the same overriding class,
 * such as a Dog and Cat class from an Animal class.
 * The difference between this and MultipleTable is multiple table stores records
 * in different tables, SharedBaseRecordTable stores different classes in the same physical record.
 */
public class SharedBaseRecordTable extends BaseSharedTable
{
    public Record getBaseRecord()
    {
        return this.getRecord();
    }
    public Record getCurrentRecord()
    {
        return this.getCurrentTable().getRecord();
    }
    public void setCurrentRecord(Record record)
    {
        this.setCurrentTable(record.getTable());
    }
    /**
     * MultiTable Constructor.
     */
    public SharedBaseRecordTable()
    {
        super();
    }
    /**
     * MultiTable Constructor.
     */
    public SharedBaseRecordTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * init variables.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);
        if (record != null)
        {  // Always
            BaseField field = record.getSharedRecordTypeKey();
            if (field.getListener(InitOnceFieldHandler.class.getName()) == null)
                field.addListener(new InitOnceFieldHandler(null));
            ((InitOnceFieldHandler)field.getListener(InitOnceFieldHandler.class.getName())).setFirstTime(false);
        }
    }
    /**
     * free.
     */
    public void free()
    {
        super.free();

        if (m_recordOwnerFake != null)
            m_recordOwnerFake.free();
        m_recordOwnerFake = null;
    }
    /**
     * Do the physical Open on this table (requery the table).
     * Note - ignore's open because open is used as a flag to indicate moveNext in process.
     */
    public void open() throws DBException
    {
        super.open();
    }
    /**
     * Close this table.
     */
    public void close()
    {
        super.close();
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * @exception DBException File exception.
     */
    public void addNew() throws DBException
    {
        super.addNew();
        Record recCurrent = this.moveToCurrentRecord(null);   // Perhaps, they supplied a default record type and this will switch it.
        if ((recCurrent != null) && (this.getBaseRecord() != recCurrent))
            recCurrent.handleNewRecord(); // Display Fields and do Add behaviors on correct record
        else if ((this.getCurrentRecord() != null) && (this.getCurrentRecord() != this.getBaseRecord()))
        {
            this.getBaseRecord().matchListeners(this.getCurrentRecord(), false, true, true, true, true);      // Clone the listeners that are not there already.
            this.getCurrentRecord().addNew();
        }
    }
    /**
     * Add this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void add(Rec fieldList) throws DBException
    {
        if (this.getBaseRecord() == fieldList)    // If so, set the correct record for write
            fieldList = this.moveToCurrentRecord((Record)fieldList);   // Hopefully, they supplied a default record type and this will switch it.
        if (fieldList == null)
            throw new DBException("Unknown shared record type");
        if (fieldList == this.getBaseRecord())
            super.add(fieldList);
        else
        {
            this.getBaseRecord().matchListeners((Record)fieldList, false, true, true, true, true);      // Clone the listeners that are not there already.
            fieldList.getTable().add(fieldList);
        }
    }
    /**
     * Delete this record (Always called from the record class).
     * Always override this method.
     * @exception DBException File exception.
     */
    public void remove() throws DBException
    {
        if (this.getCurrentRecord() == this.getBaseRecord())
            super.remove();
        else
        {
            this.getBaseRecord().matchListeners(this.getCurrentRecord(), false, true, true, true, true);      // Clone the listeners that are not there already.
            this.getCurrentRecord().remove();
        }
    }
    /**
     * Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void set(Rec fieldList) throws DBException
    {
        Record recBase = this.getBaseRecord();
        Record recCurrent = this.getCurrentRecord();
        if (fieldList != recBase)
        {
            if (fieldList != recCurrent)
                throw new DBException("Can't change record class then add");
        }
        else
        {
            if (recBase != recCurrent)
                if (recCurrent.isModified())
                    this.copyRecordInfo(recBase, recCurrent, false, true);
            fieldList = this.moveToCurrentRecord((Record)fieldList);
        }
        if (fieldList != recBase)
        {
            this.getBaseRecord().matchListeners(this.getCurrentRecord(), false, true, true, true, true);      // Clone the listeners that are not there already.
            fieldList.getTable().set(fieldList);
        }
        else
            super.set(fieldList);   // Never (hopefully)
        // Note, since target record is passed, add listeners are called on the correct record
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
        this.getCurrentTable().getRecord().setEditMode(this.getBaseRecord().getEditMode());    // In case the screen has this.
        return iErrorCode;
    }
    /**
     * Is the first record in the file?
     * @return false if file position is at first record.
     */
    public boolean hasPrevious() throws DBException
    {
        return super.hasPrevious();   // Rarely
    }
    /**
     * Is there another record (is this not the last one)?
     */
    public boolean hasNext() throws DBException
    {
        return super.hasNext();
    }
    /**
     * Move the position of the record.
     * @exception DBException File exception.
     * @iRelPosition Number of positions to move.
     * @return The record.
     */
    public FieldList move(int iRelPosition) throws DBException
    {
        this.setCurrentRecord(this.getBaseRecord());
        boolean bAfterRequery = !this.getBaseRecord().isOpen();
        if (!this.isOpen())
            this.open();    // Make sure any listeners are called before disabling.
        Object[] rgobjEnabledFields = this.getBaseRecord().setEnableNonFilter(null, false, false, false, false, true);
        try {
            Record record = (Record)super.move(iRelPosition);
            if (record == null)
                return null;

            this.getBaseRecord().setEnableNonFilter(rgobjEnabledFields, true, false, bAfterRequery, false, true);
            rgobjEnabledFields = null;
            
      //      this.getBaseRecord().handleValidRecord(); // Display Fields (handleValidRecord this was done in setEnableNonFilter)
            record = this.moveToCurrentRecord(record);
            if (record != null) if (record != this.getBaseRecord())
                record.handleValidRecord(); // Display Fields

            return record;
        } catch (DBException ex)    {
            throw ex;
        } finally {
            if (rgobjEnabledFields != null)
                this.getBaseRecord().setEnableNonFilter(rgobjEnabledFields, false, false, bAfterRequery, true, true);   // EOF
        }
    }
    /**
     * Reposition to this record Using this bookmark.
     * @exception DBException File exception.
     */
    public FieldList setHandle(Object bookmark, int iHandleType) throws DBException
    {
        this.setCurrentRecord(this.getBaseRecord());
        Object[] rgobjEnabledFields = this.getBaseRecord().setEnableFieldListeners(false);
        try {
            Record record = (Record)super.setHandle(bookmark, iHandleType);
            if (record == null)
                return null;

            this.getBaseRecord().setEnableFieldListeners(rgobjEnabledFields);

            this.getBaseRecord().handleValidRecord(); // Display Fields
            record = this.moveToCurrentRecord(record);
            if (record != null) if (record != this.getBaseRecord())
                record.handleValidRecord(); // Display Fields

            return record;
        } catch (DBException ex)    {
            throw ex;
        } finally {
            this.getBaseRecord().setEnableFieldListeners(rgobjEnabledFields);
        }
    }
    /**
     * Get the ObjectIDHandle to the last modified or added record.
     * This uses some very inefficient code... override if possible.
     */
    public Object getLastModified(int iHandleType)
    {
        return super.getLastModified(iHandleType);
    }
    /**
     * Get a unique key that can be used to reposition to this record.
     * @exception DBException File exception.
     */
    public Object getHandle(int iHandleType) throws DBException
    {
        return super.getHandle(iHandleType);
    }
    /**
     * Read the record that matches this record's current key.
     * @exception DBException File exception.
     */
    public boolean seek(String strSeekSign) throws DBException
    {
        this.setCurrentRecord(this.getBaseRecord());
        Object[] rgobjEnabledFields = this.getBaseRecord().setEnableFieldListeners(false);
        try {
            boolean bSuccess = super.seek(strSeekSign);
            if (bSuccess)
            {
                this.getBaseRecord().setEnableFieldListeners(rgobjEnabledFields);
                
                this.getBaseRecord().handleValidRecord(); // Display Fields
                Record record = this.moveToCurrentRecord(this.getCurrentTable().getRecord());
                if (record != null) if (record != this.getBaseRecord())
                    record.handleValidRecord(); // Display Fields
            }
            return bSuccess;
        } catch (DBException ex)    {
            throw ex;
        } finally {
            this.getBaseRecord().setEnableFieldListeners(rgobjEnabledFields);
        }
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
     * @param field The field to move the current data to.
     * @return The error code (From field.setData()).
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
     * @exception DBException File exception.
     */
    public int dataToField(Field field) throws DBException
    {
        return super.dataToField(field);
    }
    /**
     * Set up/do the local criteria.
     * This is only here to accomodate local file systems that can't handle
     * REMOTE criteria. All you have to do here to handle the remote criteria
     * locally is to call: return record.handleRemoteCriteria(xx, yy).
     */
    public boolean doLocalCriteria(StringBuffer strFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {   // Default BaseListener
        return super.doLocalCriteria(strFilter, bIncludeFileName, vParamList);    // If can't handle remote
    }
    /**
     * Make the record represented by this DataRecord current.
     * @param dataRecord tour.db.DataRecord
     */
    public boolean setDataRecord(DataRecord dataRecord)
    {
        this.setCurrentRecord(this.getBaseRecord());
        Object[] rgobjEnabledFields = this.getBaseRecord().setEnableFieldListeners(false);
        boolean bSuccess = super.setDataRecord(dataRecord);
        this.getBaseRecord().setEnableFieldListeners(rgobjEnabledFields);
        if (bSuccess)
        {
            Record record = this.getBaseRecord();
            record.handleValidRecord(); // Display Fields
            record = this.moveToCurrentRecord(record);
            if (record != null) if (record != this.getBaseRecord())
                record.handleValidRecord(); // Display Fields
        }
        return bSuccess;
    }
    /**
     * Figure what kind of record this record is and move it to the correct record.
     * @record The record to move to current.
     */
    public Record moveToCurrentRecord(Record recBase)
    {
        if (recBase == null)
            recBase = this.getBaseRecord(); // Actually, should always be the recBase.
        BaseField fldRecordType = recBase.getSharedRecordTypeKey();
        Object objKey = fldRecordType.getData();
        BaseTable tableCurrent = this.getTableAt(objKey);
        Record recCurrent = null;
        if (tableCurrent != null)
            recCurrent = tableCurrent.getRecord();
        if (recCurrent == null)
        {
            RecordOwner recordOwner = recBase.getRecordOwner();
            RecordOwner recordOwnerFake = this.getFakeRecordOwner(recordOwner);
            recCurrent = recBase.createSharedRecord(objKey, recordOwnerFake);
            if (recCurrent != null)
                if (recCurrent.getRecordOwner() == recordOwnerFake)
            {   // Usually
                recCurrent.setRecordOwner(recordOwner);    // No record owner
                // Add this table (but do not use this.addTable because it closes the next table)
                this.addTable(objKey, recCurrent.getTable());
                recCurrent.setOpenMode(recBase.getOpenMode());  // Same mode.
                recBase.matchListeners(recCurrent, true, true, true, true, true);      // Clone the listeners that are not there already.
            }
        }
        if (recCurrent != null)
            if (recCurrent != recBase)
        {
            this.copyRecordInfo(recCurrent, recBase, true, false);
        }
        if (recCurrent != null)
            this.setCurrentRecord(recCurrent);
        return recCurrent;
    }
    /**
     * Set the current table target.
     * @param table The new current table.
     */
    public void copyRecordInfo(Record recDest, Record recSource, boolean bCopyEditMode, boolean bOnlyModifiedFields)
    {
        if (recDest == null)
            recDest = this.getCurrentRecord();
        if (recDest != recSource)
        {
            boolean bAllowFieldChange = false;		// This will disable field behaviors on move
            boolean bMoveModifiedState = true;		// This will move the modified status to the new field
            Object[] rgobjEnabledFieldsOld = recSource.setEnableFieldListeners(false);
            recDest.moveFields(recSource, null, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE, bAllowFieldChange, bOnlyModifiedFields, bMoveModifiedState);
            recSource.setEnableFieldListeners(rgobjEnabledFieldsOld);
            if (bCopyEditMode)
                recDest.setEditMode(recSource.getEditMode());    // Okay?
        }
    }
    /**
     * Get/create a fake recordowner that will insure that the new record will make its own table.
     * @param parent The recordowner of the base record.
     * @return The current or newly created fake recordowner.
     */
    protected FakeRecordOwner m_recordOwnerFake = null;
    public RecordOwner getFakeRecordOwner(RecordOwnerParent parent)
    {
        if (m_recordOwnerFake == null)
            m_recordOwnerFake = new FakeRecordOwner(this, parent, null, null);
        return m_recordOwnerFake;
    }
}
