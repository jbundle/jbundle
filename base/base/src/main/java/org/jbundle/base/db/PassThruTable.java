/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;


/**
 * PassThruTable - This class has several uses.
 * 1) An abstract class that is used for classes wanting
 *      to pass most commands on to a target table, but likely to change some
 *      functionality. (ie., a grid table adds MoveTo)
 * 2) A Pass-thru for commands directed at another table, but where changes in the table
 *      are directed at the table's record, rather than this object's record.
 *      (ie., A Table used to fake a multi-table query; pass all commands to the main table).
 * 3) A Pass-thru for handling multiple tables.
 *      (ie., A table for faking object processing; pass navigation commands to the current table)
 */
public class PassThruTable extends BaseTable
{

    /**
     * Next table in the chain (Used to setup/break-down the link).
     */
    protected BaseTable m_tableNext = null;
    /**
     * List of all linked tables.
     */
    protected Map<Object,BaseTable> m_mapTable = null;
    
    /**
     * PassThruTable Constructor.
     */
    public PassThruTable()
    {
        super();
    }
    /**
     * PassThruTable Constructor.
     * @param database Should be null, as the last table on the chain contains the database.
     * @param record The record's current table will be changed to this table and moved down my list.
     */
    public PassThruTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * Initialize the table.
     * Adds the record's current table to my chain, and sets the record's table to this new table.
     * @param database Should be null, as the last table on the chain contains the database.
     * @param record The record's current table will be changed to this table and moved down my list.
     */
    public void init(BaseDatabase database, Record record)
    {
        m_tableNext = null;
        database = null;    // Use the database of the last table on the chain!
        super.init(database, record);
        if (record.getTable() != this)
            m_tableNext = record.getTable();
        record.setTable(this);  // Make sure the record sees me first
    }
    /**
     * Free this passthrutable and all linked tables in this chain.
     */
    public void free()
    {
        if (m_mapTable != null)
        {
            Iterator<BaseTable> iterator = this.getTables();
            while (iterator.hasNext())
            {
                BaseTable table = iterator.next();
                if ((table != null) && (table != this.getNextTable()))
                {
                    Record record = table.getRecord();
                    if (record != null)
                        record.free();
                }
            }
            m_mapTable.clear();
            m_mapTable = null;
        }
        if (m_tableNext != null)
        {
            BaseTable baseTable = m_tableNext;
            m_tableNext = null;   // This will prevent the record from being freed (freed in prev. line)
            baseTable.free(); // This will also free the record
            m_record = null;    // Being paranoid
        }
        super.free();
    }
    /**
     * Get the next table in the chain.
     */
    public BaseTable getNextTable()
    {
        return m_tableNext;     // Current table
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * <p />NOTE: This is where the notification message is sent after an ADD, DELETE, or UPDATE.
     * @param field If the change is due to a field, pass the field.
     * @param iChangeType The type of change.
     * @param bDisplayOption If true display the fields on the screen.
     * @return An error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)     // init this field override for other value
    {
        if (this.getNextTable() != null)
            return this.getNextTable().doRecordChange(field, iChangeType, bDisplayOption);  // Pass it on.
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }
    /**
     * Add a listener to the chain.
     * The listener must be cloned and added to all records on the list.
     * @param listener The listener to add.
     */
    public void addListener(Record record, FileListener listener)
    {
        if (this.getNextTable() != null)
            this.getNextTable().addListener(record, listener);
    }
    /**
     * Is this table open?
     * @return true if next next table is open.
     */
    public boolean isOpen()
    {
        if (this.getNextTable() == null)
            return false;
        return this.getNextTable().isOpen();
    }
    /**
     * Do the physical Open on this table (requery the table).
     * @exception DBException File exception.
     */
    public void open() throws DBException
    {
        this.getNextTable().open();
    }
    /**
     * Close the all the recordsets in the list.
     */
    public void close()
    {
        if (m_mapTable != null)
        {
            Iterator<BaseTable> iterator = this.getTables();
            while (iterator.hasNext())
            {
                BaseTable table = iterator.next();
                if ((table != null) && (table != this.getNextTable()))
                {
                    Record record = table.getRecord();
                    if (record != null)
                        record.close();
                }
            }
        }
        if (m_tableNext != null)
            m_tableNext.close();
        super.close();
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * @exception DBException File exception.
     */
    public void addNew() throws DBException
    {
        this.getNextTable().addNew();
    }
    /**
     * Add this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void add(Rec fieldList) throws DBException
    {
        this.getNextTable().add(fieldList);
    }
    /**
     * Delete this record (Always called from the record class).
     * Always override this method.
     * @exception DBException File exception.
     */
    public void remove() throws DBException
    {
        this.getNextTable().remove();
    }
    /**
     * Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void set(Rec fieldList) throws DBException
    {
        this.getNextTable().set(fieldList);
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
        return this.getNextTable().edit();
    }
    /**
     * Is the record at the Start of the file?
     * @return true if file position is one before the first record.
     */
    public boolean isBOF()
    {
        if (this.getNextTable() != null)
            return this.getNextTable().isBOF();
        else
            return super.isBOF();   // Rarely
    }
    /**
     * Is the record at the End of the file?
     * @return true if file position is one after the last record.
     */
    public boolean isEOF()
    {
        if (this.getNextTable() != null)
            return this.getNextTable().isEOF();
        else
            return super.isEOF();   // Rarely
    }
    /**
     * Is the first record in the file?
     * @return false if file position is at first record.
     */
    public boolean hasPrevious() throws DBException
    {
        if (this.getNextTable() != null)
            return this.getNextTable().hasPrevious();
        else
            return super.hasPrevious();   // Rarely
    }
    /**
     * Is there another record (is this not the last one)?
     */
    public boolean hasNext() throws DBException
    {
        if (this.getNextTable() != null)
            return this.getNextTable().hasNext();
        else
            return super.hasNext();   // Rarely
    }
    /**
     * Move the position of the record.
     * @exception DBException File exception.
     */
    public FieldList move(int iRelPosition) throws DBException
    {
        if (this.getNextTable() != null)
            return this.getNextTable().move(iRelPosition);
        else
            return super.move(iRelPosition);
    }
    /**
     * Read the record that matches this record's current key.<p>
     * @exception DBException File exception.
     */
    public boolean seek(String strSeekSign) throws DBException
    {
        return this.getNextTable().seek(strSeekSign);
    }
    /**
     * Get the ObjectIDHandle to the last modified or added record.
     * This uses some very inefficient code... override if possible.
     */
    public Object getLastModified(int iHandleType)
    {
        return this.getNextTable().getLastModified(iHandleType);
    }
    /**
     * Get a unique key that can be used to reposition to this record.
     * @exception DBException File exception.
     */
    public Object getHandle(int iHandleType) throws DBException
    {
        return this.getNextTable().getHandle(iHandleType);
    }
    /**
     * Move all the fields to the output buffer.
     * @exception DBException File exception.
     */
    public void fieldsToData(Rec record) throws DBException
    {
        this.getNextTable().fieldsToData(record);
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
        this.getNextTable().fieldToData(field);
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
        return this.getNextTable().dataToFields(record);
    }
    /**
     * Move the output buffer to this field (Override this to use non-standard buffers).
     * Warning: Check to make sure the field.isSelected() before moving data!
     * @exception DBException File exception.
     */
    public int dataToField(Field field) throws DBException
    {
        return this.getNextTable().dataToField(field);
    }
    /**
     * Set up/do the local criteria.
     * This is only here to accomodate local file systems that can't handle
     * REMOTE criteria. All you have to do here to handle the remote criteria
     * locally is to call: return record.handleRemoteCriteria(xx, yy).
     */
    public boolean doLocalCriteria(StringBuffer strFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {   // Default BaseListener
        return this.getNextTable().doLocalCriteria(strFilter, bIncludeFileName, vParamList);    // If can't handle remote
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * @exception DBException File exception.
     */
    public void doAddNew() throws DBException
    {
        throw new DatabaseException("Called an unreachable statement - DoAddNew");
    }
    /**
     * Delete this record (Always called from the record class).
     * Always override this method.
     * @exception DBException File exception.
     */
    public void doRemove() throws DBException
    {
        throw new DatabaseException("Called an unreachable statement - DoDelete");
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
    public int doEdit() throws DBException
    {
        throw new DatabaseException("Called an unreachable statement - DoEdit");
    }
    /**
     * Move the position of the record.
     * @exception DBException File exception.
     */
    public int doMove(int iRelPosition) throws DBException
    {
        throw new DatabaseException("Called an unreachable statement - doMove");
    }
    /**
     * Read the record that matches this record's current key.<p>
     * @exception DBException File exception.
     */
    public boolean doSeek(String strSeekSign) throws DBException
    {
        throw new DatabaseException("Called an unreachable statement - DoSeek");
    }
    /**
     * Reposition to this record Using this bookmark.
     * @exception DBException File exception.
     */
    public boolean doSetHandle(Object bookmark, int iHandleType) throws DBException
    {
        return this.getNextTable().doSetHandle(bookmark, iHandleType);
    }
    /**
     * Add/Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void doAdd(Record record) throws DBException
    {
        throw new DatabaseException("Called an unreachable statement - DoAdd");
    }
    /**
     * Add/Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void doSet(Record record) throws DBException
    {
        throw new DatabaseException("Called an unreachable statement - DoSet");
    }
    /**
     * Get the current table target.
     * This is usually used to keep track of the current table in Move(s).
     * (ie., If this is a table for the "Animal" record, m_CurrentRecord
     * could be an "Cat" table object).
     */
    public BaseTable getCurrentTable()
    {
        return this.getNextTable().getCurrentTable();   // Current table
    }
    /**
     * Get the table at this position in the table list.
     * @param iIndex The index to retrieve the table from.
     * @return The table at this location.
     */
    public BaseTable getTableAt(Object objKey)
    {
        if (m_mapTable != null)
            if (objKey != null)
                return (BaseTable)m_mapTable.get(objKey);
        return null;
    }
    /**
     * Get the number of tables on the table list.
     * @return The table count.
     */
    public Iterator<BaseTable> getTables()
    {
        if (m_mapTable != null)
            return m_mapTable.values().iterator();
        return new EmptyIterator();
    }
    class EmptyIterator extends Object
        implements Iterator<BaseTable>
    {
        public EmptyIterator() {
            super();
        }
        public boolean hasNext() {
            return false;
        }
        public BaseTable next() {
            return null;
        }
        public void remove() {
        }
    }
    /**
     * Set the main (base) record for this table's class.
     * @param record The record to set.
     */
    public void setRecord(Rec record)
    {
        if (m_tableNext != null)
            m_tableNext.setRecord(record);  // Must be synchronized
        super.setRecord(record);
    }
    /**
     * Add this record's table to the list of tables to pass commands to.
     * @param table The table to add.
     */
    public void addTable(BaseTable table)
    {
        this.addTable(null, table);
        table.close();      // Make sure the table starts in a known state
    }
    /**
     * Add this record's table to the list of tables to pass commands to.
     * @param table The table to add.
     */
    public void addTable(Object objKey, BaseTable table)
    {
        if (m_mapTable == null)
            m_mapTable = new Hashtable<Object,BaseTable>();
        if (objKey == null)
            objKey = new Integer(m_mapTable.size());
        m_mapTable.put(objKey, table);
    }
    /**
     * Remove this table from this table list.
     * @param table The table to remove.
     */
    public boolean removeTable(BaseTable table)
    {
        if (m_mapTable != null)
        {
            for (Object objKey : m_mapTable.keySet())
            {
                if (table == m_mapTable.get(objKey))
                {
                    return (m_mapTable.remove(objKey) != null);
                }                
            }
        }
        else
        {
            if (this.getNextTable() != null)
                return this.getNextTable().removeTable(table);
        }
        return super.removeTable(table);
    }
    /**
     * Get the table's database.
     * @return The database for this table.
     */
    public BaseDatabase getDatabase()
    {
        BaseDatabase database = super.getDatabase();
        if (database == null)
            if (this.getNextTable() != null)
                database = this.getNextTable().getDatabase();
        return database;
    }
    
    //------------- These are utility methods that are often used with multiple tables: ----------
    
    /**
     * Copy the fields from the (main) source to the (mirrored) destination record.
     * This is done before any write or set.
     * @param recAlt Destination record
     * @param recMain Source record
     */
    public void copyKeys(Record recAlt, Record recMain, int iKeyArea)
    {
        BaseBuffer vector = new VectorBuffer(null);
        KeyArea keyMain = recMain.getKeyArea(iKeyArea);
        KeyArea keyAlt = recAlt.getKeyArea(iKeyArea);
        keyMain.setupKeyBuffer(vector, DBConstants.FILE_KEY_AREA);
        keyAlt.reverseKeyBuffer(vector, DBConstants.FILE_KEY_AREA);     // Move these keys back to the record
        vector.free();
    }
    /**
     * Sync the current record's contents and status to the base record
     */
    public void syncRecordToBase(Record recBase, Record recAlt)
    {
        if ((recAlt != null) && (recBase != null))
        {
            recBase.moveFields(recAlt, null, true, DBConstants.READ_MOVE, false, false, true);
            recBase.setEditMode(recAlt.getEditMode());
        }
        if ((recBase.getEditMode() == DBConstants.EDIT_CURRENT) || (recBase.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
            recBase.handleValidRecord(); // Do listeners, Display Fields
        else if (recBase.getEditMode() == DBConstants.EDIT_ADD)
            recBase.handleNewRecord(); // Do listeners, Display Fields
    }
}
