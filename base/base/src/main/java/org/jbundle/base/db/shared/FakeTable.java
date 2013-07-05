/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.shared;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.RecordChangedHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldList;


/**
 * A fake table - The record must be moved to a record with a physical table to do anything. 
 */
public class FakeTable extends BaseTable
{
    protected BaseDatabase m_databaseFake = null;
    
    /**
     *
     */
    public SharedBaseRecordTable getSharedTable()
    {
        return (SharedBaseRecordTable)((FakeRecordOwner)m_databaseFake.getDatabaseOwner()).getSharedTable();
    }
    /**
     *
     */
    public BaseTable getNextTable()
    {
        return this.getSharedTable().getNextTable();
    }
    /**
     * Copy this record to the base table.
     */
    public Record moveRecordToBase(Record record)
    {
        SharedBaseRecordTable sharedTable = this.getSharedTable();
        sharedTable.setCurrentRecord(record);
        boolean bCopyEditMode = false;
        boolean bOnlyModifiedFields = false;
        Record recBase = sharedTable.getBaseRecord();
        sharedTable.copyRecordInfo(recBase, record, bCopyEditMode, bOnlyModifiedFields);
        return recBase;
    }

    /**
     * Constructor.
     */
    public FakeTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public FakeTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * Init this table.
     * Add this table to the database and hook this table to the record.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);
        
        m_databaseFake = m_database;
        m_database = this.getSharedTable().getDatabase();  // Return the correct database
    }
    /**
     * Free this table object.
     * Don't call this directly, freeing the record will free the table correctly.
     * Client table just calls the remote freeTable() method.
     */
    public void free()
    {
        m_database = m_databaseFake;  // Restore the correct parent database
        m_databaseFake = null;
        super.free();
    }
    /**
     * Do the physical Open on this table (requery the table).
     * @exception DBException File exception.
     */
    public void doOpen() throws DBException
    {
        this.getNextTable().open();
    }
    /**
     * Close this table.
     * This is not implemented for a ClientTable, since the TableSessionObject always does
     * a close before each open.
     */
    public void close()
    {
        if (m_databaseFake != null)
            this.getNextTable().close();    // If not in free, close
        super.close();
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * This is not implemented for a ClientTable, since the TableSessionObject always does
     * an addNew before an add() call.
     * @exception DBException File exception.
     */
    public void doAddNew() throws DBException
    {
        this.getNextTable().addNew();
    }
    /**
     * Add this record (Always called from the record class).
     * Make the remote add call with the current data.
     * @param record The record to add.
     * @exception DBException File exception.
     */
    public void doAdd(Record record) throws DBException
    {
        record = this.moveRecordToBase(record);

        int iOpenMode = record.getOpenMode();
        RecordChangedHandler recordChangeListener = (RecordChangedHandler)record.getListener(RecordChangedHandler.class);
        Object[] rgobjEnabledFields = record.setEnableFieldListeners(false);
        boolean[] rgbEnabled = record.setEnableListeners(false);
        if (recordChangeListener != null)
        	recordChangeListener.setEnabledListener(true);	// I may need this listener (for an update) to do the update =ID AND =LastChangeDate
        
        try {
            record.setOpenMode(0);  // Normal add
            this.getNextTable().add(record);
        } catch (DBException ex) {
            throw ex;
        } finally {
            record.setOpenMode(iOpenMode);
            record.setEnableListeners(rgbEnabled);
            record.setEnableFieldListeners(rgobjEnabledFields);
        }
    }
    /**
     * Lock the current record.
     * Will not make the remote call if the remote db doesn't have lock capability.
     * The remote set method automatically does an edit before calling.
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
    public int doEdit() throws DBException
    {
        int iErrorCode = this.getNextTable().edit();
        return iErrorCode;
    }
    /**
     * Update this record (Always called from the record class).
     * @param record The record to add.
     * @exception DBException File exception.
     */
    public void doSet(Record record) throws DBException
    {
        record = this.moveRecordToBase(record);

        int iOpenMode = record.getOpenMode();
        RecordChangedHandler recordChangeListener = (RecordChangedHandler)record.getListener(RecordChangedHandler.class);
        Object[] rgobjEnabledFields = record.setEnableFieldListeners(false);
        boolean[] rgbEnabled = record.setEnableListeners(false);
        if (recordChangeListener != null)
        	recordChangeListener.setEnabledListener(true);	// I will need this listener to do the update =ID AND =LastChangeDate
        
        try {
//x            record.setOpenMode(0);  // Normal set
            this.getNextTable().set(record);
        } catch (DBException ex) {
            throw ex;
        } finally {
            record.setOpenMode(iOpenMode);
            record.setEnableListeners(rgbEnabled);
            record.setEnableFieldListeners(rgobjEnabledFields);
        }
    }
    /**
     * Delete this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void doRemove() throws DBException
    {
        this.getNextTable().remove();
    }
    /**
     * Move the position of the record.
     * @param iRelPosition - Relative position positive or negative or FIRST_RECORD/LAST_RECORD.
     * @return NORMAL_RETURN - The following are NOT mutually exclusive
     * @exception DBException File exception.
     */
    public int doMove(int iRelPosition) throws DBException
    {
        throw new DBException("Can't move through an inherited class");
    }
    /**
     * Read the record that matches this record's current key.
     * <p>NOTE: You do not need to Open to do a seek or addNew.
     * @param strSeekSign - Seek sign:
     * @return true if successful, false if not found.
     * @exception FILE_NOT_OPEN.
     * @exception KEY_NOT_FOUND - The key was not found on read.
     */
    public boolean doSeek(String strSeekSign) throws DBException
    {
        throw new DBException("Can't seek in an inherited class");
    }
    /**
     * Reposition to this record using this bookmark.
     * @param bookmark The handle to use to position the record.
     * @param iHandleType The type of handle (DATA_SOURCE/OBJECT_ID,OBJECT_SOURCE,BOOKMARK).
     * @return  - true - record found/false - record not found
     * @exception FILE_NOT_OPEN.
     * @exception DBException File exception.
     */
    public boolean doSetHandle(Object bookmark, int iHandleType) throws DBException
    {
        FieldList fieldList = this.getSharedTable().setHandle(bookmark, iHandleType);
        if (fieldList != null)
            if (fieldList.getTable() != this)  // Must be this type of record
		        throw new DBException("Can't set handle in an inherited class");
        return (fieldList != null);
    }
    /**
     * Get a unique object that can be used to reposition to this record.
     * Note: This doesn't do a remote call, it just returns the current handle (bookmark type).
     * @param int iHandleType 
     *  BOOKMARK_HANDLE - long ID for access to native (DB) objects<br />
     *  OBJECT_ID_HANDLE - object ID of persistent object (remote or local)<br />
     *  DATA_SOURCE_HANDLE - pointer to the physical object (persistent or not)<br />
     *  OBJECT_SOURCE_HANDLE - source of this object (RMI Server or JDBC path)<br />
     *  FULL_OBJECT_HANDLE - full path to this object ObjectSource + ObjectID.
     * @return The data.
     * @exception FILE_NOT_OPEN.
     * @exception INVALID_RECORD - There is no current record.
     */
    public Object getHandle(int iHandleType) throws DBException   
    {
        return this.getNextTable().getHandle(iHandleType);
    }
    /**
     * Get the Handle to the last modified or added record.
     * This uses some very inefficient (and incorrect) code... override if possible.
     * NOTE: There is a huge concurrency problem with this logic if another person adds
     * a record after you, you get the their (wrong) record, which is why you need to
     * provide a solid implementation when you override this method.
     * @param iHandleType The handle type.
     * @return The bookmark.
     */
    public Object getLastModified(int iHandleType)
    {
        return this.getNextTable().getLastModified(iHandleType);
    }
    /**
     * Move the data source buffer to all the fields.
     * In this implementation, move the local copy of the returned datasource to the fields.
     * @return an error code.
     * @exception DBException File exception.
     */
    public int dataToFields(Rec record) throws DBException
    {
        return DBConstants.NORMAL_RETURN;
//x        return super.dataToFields(record);
    }
    /**
     * Move all the fields to the output buffer.
     * In this implementation, create a new VectorBuffer and move the fielddata to it.
     * @exception DBException File exception.
     */
    public void fieldsToData(Rec record) throws DBException
    {
//x        super.fieldsToData(record);
    }
    /**
     * Is this a table (or SQL) type of "Table"?.
     * This flag determines when you hit the end of a query. If set, every record is compared
     * againt the last record in the query. If not, the only EOF determines the end (ie., a SQL query).
     * Override this and return false where the DB returns the complete query.
     * @return false Because a client table has the remote table handle the complicated calls.
     */
    public boolean isTable()
    {   // Use the physical 'table' calls
        return false;
    }
    /**
     * Get this property for this table.
     * NOTE: This is only for this table, if you want global properties, use getDatabase().getProperty(xx);
     * @param strProperty The property key.
     * @return The property value.
     */
    public String getProperty(String strProperty)
    {
        String strValue = super.getProperty(strProperty);
        if (strValue == null)
        	return this.getSharedTable().getProperty(strProperty);
        return strValue;
    }
    /**
     * Set a property for this table.
     * Usually used to Enable/Disable autosequence for this table.
     * <p />Sets the remote properties and also sets the local properties.
     * @param strProperty The key to set.
     * @param strValue The value to set.
     */
//x    public void setProperty(String strProperty, String strValue)
//x    {
//x        this.getNextTable().setProperty(strProperty, strValue);
//x    }
}
