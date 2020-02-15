/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db;

import org.jbundle.model.DBException;
import org.jbundle.model.RemoteTarget;
import org.jbundle.model.db.Database;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.model.db.Table;


/**
 * List of Records (FieldList[s]).
 */
public class FieldTable extends Object
    implements Table
{
    /**
     * This is the error code for a deleted record.
     */
    public static final Integer DELETED_RECORD = new Integer(2);
    /**
     * End of file error.
     */
    public static final Integer EOF_RECORD = new Integer(4);   // End of file status.
    /**
     * The current record.
     */
    protected FieldList m_record = null;
    /**
     * If true, this table is open.
     */
    protected boolean m_bIsOpen = false;
    /**
     * The object that contains the physical data.<br />
     *  resultset - for JDBC database tables.<br />
     *  DBObject - for Remote tables (Corba/RMI).<br />
     *  Buffer - for Vector tables.<br />
     * Vector - For some thin implementations.
     */
    protected Object m_dataSource = null;   // Remote object this table relates to

    /**
     * Constructor.
     */
    public FieldTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The initial record for this table.
     */
    public FieldTable(FieldList record)
    {
        this();
        this.init(record);
    }
    /**
     * Constructor.
     * @param record The initial record for this table.
     */
    public void init(Rec record)
    {
        m_dataSource = null;
        this.setRecord(record);
        if (record != null)
        {
            Object owner = record.getOwner();
            record.setOwner(null);  // Make sure I don't create a new table when I call getTable().
            if (record.getTable() == null)
                record.setTable(this);  // Just being careful (Note: Thick model does create a table before calling this method)
            record.setOwner(owner);  // Restore
        }
    }
    /**
     * I'm done with the model, free the resources.
     * Note: This does not free the associated record. it just nulls the reference.
     * (although freeing the record will free this table).
     */
    public void free()
    {
        if (m_record != null) // First, release the record for this table
        { // Do not free this.getFieldList() (may be current record)
            m_record.setTable(null);    // Don't try to free me
            m_record.free();
            m_record = null;
        }
    }
    /**
     * Move all the fields to the output buffer.
     * @exception Exception File exception.
     */
    public void fieldsToData(Rec record) throws DBException
    {
        this.setupNewDataSource();
        int fieldCount = record.getFieldCount(); // Number of fields to write out
        for (int iFieldSeq = Constants.MAIN_FIELD; iFieldSeq < fieldCount + Constants.MAIN_FIELD; iFieldSeq++)
        {
            Field field = record.getField(iFieldSeq);
            if (field.isVirtual()) //x || (!field.isSelected()))
                continue;               // This field is never moved to a buffer
            this.fieldToData(field);
        }
    }
    /**
     * Move this Field's data to the record area (Override this for non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the data from.
     * @exception DBException File exception.
     */
    public void fieldToData(Field field) throws DBException
    {
        throw new DBException("You must override fieldToData");
    } 
    /**
     * Move the data source buffer to all the fields.
     *  <p /><pre>
     *  Make sure you do the following steps:
     *  1) Move the data fields to the correct record data fields, with mode Constants.READ_MOVE.
     *  2) Set the data source or set to null, so I can cache the source if necessary.
     *  3) Save the objectID if it is not an Integer type, so I can serialize the source of this object.
     *  </pre>
     * @exception Exception File exception.
     * @return Any error encountered moving the data.
     */
    public int dataToFields(Rec record) throws DBException
    {
        int iFieldCount = record.getFieldCount();      // Number of fields to read in
        for (int iFieldSeq = Constants.MAIN_FIELD; iFieldSeq < iFieldCount + Constants.MAIN_FIELD; iFieldSeq++)
        {
            Field field = record.getField(iFieldSeq);
            if (field.isVirtual()) //x|| (!field.isSelected()))
                field.initField(Constants.DONT_DISPLAY);
            else
                this.dataToField(field);
        }
        return Constants.NORMAL_RETURN;
    }
    /**
     * Move the output buffer to this field (Override this to use non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the current data to.
     * @return The error code (From field.setData()).
     * @exception DBException File exception.
     */
    public int dataToField(Field field) throws DBException
    {
        throw new DBException("You must override dataToField");
    } 
    /**
     * Get the record for this table.
     * @return The record.
     */
    public FieldList getRecord()
    {
        return m_record;
    } 
    /**
     * Set this field's Record.
     * @param record The Record.
     */
    public void setRecord(Rec record)
    {
        m_record = (FieldList)record;
    }
        // Here are the access operations:
    /**
     * Reset the current position and open the file.
     * Note: This is called automatically on your first file access.
     */
    public void open() throws DBException
    {
        m_bIsOpen = true;
        this.getRecord().setEditMode(Constants.EDIT_NONE);
        this.setDataSource(null);
    }
    /**
     * Close the file.
     * Reset the current position.
     */
    public void close()
    {
        m_bIsOpen = false;
        this.getRecord().setEditMode(Constants.EDIT_NONE);
    }
    /**
     * Create a new empty record.
     * Discard the current fields, init all the fields and display them,
     * and set the status to EDIT_ADD.
     */
    public void addNew() throws DBException
    {
        this.getRecord().initRecord(true);
        this.getRecord().setEditMode(Constants.EDIT_ADD);
    }
    /**
     * Add this record to this table.
     * Add this record to the table and set the current edit mode to NONE.
     * @param record The record to add.
     * @throws Exception
     */
    public void add(Rec record) throws DBException
    {
        if (this.getRecord().getEditMode() != Constants.EDIT_ADD)
            throw new DBException(Constants.INVALID_RECORD);
        m_record = (FieldList)record;
        this.fieldsToData(record);
        this.doAdd(record);
        this.setDataSource(null);
        this.getRecord().setEditMode(Constants.EDIT_NONE);
    }
    /**
     * Add this record to this table.
     * Add this record to the table and set the current edit mode to NONE.
     * @param record The record to add.
     * @throws Exception
     */
    public void doAdd(Rec record) throws DBException
    {
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
     * NOTE: For a remote table it is not necessary to call edit, as edit will
     * be called automatically by the set() call.
     */
    public int edit() throws DBException
    {
        if ((this.getRecord().getEditMode() != Constants.EDIT_CURRENT)
            && (this.getRecord().getEditMode() != Constants.EDIT_IN_PROGRESS))
            throw new DBException(Constants.INVALID_RECORD);
        int iErrorCode = this.doEdit(); // Only call if edit is supported by remote db
        if (iErrorCode == Constants.NORMAL_RETURN)
            this.getRecord().setEditMode(Constants.EDIT_IN_PROGRESS);
        return iErrorCode;
    }
    /**
     * Lock the current record (Always called from the record class).
     * NOTE: For a remote table it is not necessary to call edit, as edit will
     * be called automatically by the set() call.
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
        return Constants.NORMAL_RETURN;
    }
    /**
     * Set the current record to this (new) record.
     * NOTE: In the thin model, it is not necessary to call edit() before set.
     * @exception DBException Throws an exception if there is no current record.
     */
    public void set(Rec record) throws DBException
    {
        if ((this.getRecord().getEditMode() != Constants.EDIT_CURRENT)
            && ((this.getRecord().getEditMode() != Constants.EDIT_IN_PROGRESS)))
                throw new DBException(Constants.INVALID_RECORD);
        m_record = (FieldList)record;
        this.fieldsToData(record);
        this.doSet(record);
        this.setDataSource(null);
        this.getRecord().setEditMode(Constants.EDIT_NONE);
    }
    /**
     * Set the current record to this (new) record.
     * NOTE: In the thin model, it is not necessary to call edit() before set.
     * @exception DBException Throws an exception if there is no current record.
     */
    public void doSet(Rec record) throws DBException
    {
    }
    /**
     * Remove the current record.
     */
    public void remove() throws DBException
    {
        if ((this.getRecord().getEditMode() != Constants.EDIT_CURRENT)
            && ((this.getRecord().getEditMode() != Constants.EDIT_IN_PROGRESS)))
                throw new DBException(Constants.INVALID_RECORD);
        this.doRemove();
        this.getRecord().setEditMode(Constants.EDIT_NONE);
    }
    /**
     * Remove the current record.
     */
    public void doRemove() throws DBException
    {
    }
    /**
     * Read the record that matches this record's current key.
     * <p>NOTE: You do not need to Open to do a seek or addNew.
     * @param strSeekSign - Seek sign:
     * @return true if successful, false if not found.
     * @exception DBException FILE_NOT_OPEN.
     * @exception DBException KEY_NOT_FOUND - The key was not found on read.
     */
    public boolean seek(String strSeekSign) throws DBException
    {
        this.getRecord().setEditMode(Constants.EDIT_NONE);
        if (strSeekSign == null)
            strSeekSign = Constants.EQUALS;
        boolean bSuccess = this.doSeek(strSeekSign);
        if (bSuccess)
        {
            this.dataToFields(this.getRecord());
            this.setDataSource(null);
            this.getRecord().setEditMode(Constants.EDIT_CURRENT);
            return true;
        }
        else
            return false; // Not found
    }
    /**
     * Read the record that matches this record's current key.
     * @param strSeekSign The seek sign (defaults to '=').
     * @return true if successful.
     * Remember to set m_dataSource if dataToFields needs to move from the data object.
     * @exception DBException File exception.
     */
    public boolean doSeek(String strSeekSign) throws DBException
    {
        return false;
    }
    /**
     * Get the next record (return a null at EOF).
     * @param iRelPosition The relative records to move.
     * @return next FieldList or null if EOF.
     */
    public FieldList move(int iRelPosition) throws DBException
    {
        if (!m_bIsOpen)
            this.open();        // This will requery the table the first time
        this.getRecord().setEditMode(Constants.EDIT_NONE);
        int iRecordStatus = this.doMove(iRelPosition);
        if (iRecordStatus == Constants.NORMAL_RETURN)
        {
            this.dataToFields(this.getRecord());
            //  Usually, you would setDataSource(null), but if in hasNext(), you will need the dataSource.
            this.getRecord().setEditMode(Constants.EDIT_CURRENT);
            return m_record;
        }
        else
            return null;    // Usually EOF
    }
    /**
     * Get the next record (return a null at EOF).
     * Note: Remember to set the data source before returning a NORMAL_RETURN.
     * @param iRelPosition The relative records to move.
     * @return A record status (NORMAL_RETURN means the move was successful).
     */
    public int doMove(int iRelPosition) throws DBException
    {
        this.getRecord().setEditMode(Constants.EDIT_NONE);
        return Constants.ERROR_RETURN;  // You better override this.
    }
    /**
     * Does this list have a next record?
     * @return True if there is a next record.
     */
    public boolean hasNext() throws DBException
    {
        return false;
    }
    /**
     * Get the next record (return a null at EOF).
     * @return next FieldList or null if EOF.
     */
    public Object next() throws DBException
    {
        return this.move(Constants.NEXT_RECORD);
    }
    /**
     * Does this list have a previous record?
     * @return True if there is a previous record.
     */
    public boolean hasPrevious() throws DBException
    {
        return false;
    }
    /**
     * Get the previous record (return a null at BOF).
     * @return previous FieldList or null if BOF.
     */
    public Object previous() throws DBException
    {
        return this.move(Constants.PREVIOUS_RECORD);
    }
    /**
     * Get the row count.
     * @return The record count or -1 if unknown.
     */
    public int size()
    {
        return -1;
    }
    /**
     * Returns the record at this absolute row position.
     * Be careful, if a record at a row is deleted, this method will return a new
     * (empty) record, so you need to check the record status before updating it.
     * @param iRowIndex Absolute position of the record to retrieve.
     * @return The record at this location (or an Integer with the record status or null if not found).
     * @exception DBException File exception.
     */
    public Object get(int iRowIndex) throws DBException
    {
        if (!m_bIsOpen)
            this.open();
        this.getRecord().setEditMode(Constants.EDIT_NONE);
        Object objData = this.doGet(iRowIndex);
        if (objData instanceof Integer)
        {
            int iRecordStatus = ((Integer)objData).intValue();
            if (iRecordStatus == DELETED_RECORD.intValue())   // DBConstants.RECORD_NEW
            {   // Probably a deleted record (just return a blank record).
                this.addNew();
                this.getRecord().setEditMode(Constants.EDIT_NONE);   // Invalid record
                return m_record;
            }
            else if (iRecordStatus == EOF_RECORD.intValue())   // DBConstants.RECORD_NEW
            {
                return null;    // EOF
            }
        }
        else if (objData != null) 
        {
            this.setDataSource(objData);    //m_tableRemote.dataToFields();
            this.dataToFields(this.getRecord());
            // Usually you would setDataSource(null), but the datasource is required for remove, etc.
            this.getRecord().setEditMode(Constants.EDIT_CURRENT);
            return m_record;
        }
        return null;    // EOF
    }
    /**
     * Returns an attribute value for the cell at columnIndex and rowIndex.
     */
    public Object doGet(int iRowIndex) throws DBException
    {
        throw new DBException("get() not supported");
    }
    /**
     * Get a unique object that can be used to reposition to this record.
     * @exception DBException FILE_NOT_OPEN.
     * @exception DBException INVALID_RECORD - There is no current record.
     */
    public Object getHandle(int iHandleType) throws DBException   
    {
        return this.getRecord().getCounterField().getData();
    }
    /**
     * Reposition to this record Using this bookmark.
     * @param bookmark Bookmark.
     * @param iHandleType Type of handle (see getHandle).
     * @exception DBException FILE_NOT_OPEN.
     * @return record if found/null - record not found.
     */
    public FieldList setHandle(Object bookmark, int iHandleType) throws DBException
    {
        if (this.doSetHandle(bookmark, iHandleType))
            return this.getRecord();
        else
            return null;
    }
    /**
     * Reposition to this record using this bookmark.
     * @param bookmark The handle to use to position the record.
     * @param iHandleType The type of handle (DATA_SOURCE/OBJECT_ID,OBJECT_SOURCE,BOOKMARK).
     * @return  - true - record found/false - record not found
     * @exception DBException FILE_NOT_OPEN.
     * @exception DBException File exception.
     */
    public boolean doSetHandle(Object bookmark, int iHandleType) throws DBException
    {
        String strCurrentOrder = this.getRecord().getKeyName();
        this.getRecord().setKeyArea(Constants.PRIMARY_KEY);

        this.getRecord().getCounterField().setData(bookmark);
        boolean bSuccess = this.seek(Constants.EQUALS);

        this.getRecord().setKeyArea(strCurrentOrder);
        return bSuccess;
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
    public Object getLastModified(int iHandleType) throws DBException
    {
        return null;    // Override this to supply last modified.
    }
    /**
     * Setup a new data source.
     * Creates a new data source and sets it to the data source method variable.
     * Override this and code:
     * <pre>this.setDataSource(yournewdatasource);</pre>
     */
    public void setupNewDataSource()
    {
    }
    /**
     * Set the current data source.
     * Note: The record status is undefined.
     * @param dataSource The datasource to set.
     */
    public void setDataSource(Object dataSource)
    {
        m_dataSource = dataSource;
    }
    /**
     * Get the data source.
     * @return The current data source.
     */
    public Object getDataSource()
    {
        return m_dataSource;
    }
    /**
     * Look up this string in the resource table.
     * This is a convience method - calls getString in the task.
     * @param string The string key.
     * @return The local string (or the key if the string doesn't exist).
     */
    public String getString(String string)
    {
        if (this.getRecord() != null)
            if (this.getRecord().getTask() != null)
                string = this.getRecord().getTask().getString(string);
        return string;
    }
    /**
     * Get the remote table reference.
     * If you want the remote table session, call this method with Remote.class.
     * @classType The base class I'm looking for (If null, return the next table on the chain) 
     * @return The remote table reference.
     */
    public RemoteTarget getRemoteTableType(Class<?> classType)
    {
        return null;	// Override this
    }
    /**
     * Get the table's database.
     * @return The database.
     */
    public Database getDatabase()
    {
        return null;    // Override this
    }
}
