package org.jbundle.base.db.shared;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 * NOTE: Once you resolve the bookmark dilemma in BaseTable, fix GetAtRecordClose.
 */
import java.util.Iterator;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.grid.DataRecord;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.Rec;
import org.jbundle.thin.base.db.FieldList;


/**
 * Access multiple tables as if they were overrides of the base record's table.
 * This is typically used to access multiple tables from the same overriding class,
 * such as a Dog and Cat class from an Animal class.
 */
public class MultiTable extends BaseSharedTable
{
    
    /**
     * Get the current table target.
     * @return The current table.
     */
    public BaseTable getCurrentTable()
    {
        return m_tableCurrent;      // Current table
    }
    /**
     * Get the next table in the chain.
     * @return The next table in the chain (or the current table if non-null).
     */
    public BaseTable getNextTable()
    {
        if (this.getCurrentTable() != null)
            return this.getCurrentTable();
        return super.getNextTable();        // Current table
    }

    /**
     * MultiTable Constructor.
     */
    public MultiTable()
    {
        super();
    }
    /**
     * MultiTable Constructor.
     */
    public MultiTable(BaseDatabase database, Record record)
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
        record.addTables(record.getRecordOwner());          // Add any query records or overriding records
        Iterator<BaseTable> tables = this.getTables();
        while (tables.hasNext())
        {
            if (record.getRecordOwner() != null)
                record.getRecordOwner().removeRecord(tables.next().getRecord());    // Remove from the recordowner
        }
    }
    /**
     * free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Do the physical Open on this table (requery the table).
     * Note - ignore's open because open is used as a flag to indicate moveNext in process.
     */
    public void open() throws DBException
    {
        // Ignore open - wait for move(+1);
        // 1. Synchronize selection:
        Record record = this.getRecord();
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            Record recAlt = table.getRecord();
            if (record.isAllSelected())
                recAlt.setSelected(true);
            else
            {
                recAlt.setSelected(false);
                for (int iFieldSeq = 0; iFieldSeq < record.getFieldCount(); iFieldSeq++)
                {
                    if (record.getField(iFieldSeq).isSelected())
                        recAlt.getField(iFieldSeq).setSelected(true);
                }
            }
        }
        this.doOpen();      // Set isOpen flag to true.
    }
    /**
     * Close this table.
     */
    public void close()
    {
        super.close();
    }
    /**
     * Is the last record in the file?
     * @return false if file position is at last record.
     */
    public boolean hasNext() throws DBException
    {
        return this.doHasNext();
    }
    /**
     * Move the position of the record.
     * @exception DBException File exception.
     */
    public FieldList move(int iRelPosition) throws DBException
    {
        return this.moveThruMultipleTables(iRelPosition);
    }
    /**
     * Reposition to this record Using this bookmark.
     * @exception DBException File exception.
     */
    public FieldList setHandle(Object bookmark, int iHandleType) throws DBException
    {
        if ((iHandleType == DBConstants.OBJECT_ID_HANDLE) || (iHandleType == DBConstants.BOOKMARK_HANDLE))
        {
            BaseTable table = null;
            Object strTable = this.getInfoFromHandle(bookmark, true, iHandleType);
            Iterator<BaseTable> iterator = this.getTables();
            while (iterator.hasNext())
            {
                table = iterator.next();
                if (strTable.equals(table.getRecord().getHandle(DBConstants.OBJECT_SOURCE_HANDLE)))
                    break;
            }
            bookmark = this.getInfoFromHandle(bookmark, false, iHandleType);
            this.setCurrentTable(table);
        }
        else if (iHandleType == DBConstants.DATA_SOURCE_HANDLE)
        {
            BaseTable table = ((FullDataSource)bookmark).getTable();
            bookmark = ((FullDataSource)bookmark).getDataSource();
            this.setCurrentTable(table);
        }
        FieldList record = null;
        BaseTable table = this.getCurrentTable();
        if (table != null)
            record = table.setHandle(bookmark, iHandleType);
        else
            record = null;
        this.syncCurrentToBase();
        return record;
    }
    /**
     * Sync the current record's contents and status to the base record
     */
    public void syncCurrentToBase()
    {
        BaseTable table = this.getCurrentTable();
        if (table != null)
        {
            Record recCurrent = table.getRecord();
            Record recBase = (Record)m_record;
            this.syncRecordToBase(recBase, recCurrent);
        }
    }
    /**
     * Get the table or object ID portion of the bookmark.
     * @exception DBException File exception.
     */
    public Object getInfoFromHandle(Object bookmark, boolean bGetTable, int iHandleType) throws DBException
    {
        if (iHandleType == DBConstants.OBJECT_ID_HANDLE)
        {
            if (!(bookmark instanceof String))
                return null;
            int iLastColon = ((String)bookmark).lastIndexOf(BaseTable.HANDLE_SEPARATOR);
            if (iLastColon == -1)
                return null;
            if (bGetTable)
                return ((String)bookmark).substring(0, iLastColon);
            else
                return ((String)bookmark).substring(iLastColon+1);
        }
        return bookmark;
    }
    /**
     * Get a unique key that can be used to reposition to this record.
     * @exception DBException File exception.
     */
    public Object getHandle(int iHandleType) throws DBException
    {
        if (iHandleType == DBConstants.OBJECT_ID_HANDLE)
            iHandleType = DBConstants.FULL_OBJECT_HANDLE; // Get the source table also
        if (this.getCurrentTable() == null)
            return null;
        Object object = this.getCurrentTable().getHandle(iHandleType);
        if (iHandleType == DBConstants.DATA_SOURCE_HANDLE)
            object = new FullDataSource(this.getCurrentTable(), object);
        return object;
    }
    /**
     * Read the record that matches this record's current key.
     * @exception DBException File exception.
     */
    public boolean seek(String strSeekSign) throws DBException
    {
        boolean bSuccess = false;
        BaseTable table = this.getCurrentTable();
        if (table != null)
        {
            table.getRecord().setKeyArea(this.getRecord().getDefaultOrder());
            bSuccess = table.seek(strSeekSign);
            if (bSuccess)    // Move to first record
                this.setCurrentTable(table);
        }
        this.syncCurrentToBase();
        return bSuccess;
    }
    /**
     * Add this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void add(Rec fieldList) throws DBException
    {
        this.syncCurrentToBase();
        boolean[] rgbListenerState = this.getRecord().setEnableListeners(false);
        this.getRecord().handleRecordChange(DBConstants.ADD_TYPE);   // Fake the call for the grid table
        this.getRecord().setEnableListeners(rgbListenerState);
        
        super.add(fieldList);
        this.syncCurrentToBase();

        rgbListenerState = this.getRecord().setEnableListeners(false);
        this.getRecord().handleRecordChange(DBConstants.AFTER_ADD_TYPE);   // Fake the call for the grid table
        this.getRecord().setEnableListeners(rgbListenerState);
    }
    /**
     * Delete this record (Always called from the record class).
     * Always override this method.
     * @exception DBException File exception.
     */
    public void remove() throws DBException
    {
        this.syncCurrentToBase();
        boolean[] rgbListenerState = this.getRecord().setEnableListeners(false);
        this.getRecord().handleRecordChange(DBConstants.DELETE_TYPE);   // Fake the call for the grid table
        this.getRecord().setEnableListeners(rgbListenerState);
        
        super.remove();
        this.syncCurrentToBase();

        rgbListenerState = this.getRecord().setEnableListeners(false);
        this.getRecord().handleRecordChange(DBConstants.AFTER_DELETE_TYPE);   // Fake the call for the grid table
        this.getRecord().setEnableListeners(rgbListenerState);
    }
    /**
     * Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void set(Rec fieldList) throws DBException
    {
        this.syncCurrentToBase();
        boolean[] rgbListenerState = this.getRecord().setEnableListeners(false);
        this.getRecord().handleRecordChange(DBConstants.UPDATE_TYPE);   // Fake the call for the grid table
        this.getRecord().setEnableListeners(rgbListenerState);
        
        super.set(fieldList);        
        this.syncCurrentToBase();

        rgbListenerState = this.getRecord().setEnableListeners(false);
        this.getRecord().handleRecordChange(DBConstants.AFTER_UPDATE_TYPE);   // Fake the call for the grid table
        this.getRecord().setEnableListeners(rgbListenerState);
    }
    /**
     * Make the record represented by this DataRecord current.
     * @param dataRecord tour.db.DataRecord
     */
    public boolean setDataRecord(DataRecord dataRecord)
    {
        if (dataRecord.getHandle(DBConstants.DATA_SOURCE_HANDLE) == null)
        {   // No data source handle, mean you may need a table specified.
            String strObjectSource = (String)dataRecord.getHandle(DBConstants.OBJECT_SOURCE_HANDLE);
            if (strObjectSource != null)
            {
                int iLastColon = strObjectSource.lastIndexOf(BaseTable.HANDLE_SEPARATOR);
                if ((iLastColon > 0) && (iLastColon < strObjectSource.length() - 1))
                {
                    String strTableName = strObjectSource.substring(iLastColon + 1);
                    Iterator<BaseTable> iterator = this.getTables();
                    while (iterator.hasNext())
                    {
                        BaseTable table = iterator.next();
                        if (strTableName.equals(table.getRecord().getTableNames(false)))
                            this.setCurrentTable(table);
                    }
                }
            }
        }
        else if (dataRecord.getHandle(DBConstants.DATA_SOURCE_HANDLE) instanceof FullDataSource)
        {   // Always
            this.setCurrentTable(((FullDataSource)dataRecord.getHandle(DBConstants.DATA_SOURCE_HANDLE)).getTable());
        }
        boolean bSuccess = super.setDataRecord(dataRecord);
        this.syncCurrentToBase();
        return bSuccess;
    }
    /**
     * A full data source is a special datasource that include the table, so I can get the record back.
     */
    protected class FullDataSource extends Object
    {
        /**
         * The source table.
         */
        BaseTable m_table = null;
        /**
         * The normal data source.
         */
        Object m_dataSource = null;
        /**
         * Constructor.
         * @param table The table source.
         * @param dataSource The data source to set.
         */
        public FullDataSource(BaseTable table, Object dataSource)
        {
            m_table = table;
            m_dataSource = dataSource;
        }
        /**
         * Get this source's table.
         * @return The table.
         */
        public BaseTable getTable()
        {
            return m_table;
        }
        /**
         * Get the datasource.
         * @return The data source.
         */
        public Object getDataSource()
        {
            return m_dataSource;
        }
        /**
         * Set the data source.
         * @param dataSource The data source to set.
         */
        public void setDataSource(Object dataSource)
        {
            m_dataSource = dataSource;
        }
    }

}
