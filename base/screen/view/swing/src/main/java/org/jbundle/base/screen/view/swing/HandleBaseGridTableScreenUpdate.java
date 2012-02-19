/*
 * UpdateBaseScreenRecord.java
 *
 * Created on October 6, 2005, 3:29 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing;

import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.message.record.RecordMessageHeader;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.base.screen.model.BaseGridTableScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.view.swing.grid.GridTableModel;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * Process this RecordMessage.
 */
class HandleBaseGridTableScreenUpdate extends HandleBaseScreenUpdate
    implements Runnable
{

    /**
     * Constructor.
     */
    public HandleBaseGridTableScreenUpdate()
    {
        super();
    }
    /**
     * Constructor.
     */
    public HandleBaseGridTableScreenUpdate(ScreenFieldView screenFieldView, BaseMessage message)
    {
        this();
        this.init(screenFieldView, message);
    }
    /**
     * Constructor.
     */
    public void init(ScreenFieldView screenFieldView, BaseMessage message)
    {
        super.init(screenFieldView, message);
    }
    /**
     *
     */
    public GridTableModel getGridTableModel()
    {
        return ((VBaseGridTableScreen)this.getScreenFieldView()).getModel();
    }
    /**
     * Handle the update record message.
     * The current record was updated, so update this record to reflect any changes
     * made when it was updated.
     * @param message The message.
     * @param properties The message properties.
     * @param iChangeType The change type.
     */
    public void handleUpdateMessage(BaseMessage message, int iChangeType)
    {
        if ((this.getScreenFieldView().getScreenField() == null)
            || (((ComponentParent)this.getScreenFieldView().getScreenField()).getMainRecord() == null))
                return; // Screen was probably freed before I got to it.
        Record record = (Record)((ComponentParent)this.getScreenFieldView().getScreenField()).getMainRecord(); // Record changed
        int iHandleType = DBConstants.BOOKMARK_HANDLE;  // OBJECT_ID_HANDLE;
        Object bookmark = ((RecordMessageHeader)m_message.getMessageHeader()).getBookmark(iHandleType);
        int iRecordMessageType = ((RecordMessageHeader)m_message.getMessageHeader()).getRecordMessageType();
        // See if this record is currently displayed or buffered, if so, refresh and display.
        GridTable table = (GridTable)record.getTable();
        // First, update the cache in the gridtable to match this new (changed) record.
        boolean bAddIfNotFound = true;
        if (this.getGridTableModel().getLockedRow() == this.getGridTableModel().getRowCount(false) - 1)
            if (this.getGridTableModel().getLockedRow() == this.getGridTableModel().getCurrentRow())
                if (this.getGridTableModel().isRecordChanged())
        {   // Special case - I'm currently in the process of adding a new record
            FieldList recordCurrent = this.getGridTableModel().makeRowCurrent(this.getGridTableModel().getLockedRow(), true);
            if (recordCurrent.getEditMode() == DBConstants.EDIT_ADD)
                bAddIfNotFound = false;
        }
        int iIndex = table.updateGridToMessage(m_message, false, bAddIfNotFound);   // Don't re-read, just invalidate the cache so my next read will get the new value.
        if (iRecordMessageType == -1)
        {
            // Don't do anything to the model.
        }
        else if (iRecordMessageType == DBConstants.SELECT_TYPE)
        {   // A secondary record was selected, update the secondary field to the new value.
            // Never
        }
        else if (iRecordMessageType == DBConstants.AFTER_ADD_TYPE)
        {
            if (iIndex == -1)
                if (bAddIfNotFound == false)
            {   // Special case - I'm currently in the process of adding a new record
                try {
                    int iLockedRow = this.getGridTableModel().getLockedRow();
                    this.getGridTableModel().updateIfNewRow(-1);  // Write the current record before continuing
                    this.getGridTableModel().makeRowCurrent(iLockedRow, true);
                    iIndex = table.updateGridToMessage(m_message, false, true);   // This adds this new record to the end
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
            if (iIndex != -1)
            {
                if (iIndex == this.getGridTableModel().getRowCount(false))   // Since index is zero based
                    this.getGridTableModel().bumpTableSize(+1, true);   // Add a line - Update the model, and the screen.
            }
        }
        else if (iRecordMessageType == DBConstants.AFTER_DELETE_TYPE)
        {
            if ((record.getEditMode() == DBConstants.EDIT_CURRENT) || (record.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
            {
                try {
                    Object recordBookmark = record.getHandle(iHandleType);
                    if (bookmark.equals(recordBookmark))
                        record.addNew();    // Since this record was deleted!
                } catch (DBException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else
        {
            // The bookmark has already been updated
        }
        if (iIndex != -1)
        { // This screen has changed, modify it in the table and on the screen
            boolean bRefreshChangedData = false;
            if (this.getGridTableModel().getLockedRow() == iIndex)
            {
                record = (Record)this.getGridTableModel().makeRowCurrent(iIndex, true);  // Start with the current record
                if ((record != null)
                    && ((record.getEditMode() == DBConstants.EDIT_CURRENT) || (record.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    && (iChangeType != DBConstants.AFTER_DELETE_TYPE))
                {   // Always (why else would it be locked?)
                    record.refreshToCurrent(iChangeType, false);   // Merge the changes
                    this.getGridTableModel().cacheCurrentLockedData(iIndex, record);
                }
                else
                {
                    bRefreshChangedData = true;
                }
            }
            else if (this.getGridTableModel().getCurrentRow() == iIndex)
                bRefreshChangedData = true;
            if (bRefreshChangedData)
            {
                this.getGridTableModel().setCurrentRow(-1);    // Make sure grid gets new data
                try {
                    record.addNew();    // Make sure I get the new copy on refresh
                } catch (DBException ex)    {
                }   // Never
                this.getGridTableModel().makeRowCurrent(iIndex, false);  // re-read the current record
            }
            ((VBaseGridTableScreen)this.getScreenFieldView()).fireTableRowsUpdated(iIndex, iIndex);
        }
    }
    /**
     * Handle the select record message.
     * A lookup window selected a new record, so update the current record and display the
     * new selected record.
     * @param message The message.
     * @param properties The message properties.
     */
    public void handleSelectMessage(BaseMessage message)
    {
        Record recordToUpdate = (Record)message.get(RecordMessageHeader.RECORD_TO_UPDATE);
        Boolean boolUpdate = (Boolean)message.get(RecordMessageHeader.UPDATE_ON_SELECT);
        int iIndex = this.getRecordColumn(recordToUpdate);
        int iSelection = ((VBaseGridTableScreen)this.getScreenFieldView()).getSelectedRow();   // Row to change
        int iHandleType = DBConstants.BOOKMARK_HANDLE;  // OBJECT_ID_HANDLE;
        Object bookmark = ((RecordMessageHeader)message.getMessageHeader()).getBookmark(iHandleType);

        boolean bSuccess = false;
        if (iIndex != -1)
        {
            if (iSelection != -1)
            {
                if (boolUpdate == null)
                    boolUpdate = Boolean.FALSE;
                if (boolUpdate.booleanValue())
                {
                    // Good, I found the field that I need to update.
                    // 1. I know this is the selected row, so I don't have to worry about seting the bookmark.
                    // 2. Update the bookmark at this position
                    this.getGridTableModel().setValueAt(bookmark, iSelection, iIndex);
                    // 3. Refresh the grid to display to new value
                    ((VBaseGridTableScreen)this.getScreenFieldView()).tableChanged(iSelection);    // Refresh
//                      ((JTable)getControl()).tableChanged(new TableModelEvent(m_gridTableModel, iSelection));    // Refresh
                    bSuccess = true;
                }
            }
        }
        if (!bSuccess)
            if (recordToUpdate != null)
        {   // This record is not contained in the grid, it is probably in the header screen... read it.
            super.handleSelectMessage(message);
        }
    }
    /**
     * Here is the record, find the column of the field which references this record.
     * @param The record to find.
     * @return The column of the referencing field (or -1 if none).
     */
    public int getRecordColumn(Record recordToUpdate)
    {
        for (int iIndex = 0; iIndex < ((BaseGridTableScreen)this.getScreenFieldView().getScreenField()).getSFieldCount(); iIndex++)
        {
            ScreenField sField = ((BaseGridTableScreen)this.getScreenFieldView().getScreenField()).getSField(iIndex);
            if (sField instanceof ToolScreen)
                continue;
            Convert converter = sField.getConverter();
            if (converter == null)
                continue;
            BaseField field = (BaseField)converter.getField();
            if (field instanceof ReferenceField)
                if (recordToUpdate != null)
                    if (((ReferenceField)field).getReferenceRecord() == recordToUpdate)
            {   // Good, I found the field that I need to update.
                return iIndex;
            }
        }
        return -1;  // Not found
    }
}
