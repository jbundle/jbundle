/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing.grid;

/**
 * @(#)TableModelHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldTable;


/**
 * On requery, notify the table model that it is looking at a new table.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class TableModelHandler extends FileListener
{
    /**
     * The table model.
     */
    protected GridTableModel m_tableModel = null;

    /**
    * Constructor.
    */
    public TableModelHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param tableModel The table model.
     */
    public TableModelHandler(GridTableModel tableModel)
    {
        this();
        this.init(null, tableModel);
    }
    /**
     * Constructor.
     * @param tableModel The table model.
     */
    public void init(Record record, GridTableModel tableModel)
    {
        super.init(record);
        m_tableModel = tableModel;
    }
    /**
     * Synchronize records after an update.
     * @param field The field that changed.
     * @param iChangeType The type of change (looking for AFTER_REQUERY).
     * @param bDisplayOption Display?
     * @return The error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);      // Initialize the record
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        if (iChangeType == DBConstants.AFTER_REQUERY_TYPE)
        {
            m_tableModel.resetTheModel();
        }
        if (iChangeType == DBConstants.AFTER_ADD_TYPE)
        {
            if (m_tableModel.isAppending())
            {   // Need to tell table model to update to reflect that I have an ID
                int iRowCount = m_tableModel.getRowCount(false);
                int iSelectedRow = m_tableModel.getSelectedRow();
                int iCurrentRow = m_tableModel.getCurrentRow();
                int iLockedRow = m_tableModel.getLockedRow();
                FieldTable table = m_tableModel.getFieldTable();
                if (table instanceof GridTable) // Always
                    if (iCurrentRow == iSelectedRow)    // Hmmm, It is unusual to add the currently locked row                    
                        if (iCurrentRow == iLockedRow)  // A outside factor must have added (refreshed!) the record
                            if (iCurrentRow == iRowCount - 1)   // So I better notify the table model.
                {
                    m_tableModel.bumpTableSize(0, false);   // Sync table size to grid size
                    m_tableModel.setCurrentRow(-1);         // Make sure grid gets new data
                    m_tableModel.makeRowCurrent(iCurrentRow, true); // Refresh data to grid
                }
            }
        }
        return iErrorCode;
    }
}
