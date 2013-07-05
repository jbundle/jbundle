/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.util;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Iterator;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.StringField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;


/**
 * ResourceTable - A mirror table that retrieves the mirrored fields for the current language.
 */
public class ResourceTable extends MirrorTable
{
    protected BaseBuffer m_buffer = null;

    /**
     * RecordList Constructor.
     */
    public ResourceTable()
    {
        super();
    }
    /**
     * RecordList Constructor.
     */
    public ResourceTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * init.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * @exception DBException File exception.
     */
    public void addNew() throws DBException
    {
        super.addNew();
        m_buffer = null;
    }
    /**
     * Add this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void add(Rec fieldList) throws DBException
    {
        Record record = (Record)fieldList;
        if (((this.getRecord().getOpenMode() & DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY) != DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY)
            || (record.isRefreshedRecord()))
                this.restoreMainRecord(record, true);
        super.add(record);
    }
    /**
     * Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void set(Rec fieldList) throws DBException
    {
        this.restoreMainRecord((Record)fieldList, true);
        super.set(fieldList);
    }
    /**
     * doRecordChange Method.
     * If this is an update or add type, grab the bookmark.
     * @param field The field that is changing.
     * @param iChangeType The type of change.
     * @param bDisplayOption not used here.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        if (iChangeType == DBConstants.UPDATE_TYPE)
        {   // Special case - Even though the record can't be used, GridTable needs a snapshot of the merged record
            Record recMain = this.getNextTable().getRecord();
            Record recAlt = this.getAltRecord();
            this.syncRecords(recAlt, recMain);  // NOTE: Although I put the wrong fields into the main record, modified = false! (so they don't update)
        }
        return iErrorCode;
    }
    /**
     * Copy the fields from the (main) source to the (mirrored) destination record.
     * This is done before any write or set.
     * @param recAlt Destination record
     * @param recMain Source record
     */
    public void copyRecord(Record recAlt, Record recMain)
    {
        // Already done in preCopyRecord(), just make sure the primary key is the same
        this.copyKeys(recAlt, recMain, DBConstants.MAIN_KEY_AREA);
    }
    /**
     * Read the mirrored record.
     * @param tblTarget The table to read the same record from.
     * @param recSource Source record
     * @return The target table's record.
     */
    public Record syncTables(BaseTable tblAlt, Record recMain) throws DBException
    {
        Record recAlt = super.syncTables(tblAlt, recMain);  // Read the mirrored record
        if (recAlt.getEditMode() == Constants.EDIT_CURRENT)
            this.syncRecords(recAlt, recMain);
        return recAlt;
    }
    /**
     * Save the record state and copy the localized fields from the language record to the main record.
     * @param tblTarget The table to read the same record from.
     * @param recSource Source record
     * @return The target table's record.
     */
    public void syncRecords(Record recAlt, Record recMain)
    {
        boolean bFieldsInSync = true;
        // Copy the language-specific fields
        m_buffer = new VectorBuffer(null);
        for (int iIndex = 0; iIndex < recMain.getFieldCount(); iIndex++)
        {
            BaseField fieldAlt = recAlt.getField(iIndex);
            BaseField fieldMain = null;
            if (bFieldsInSync)
                fieldMain = recMain.getField(iIndex);
            if ((fieldMain == null) || (!fieldMain.getFieldName().equals(fieldAlt.getFieldName())))
            {
                fieldMain = recMain.getField(fieldAlt.getFieldName());
                bFieldsInSync = false;
            }
            if (fieldMain != null) if (fieldAlt != null)
                if (this.isLanguageOverride(fieldMain))
            { // Move this field, but don't call doRecordChange (READ_MOVE)
                m_buffer.addNextField(fieldMain);   // Save these to restore later
                if (!fieldAlt.isNull())   // Don't move a null
                {
                    fieldMain.moveFieldToThis((BaseField)fieldAlt, DBConstants.DISPLAY, DBConstants.READ_MOVE);
                    fieldMain.setModified(false);
                }
            }
        }
    }
    /**
     * Restore the unchangeable info from the buffer to this main record.
     * @param record The main record
     * @param altMatchesToNull If the alternate field is the same as the main field, set it to null (no change).
     */
    public void restoreMainRecord(Record record, boolean altMatchesToNull)
    {
        Iterator<BaseTable> iterator = this.getTables();
        while (iterator.hasNext())
        {
            BaseTable table = iterator.next();
            if ((table != null) && (table != this.getNextTable()))
            {
                Record record2 = table.getRecord();
                this.restoreMainRecord(record2, record, altMatchesToNull);
            }
        }
    }
    /**
     * Restore the main record's string fields to their original state from the buffer.
     * @param recAlt The alternate record
     * @param altMatchesToNull If the alternate field is the same as the main field, set it to null (no change).
     * @param record The main record
     */
    public void restoreMainRecord(Record recAlt, Record recMain, boolean altMatchesToNull)
    {
        if (m_buffer != null)
            m_buffer.resetPosition();
        boolean bFieldsInSync = true;
        for (int iIndex = 0; iIndex < recAlt.getFieldCount(); iIndex++)
        {
            BaseField fieldAlt = recAlt.getField(iIndex);
            BaseField fieldMain = null;
            if (bFieldsInSync)
                fieldMain = recMain.getField(iIndex);
            if ((fieldMain == null) || (!fieldMain.getFieldName().equals(fieldAlt.getFieldName())))
            {
                fieldMain = recMain.getField(fieldAlt.getFieldName());
                bFieldsInSync = false;
            }
            fieldAlt.moveFieldToThis((BaseField)fieldMain);   // All values belong in the alt record.
            if (fieldMain != null) if (fieldAlt != null)
                if (this.isLanguageOverride(fieldMain))
            { // Move this field, but don't call doRecordChange (READ_MOVE)
                if (m_buffer != null)
                    m_buffer.getNextField(fieldMain, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);      // Get the original value back
                fieldMain.setModified(false);       // No change from original
            //x    if (fieldMain.isNull())         // If the value was null, set it to new (Why not?)
            //x        fieldMain.moveFieldToThis((BaseField)fieldAlt);
            }
            if (altMatchesToNull)
            	if (fieldMain.equals(fieldAlt))
            	    if ((fieldAlt.isNullable()) && (fieldAlt != recAlt.getCounterField()))
                		fieldAlt.setData(null);
        }
    }
    /**
     * Is this a local-specific field?
     * @param field
     * @return True if it is
     */
    public boolean isLanguageOverride(BaseField field)
    {
        return (field instanceof StringField);  // For now
    }
    /**
     * Get the alternate (language) record.
     * @return The record
     */
    public Record getAltRecord()
    {
        Record recNext = this.getNextTable().getRecord();
        Iterator<BaseTable> tables = this.getTables();
        while (tables.hasNext())
        {
            Record recAlt = tables.next().getRecord();
            if (recAlt != recNext)
                return recAlt;
        }
        return recNext;
    }
}
