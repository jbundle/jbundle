package org.jbundle.model;


/**
 * List of Records (FieldList[s]).
 */
public interface Table
    extends Freeable
{
    /**
     * Constructor.
     * @param record The initial record for this table.
     */
    public void init(Rec record);
    /**
     * Move all the fields to the output buffer.
     * @exception Exception File exception.
     */
    public void fieldsToData(Rec record) throws DBException;
    /**
     * Move this Field's data to the record area (Override this for non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the data from.
     * @exception DBException File exception.
     */
    public void fieldToData(Field field) throws DBException;
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
    public int dataToFields(Rec record) throws DBException;
    /**
     * Move the output buffer to this field (Override this to use non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the current data to.
     * @return The error code (From field.setData()).
     * @exception DBException File exception.
     */
    public int dataToField(Field field) throws DBException;
    /**
     * Get the record for this table.
     * @return The record.
     */
    public Rec getRecord();
    /**
     * Set this field's Record.
     * @param record The Record.
     */
    public void setRecord(Rec record);
        // Here are the access operations:
    /**
     * Reset the current position and open the file.
     * Note: This is called automatically on your first file access.
     */
    public void open() throws DBException;
    /**
     * Close the file.
     * Reset the current position.
     */
    public void close();
    /**
     * Create a new empty record.
     * Discard the current fields, init all the fields and display them,
     * and set the status to EDIT_ADD.
     */
    public void addNew() throws DBException;
    /**
     * Add this record to this table.
     * Add this record to the table and set the current edit mode to NONE.
     * @param record The record to add.
     * @throws Exception
     */
    public void add(Rec record) throws DBException;
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
    public int edit() throws DBException;
    /**
     * Set the current record to this (new) record.
     * NOTE: In the thin model, it is not necessary to call edit() before set.
     * @exception Throws an exception if there is no current record.
     */
    public void set(Rec record) throws DBException;
    /**
     * Remove the current record.
     */
    public void remove() throws DBException;
    /**
     * Read the record that matches this record's current key.
     * <p>NOTE: You do not need to Open to do a seek or addNew.
     * @param strSeekSign - Seek sign:
     * @return true if successful, false if not found.
     * @exception FILE_NOT_OPEN.
     * @exception KEY_NOT_FOUND - The key was not found on read.
     */
    public boolean seek(String strSeekSign) throws DBException;
    /**
     * Get the next record (return a null at EOF).
     * @param iRelPosition The relative records to move.
     * @return next Rec or null if EOF.
     */
    public Rec move(int iRelPosition) throws DBException;
    /**
     * Does this list have a next record?
     * @return True if there is a next record.
     */
    public boolean hasNext() throws DBException;
    /**
     * Get the next record (return a null at EOF).
     * @return next Rec or null if EOF.
     */
    public Object next() throws DBException;
    /**
     * Does this list have a previous record?
     * @return True if there is a previous record.
     */
    public boolean hasPrevious() throws DBException;
    /**
     * Get the previous record (return a null at BOF).
     * @return previous Rec or null if BOF.
     */
    public Object previous() throws DBException;
    /**
     * Get the row count.
     * @return The record count or -1 if unknown.
     */
    public int size();
    /**
     * Returns the record at this absolute row position.
     * Be careful, if a record at a row is deleted, this method will return a new
     * (empty) record, so you need to check the record status before updating it.
     * @param iRelPosition Absolute position of the record to retrieve.
     * @return The record at this location (or an Integer with the record status or null if not found).
     * @exception DBException File exception.
     */
    public Object get(int iRowIndex) throws DBException;
    /**
     * Get a unique object that can be used to reposition to this record.
     * @exception FILE_NOT_OPEN.
     * @exception INVALID_RECORD - There is no current record.
     */
    public Object getHandle(int iHandleType) throws DBException;
    /**
     * Reposition to this record Using this bookmark.
     * @param Object bookmark Bookmark.
     * @param int iHandleType Type of handle (see getHandle).
     * @exception FILE_NOT_OPEN.
     * @return record if found/null - record not found.
     */
    public Rec setHandle(Object bookmark, int iHandleType) throws DBException;
    /**
     * Get the Handle to the last modified or added record.
     * This uses some very inefficient (and incorrect) code... override if possible.
     * NOTE: There is a huge concurrency problem with this logic if another person adds
     * a record after you, you get the their (wrong) record, which is why you need to
     * provide a solid implementation when you override this method.
     * @param iHandleType The handle type.
     * @return The bookmark.
     */
    public Object getLastModified(int iHandleType) throws DBException;
    /**
     * Setup a new data source.
     * Creates a new data source and sets it to the data source method variable.
     * Override this and code:
     * <pre>this.setDataSource(yournewdatasource);</pre>
     */
    public void setupNewDataSource();
    /**
     * Set the current data source.
     * Note: The record status is undefined.
     * @param dataSource The datasource to set.
     */
    public void setDataSource(Object dataSource);
    /**
     * Get the data source.
     * @return The current data source.
     */
    public Object getDataSource();
    /**
     * Look up this string in the resource table.
     * This is a convience method - calls getString in the task.
     * @param string The string key.
     * @return The local string (or the key if the string doesn't exist).
     */
    public String getString(String string);
    /**
     * Get the remote table reference.
     * If you want the remote table session, call this method with java.rmi.server.RemoteStub.class.
     * @classType The base class I'm looking for (If null, return the next table on the chain) 
     * @return The remote table reference.
     */
    public Object getRemoteTableType(Class<?> classType);
}
