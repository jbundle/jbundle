/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.shared;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 * NOTE: Once you resolve the bookmark dilemma in BaseTable, fix GetAtRecordClose.
 */
import java.util.Iterator;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.PassThruTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.event.FieldListener;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldList;


/**
 * Access multiple tables as if they were overrides of the base record's table.
 * This is typically used to access multiple tables from the same overriding class,
 * such as a Dog and Cat class from an Animal class.
 */
public class BaseSharedTable extends PassThruTable
{
    /**
     * The table for the current operation (Add/Move/Delete).
     * (ie., If this is a table for the "Animal" record, m_CurrentRecord
     * could be a "Cat" table object).
     */
    protected BaseTable m_tableCurrent = null;
    
    /**
     * Get the current table target.
     * This is usually used to keep track of the current table in Move(s).
     * (ie., If this is a table for the "Animal" record, m_CurrentRecord
     * could be an "Cat" table object).
     */
    public BaseTable getCurrentTable()
    {
        if (m_tableCurrent != null)
            return m_tableCurrent;
        else
            return super.getCurrentTable();   // Current table
    }
    /**
     * Set the current table target.
     * @param table The new current table.
     */
    public void setCurrentTable(BaseTable table)
    {
        m_tableCurrent = table;     // Current table
    }
    /**
     * MultiTable Constructor.
     */
    public BaseSharedTable()
    {
        super();
    }
    /**
     * MultiTable Constructor.
     */
    public BaseSharedTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * init variables.
     */
    public void init(BaseDatabase database, Record record)
    {
        if (database == null) if (record != null)
        {
            BaseTable tableBase = record.getTable();
            if (tableBase != null)
                database = tableBase.getDatabase();   // Same database as table
        }
        m_tableCurrent = null;
        super.init(database, record);
    }
    /**
     * free.
     */
    public void free()
    {
        m_tableCurrent = null;
        super.free();
    }
    /**
     * Add this record's table to the list of tables to pass commands to.
     * @param table The table to add to the list.
     */
    public void addTable(BaseTable table)
    {
        super.addTable(table);
        this.setCurrentTable(table);    // Newest record is always current
    }
    /**
     * Add a listener to the chain.
     * The listener must be cloned and added to all records on the list.
     * @param listener The listener to add.
     */
    public void addListener(Record record, FileListener listener)
    {
        super.addListener(record, listener);
        if (listener.getOwner() == this.getRecord())   // Only replicate listeners added to base.
        {
            Iterator<BaseTable> iterator = this.getTables();
            while (iterator.hasNext())
            {
                BaseTable table = iterator.next();
                if ((table != null) && (table != this.getNextTable()))
                {
                    FileListener newBehavior = null;    // Clone the file behaviors
                    try   {
                        newBehavior = (FileListener)listener.clone(); // Clone the file behaviors
                    } catch (CloneNotSupportedException ex)   {
                        newBehavior = null;
                    }
                    record = table.getRecord();
                    if (newBehavior != null)
                        table.addListener(record, newBehavior);     // Add them to the new query
                }
            }
        }
    }
    /**
     * Add a field listener to the chain.
     * The listener must be cloned and added to all records on the list.
     * @param listener The listener to add.
     * @param The field to add to.
     */
    public void addListener(FieldListener listener, BaseField field)
    {
        super.addListener(listener, field);
        if (listener.getOwner().getRecord() == this.getRecord())   // Only replicate listeners added to base.
        {
            Iterator<BaseTable> iterator = this.getTables();
            while (iterator.hasNext())
            {
                BaseTable table = iterator.next();
                if ((table != null) && (table != this.getNextTable()))
                {
                    BaseField fldInTable = table.getRecord().getField(field.getFieldName());
                    if (fldInTable != null)
                    {
                        FieldListener newBehavior = null; // Clone the file behaviors
                        try   {
                            newBehavior = (FieldListener)listener.clone(fldInTable);  // Clone the file behaviors
                        } catch (CloneNotSupportedException ex)   {
                            newBehavior = null;
                        }
                        if (newBehavior != null)
                            if (newBehavior.getOwner() == null)     // This should have been set, just being careful (ie., next line never called)
                                fldInTable.addListener(newBehavior);        // Add them to the new query
                    }
                }
            }
        }
    }
    /**
     * Move the position of the record.
     * @exception DBException File exception.
     */
    public FieldList moveThruMultipleTables(int iRelPosition) throws DBException
    { // ****ONLY TESTED FOR iRelPosition = +1****
        boolean bIsOpen = m_bIsOpen;
        if (!m_bIsOpen)
            this.open();
        BaseTable table = null;
        BaseTable tableNext = null;
        if (((m_iRecordStatus & DBConstants.RECORD_NEXT_PENDING) == DBConstants.RECORD_NEXT_PENDING)
            && (iRelPosition == DBConstants.NEXT_RECORD))
        {
            m_iRecordStatus = DBConstants.RECORD_NORMAL;
            tableNext = this.getCurrentTable();   // Special case - last call was hasNext()
        }
        else if (((m_iRecordStatus & DBConstants.RECORD_PREVIOUS_PENDING) == DBConstants.RECORD_PREVIOUS_PENDING)
            && (iRelPosition == DBConstants.PREVIOUS_RECORD))
        {
            m_iRecordStatus = DBConstants.RECORD_NORMAL;
            tableNext = this.getCurrentTable();   // Special case - last call was hasNext()
        }
        else
        { // First, make sure all the tables are opened and positioned on valid records.
            if ((!bIsOpen) || (iRelPosition == DBConstants.FIRST_RECORD))   // Need to use a different variable
            {   // If this is the first time thru, open and move to the first record
                Iterator<BaseTable> iterator = this.getTables();
                while (iterator.hasNext())
                {
                    table = iterator.next();
                    table.close();
                    table.getRecord().setKeyArea(this.getRecord().getDefaultOrder());
                    if (table.hasNext())
                    {
                        if (table != this.getCurrentTable())
                            table.move(+1);   // Move to first record
                        else
                            iRelPosition = +1;
                    }
                }
                m_bIsOpen = true; // Yes this file is now open.
            }
            if (!this.getCurrentTable().isEOF())
                this.getCurrentTable().move(iRelPosition);  // First, move the current table in the correct direction.
        // Now, compare the records to find the lowest key
            Iterator<BaseTable> iterator = this.getTables();
            while (iterator.hasNext())
            {
                table = iterator.next();
                if (!table.isEOF())
                {
                    if (tableNext == null)
                        tableNext = table;      // None here yet, this is the lowest
                    else
                    { // Now compare and get the lowest
                        int iCompareValue = 0;
                        int iKeyFields = table.getRecord().getKeyArea().getKeyFields() + DBConstants.MAIN_KEY_FIELD;
                        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFields; iKeyFieldSeq++)
                        {
                            BaseField fldCompare = table.getRecord().getKeyArea().getKeyField(iKeyFieldSeq).getField(DBConstants.FILE_KEY_AREA);
                            BaseField fldNext = tableNext.getRecord().getKeyArea().getKeyField(iKeyFieldSeq).getField(DBConstants.FILE_KEY_AREA);
                            iCompareValue = fldCompare.compareTo(fldNext);
                            if (this.getRecord().getKeyArea() != null)
                                if (this.getRecord().getKeyArea().getKeyField(iKeyFieldSeq) != null)
                                    if (this.getRecord().getKeyArea().getKeyOrder(iKeyFieldSeq) == DBConstants.DESCENDING)
                                iCompareValue = -iCompareValue;
                            if (iCompareValue != 0)
                                break;
                        }
                        if (iCompareValue < 0)
                            tableNext = table;      // This is the lowest so far.
                    }
                }
            }
        }
        if (tableNext == null)
        {
            Iterator<BaseTable> iterator = this.getTables();
            while (iterator.hasNext())
            {   // If this table is suppose to be PAST EOF, make sure all the records are too.
                table = iterator.next();
                if (iRelPosition == DBConstants.NEXT_RECORD)
                    if (table.isEOF())
                        table.move(+1);
            }
            return null;
        }
        this.setCurrentTable(tableNext);    // Never allow currentTable to be null.
        Record record = tableNext.getRecord();
        for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq < record.getFieldCount(); ++iFieldSeq)
        {
//?         if (bDisplayOption)
            record.getField(iFieldSeq).displayField();      // Display this field
        }
        return record;
    }
}
