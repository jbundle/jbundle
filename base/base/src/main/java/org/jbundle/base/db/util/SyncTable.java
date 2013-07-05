/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.util;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.PassThruTable;
import org.jbundle.base.db.Record;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldList;


/**
 * SyncTable - An Override of BaseTable that adds synchronized to the overriding table.
 */
public class SyncTable extends PassThruTable {
    

    /**
     * RecordList Constructor.
     */
    public SyncTable()
    {
        super();
    }
    /**
     * RecordList Constructor.
     */
    public SyncTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * addNew - Create/Clear the current object (Always called from the record class).
     * @exception DBException File exception.
     */
    synchronized public void addNew() throws DBException    {
        super.addNew();
    }
    /**
     * Close - Close the recordset.
     */
    synchronized public void close()
    {
        super.close();
    }
    /**
     * bufferToFields.
     * @exception DBException File exception.
     */
    synchronized public int dataToFields(Rec record) throws DBException
    {
        return super.dataToFields(record);
    }
    /**
     * Delete - Delete this record (Always called from the record class).
     * @exception DBException File exception.
     */
    synchronized public void remove() throws DBException
    {
        super.remove();
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
    synchronized public int edit() throws DBException
    {
        return super.edit();
    }
    /**
     * fieldsToBuffer.
     * @exception DBException File exception.
     */
    synchronized public void fieldsToData(Rec record) throws DBException
    {
        super.fieldsToData(record);
    }
    /**
     * init.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);
    }
    /**
     * Is the first record in the file?
     * @return false if file position is at first record.
     */
    synchronized public boolean hasPrevious() throws DBException
    {
        return super.hasPrevious();
    }
    /**
     * Is there another record (is this not the last one)?
     */
    synchronized public boolean hasNext() throws DBException
    {
        return super.hasNext();
    }
    /**
     * Move the position of the record.
     * @exception DBException File exception.
     */
    synchronized public FieldList move(int iRelPosition) throws DBException
    {
        return super.move(iRelPosition);
    }
    /**
     * Do the physical Open on this table (requery the table).
     * @exception DBException File exception.
     */
    synchronized public void open() throws DBException  {
        super.open();
    }
    /**
     * Read the record that matches this record's current key.<p>
     * @exception DBException File exception.
     */
    synchronized public boolean seek(String strSeekSign) throws DBException
    {
        return super.seek(strSeekSign);
    }
    /**
     * Reposition to this record Using this bookmark.
     * @exception DBException File exception.
     */
    synchronized public boolean doSetHandle(Object bookmark, int iHandleType) throws DBException
    {
        return super.doSetHandle(bookmark, iHandleType);
    }
    /**
     * Add this record (Always called from the record class).
     * @exception DBException File exception.
     */
    synchronized public void add(Rec fieldList) throws DBException
    {
        super.add(fieldList);
    }
    /**
     * Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    synchronized public void set(Rec fieldList) throws DBException
    {
        super.set(fieldList);
    }
}
