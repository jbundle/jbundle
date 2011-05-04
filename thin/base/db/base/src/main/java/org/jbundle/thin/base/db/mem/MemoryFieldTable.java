package org.jbundle.thin.base.db.mem;

import org.jbundle.model.DBException;
import org.jbundle.model.Rec;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabaseOwner;
import org.jbundle.thin.base.db.model.ThinPhysicalTableOwner;

/**
 * An MemoryFieldTable is a link to a memory-based raw data (PTable) table.
 * Generally, you won't want to use this class, but should use GridMemoryFieldTable
 * which overrides this class and adds the capability to handle the get() command.
 */
public class MemoryFieldTable extends BufferFieldTable
    implements ThinPhysicalDatabaseOwner, ThinPhysicalTableOwner
{
    /**
     * A reference to the raw data table.
     */
    protected PTable m_pTable = null;

    /**
     * Constructor.
     */
    public MemoryFieldTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The record to handle.
     */
    public MemoryFieldTable(FieldList record)
    {
        this();
        this.init(record, null, null);
    }
    /**
     * Constructor.
     * @param record The record to handle.
     * @param tableRemote The remote table.
     * @param dbOwner If you want this method to lookup/build the remote table, pass this.
     */
    public MemoryFieldTable(FieldList record, PTable tableRemote, PhysicalDatabaseParent dbOwner)
    {
        this();
        this.init(record, tableRemote, dbOwner);
    }
    /**
     * Constructor.
     * @param record The record to handle.
     * @param tableRemote The remote table.
     * @param server The remote server (only used for synchronization).
     */
    public void init(Rec record, PTable tableRemote, PhysicalDatabaseParent dbOwner)
    {
        super.init(record);
        this.setPTableRef(tableRemote, dbOwner);
    }
    /**
     * I'm done with the model, free the resources.
     */
    public void free()
    {
        if (m_pTable != null)
        {
            PDatabase pDatabase = m_pTable.getPDatabase();
            m_pTable.removePTableOwner(this, true);
            pDatabase.removePDatabaseOwner(this, true);
        }
        m_pTable = null;
        super.free();
    }
    /**
     * Reset the current position and open the file.
     */
    public void open() throws DBException
    {
        m_pTable.open(this);
        super.open();
    }
    /**
     * Close this table.
     */
    public void close()
    {
        m_pTable.close(this);
        super.close();
    }
    /**
     * Add this record to this table.
     * Add this record to the table and set the current edit mode to NONE.
     * @param record The record to add.
     * @throws Exception
     */
    public void doAdd(Rec record) throws DBException
    {
        m_pTable.add(this);
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
        return m_pTable.edit(this);  // Only call if edit is supported by remote db
    }
    /**
     * Set the current record to this (new) record.
     * NOTE: In the thin model, it is not necessary to call edit() before set.
     * @exception Throws an exception if there is no current record.
     */
    public void doSet(Rec record) throws DBException
    {
        m_pTable.set(this);
    }
    /**
     * Remove the current record.
     */
    public void doRemove() throws DBException
    {
        m_pTable.remove(this);
    }
    /**
     * Does this list have a next record?
     * @return true if there is a next record to read.
     */
    public boolean hasNext() throws DBException
    {
        Object record = this.next();
        if (record == null)
            return false;
        else
        {
            this.previous();        // Remember, Memory tables are FAAAAST!
            return true;
        }
    }
    /**
     * Get the next record (return a null at EOF).
     * Note: Remember to set the data source before returning a NORMAL_RETURN.
     * @param iRelPosition The relative records to move.
     * @return A record status (NORMAL_RETURN means the move was successful).
     */
    public int doMove(int iRelPosition) throws DBException
    {
        Object objData = m_pTable.move(iRelPosition, this);
        if (objData instanceof BaseBuffer)
        {
            this.setDataSource(objData);
            return Constants.NORMAL_RETURN;   // Normal return
        }
        else if (objData instanceof Number)
            return ((Number)objData).intValue();    // Usually EOF
        else
            return Constants.ERROR_RETURN;  // Never
    }
    /**
     * Does this list have a previous record?
     * @return true if there is a previous record to read.
     */
    public boolean hasPrevious() throws DBException
    {
        Object record = this.previous();
        if (record == null)
            return false;
        else
        {
            this.next();        // Remember, Memory tables are FAAAAST!
            return true;
        }
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
        Object objData = m_pTable.seek(strSeekSign, this);
        if (objData instanceof BaseBuffer)
        {
            this.setDataSource(objData);
            return true;    // Success
        }
        else if (objData instanceof Boolean)
            return ((Boolean)objData).booleanValue();
        else
            return false; // Never
    }
    /**
     * Returns the record at this absolute row position.
     * Be careful, if a record at a row is deleted, this method will return a new
     * (empty) record, so you need to check the record status before updating it.
     * @param iRelPosition Absolute position of the record to retrieve.
     * @return The record at this location (or an Integer with the record status or null if not found).
     * @exception DBException File exception.
     */
    public Object doGet(int iRowIndex) throws DBException
    {
        return null;
    }
    /**
     * Set the raw data table reference.
     * @param pTable The raw data table.
     * @param dbOwner If you want this method to lookup/build the remote table, pass this.
     */
    public void setPTableRef(PTable pTable, PhysicalDatabaseParent dbOwner)
    {
        m_pTable = pTable;
        if (pTable == null)
            if (dbOwner != null)
        {
            FieldList record = this.getRecord();
            if (record != null)
            {
                PDatabase pDatabase = dbOwner.getPDatabase(record.getDatabaseName(), ThinPhysicalDatabase.MEMORY_TYPE, true);
                pDatabase.addPDatabaseOwner(this);
                if (pDatabase != null)
                    pTable = pDatabase.getPTable(record, true);
                pTable.addPTableOwner(this);
                m_pTable = pTable;
            }
        }
    }
    /**
     * Set the raw data table reference.
     * @return The raw data table.
     */
    public PTable getPTable()
    {
        return m_pTable;
    }
    /**
     * Set the ptable that I am an owner of.
     * @param pTable
     */
    public void setPDatabase(PDatabase pDatabase)
    {
    }
    /**
     * Set the ptable that I am an owner of.
     * @param pTable
     */
    public void setPTable(PTable pTable)
    {
        m_pTable = pTable;
    }
}
