/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.physical;

/**
 * @(#)PhysicalTable.java 1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.DatabaseException;
import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.model.ThinPhysicalTableOwner;


/**
 * This is the base table class for tables which use physical memory space.
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public class PhysicalTable extends BaseTable
    implements ThinPhysicalTableOwner
{
    /**
     * Pointer to the raw data table.
     */
    protected PTable m_pTable = null;
    /**
     * Last modified bookmark. This is the hint returned from remote add call and returned on getLastModified.
     */
    protected Object m_LastModifiedBookmark = null;

    /**
     * Constructor (Don't call this one).
     */
    public PhysicalTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public PhysicalTable(BaseDatabase database, Record record)
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
        m_pTable = null;
        super.init(database, record);
        m_pTable = ((PhysicalDatabase)database).getPDatabase().getPTable(record, true);
        m_pTable.addPTableOwner(this);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
        try
        {
            if (this.getHandle(DBConstants.DATA_SOURCE_HANDLE) != null)
                ((BaseBuffer)this.getHandle(DBConstants.DATA_SOURCE_HANDLE)).free();
        } catch (DBException ex)    {
            // Never
        }
        if (m_pTable != null)
            m_pTable.removePTableOwner(this, true);      // Free or decrement the use count of the physical table
        m_pTable = null;
    }
    /**
     * Set the ptable that I am an owner of.
     * @param pTable
     */
    public void setPTable(PTable pTable)
    {
        m_pTable = pTable;
    }
    /**
     * Move the output buffer to this field (Override this to use non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data (Move ALL for Physicals)!
     * @param field The field to move the current data to.
     * @return The error code (From field.setData()).
     * @exception DBException File exception.
     */
    public int dataToField(Field field) throws DBException
    {
        return ((BaseBuffer)this.getHandle(DBConstants.DATA_SOURCE_HANDLE)).getNextField((FieldInfo)field, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
    }
    /**
     * Move the data source buffer to all the fields.<p>
     *  Make sure you do the following steps:
     *  1) Move the data fields to the correct record data fields.
     *  2) Save the data source or set to null, so I can cache the source if necessary.
     *  3) Save the objectID if it is not an Integer type, so I can serialize the source of this object.
     * @return The error code.
     * @exception DBException File exception.
     */
    public int dataToFields(Rec record) throws DBException
    {
        if (((BaseBuffer)this.getHandle(DBConstants.DATA_SOURCE_HANDLE)) == null)
            throw new DBException(DBConstants.INVALID_RECORD);
        ((BaseBuffer)this.getHandle(DBConstants.DATA_SOURCE_HANDLE)).resetPosition();
        return super.dataToFields(record);
    }
    /**
     * Move all the fields to the output buffer.
     * @exception DBException File exception.
     */
    public void fieldsToData(Rec record) throws DBException
    {
        BaseBuffer buffer = this.getNewDataBuffer();
        this.doSetHandle(buffer, DBConstants.DATA_SOURCE_HANDLE); // This way you can retrieve the current buffer.
        super.fieldsToData(record);
        buffer.finishBuffer();
    }
    /**
     * Move this Field's data to the record area (Override this for non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @param field The field to move the current data to.
     * @exception DBException File exception.
     */
    public void fieldToData(Field field) throws DBException
    {
        ((BaseBuffer)this.getHandle(DBConstants.DATA_SOURCE_HANDLE)).addNextField((FieldInfo)field);
    }
    /**
     * Create a new output buffer.
     * @return BaseBuffer - The new (data) buffer.
     */
    public BaseBuffer getNewDataBuffer()
    {
        return new VectorBuffer(null);
    }
    /**
     * Open this table (requery the table).
     * @exception DBException File exception.
     */
    public void doOpen() throws DBException
    {
        super.doOpen();
        try   {
            m_pTable.open(this);
        } catch (DBException ex)    {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Close this table.
     */
    public void close()
    {
        super.close();
        if (m_pTable != null)
            m_pTable.close(this);
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * @exception DBException File exception.
     */
    public void doAddNew() throws DBException
    {
        try   {
            m_pTable.addNew(this);
        } catch (DBException ex)    {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Add/Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void doAdd(Record record) throws DBException
    {
        m_LastModifiedBookmark = null;
        try   {
            m_pTable.add(this);
            m_LastModifiedBookmark = this.getHandle(DBConstants.BOOKMARK_HANDLE);
        } catch (DBException ex)    {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Lock the current record.
     * This method responds differently depending on what open mode the record is in:
     * OPEN_DONT_LOCK - A physical lock is not done. This is usually where deadlocks are possible
     * (such as screens) and where transactions are in use (and locks are not needed).
     * OPEN_LOCK_ON_EDIT - Holds a lock until an update or close. (Update crucial data, or hold records for processing)
     * Returns false is someone already has a lock on this record.
     * OPEN_WAIT_FOR_LOCK - Don't return from edit until you get a lock. (ie., Add to the total).
     * Returns false if someone has a hard lock or time runs out.
     * @return true if successful, false is lock failed.
     * @exception DBException FILE_NOT_OPEN
     * @exception DBException INVALID_RECORD - Record not current.
     */
    public int doEdit() throws DBException
    {
        try   {
            int iErrorCode = m_pTable.edit(this);
            if (iErrorCode == DBConstants.NORMAL_RETURN)
                if (this.lockOnDBTrxType(null, DBConstants.LOCK_TYPE, true))
                    iErrorCode = this.lockCurrentRecord();		// Lock the record using the lock manager
            return iErrorCode;
        } catch (DBException ex)    {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Add/Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void doSet(Record record) throws DBException
    {
        try   {
            m_pTable.set(this);
        } catch (DBException ex)    {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Delete this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void doRemove() throws DBException
    {
        try   {
            m_pTable.remove(this);
        } catch (DBException ex)    {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Move the position of the record.
     * @exception DBException File exception.
     */
    public int doMove(int iRelPosition) throws DBException
    {
        try   {
            BaseBuffer buffer = m_pTable.move(iRelPosition, this);
            this.doSetHandle(buffer, DBConstants.DATA_SOURCE_HANDLE);
            int iRecordStatus = DBConstants.RECORD_NORMAL;
            if (buffer == null) if ((iRelPosition < 0) || (iRelPosition == DBConstants.LAST_RECORD))
                iRecordStatus |= DBConstants.RECORD_AT_BOF;
            if (buffer == null) if ((iRelPosition >= 0) || (iRelPosition == DBConstants.FIRST_RECORD))
                iRecordStatus |= DBConstants.RECORD_AT_EOF;
            return iRecordStatus;
        } catch (DBException ex)    {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Read the record that matches this record's current key.
     * @param strSeekSign - Seek sign:
     * @return true if successful, false if not found.
     * @exception DBException File exception.
     */
    public boolean doSeek(String strSeekSign) throws DBException
    {
        try   {
            BaseBuffer buffer = m_pTable.seek(strSeekSign, this);
            this.doSetHandle(buffer, DBConstants.DATA_SOURCE_HANDLE);
            return (buffer != null);
        } catch (DBException ex)    {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Read the record given the ID to this persistent object.
     * Note: You can't use an OBJECT_ID handle, as these tables are non-persistent.
     * @param objectID java.lang.Object The handle to lookup.
     * @return true if found.
     * @exception DBException File exception.
     */
    public boolean doSetHandle(Object bookmark, int iHandleType) throws DBException   
    {
        try   {
            if (iHandleType == DBConstants.OBJECT_ID_HANDLE)
            {
                return super.doSetHandle(bookmark, iHandleType);
//?                throw new DBException("Object IDs are not supported for PhysicalTables (non persistent)");
            }
            else
                return super.doSetHandle(bookmark, iHandleType);
        } catch (DBException ex)    {
            throw DatabaseException.toDatabaseException(ex);
        }
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
        if ((iHandleType & DBConstants.OBJECT_ID_HANDLE) == DBConstants.OBJECT_ID_HANDLE)
            iHandleType = DBConstants.BOOKMARK_HANDLE;
        if (iHandleType == DBConstants.BOOKMARK_HANDLE)
            if (m_LastModifiedBookmark != null)
                return m_LastModifiedBookmark;      // Ignore the iHandleType (Always bookmark type for JDBC)
        return super.getLastModified(iHandleType);
    }
    /**
     * Get the record count.
     * @return The record count.
     */
    public int getRecordCount()
    {
        return m_pTable.getRecordCount(this);
    }
}
