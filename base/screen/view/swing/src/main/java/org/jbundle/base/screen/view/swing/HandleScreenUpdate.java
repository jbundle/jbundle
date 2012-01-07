/*
 * UpdateBaseScreenRecord.java
 *
 * Created on October 6, 2005, 3:29 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.record.RecordMessageHeader;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.view.ScreenFieldView;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * Process this RecordMessage.
 */
class HandleScreenUpdate extends HandleBaseScreenUpdate
    implements Runnable
{

    /**
     * Constructor.
     */
    public HandleScreenUpdate()
    {
        super();
    }
    /**
     * Constructor.
     */
    public HandleScreenUpdate(ScreenFieldView screenFieldView, BaseMessage message)
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
     * Handle the update record message.
     * The current record was updated, so update this record to reflect any changes
     * made when it was updated.
     * @param message The message.
     * @param properties The message properties.
     * @param iChangeType The change type.
     */
    public void handleUpdateMessage(BaseMessage message, int iChangeType)
    {
        RecordMessageHeader messageHeader = (RecordMessageHeader)message.getMessageHeader();
        String strTableName = (String)messageHeader.get(RecordMessageHeader.TABLE_NAME);
        if (this.getScreenFieldView().getScreenField() == null)
                return; // Screen was probably freed before I got to it.
        Record record = (Record)this.getScreenFieldView().getScreenField().getMainRecord(); // must be main record (for now)
        if (strTableName != null)
        	if (this.getScreenFieldView().getScreenField() instanceof BaseScreen)
        		if (!strTableName.equals(record.getTableNames(false)))
        			record = ((BaseScreen)this.getScreenFieldView().getScreenField()).getRecord(strTableName);
        if (messageHeader.isRecordMatch(record))    // Double-check to make sure this is the right message
            record.refreshToCurrent(iChangeType, false);
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
        try {
            Record record = (Record)message.get(RecordMessageHeader.RECORD_TO_UPDATE);
            RecordMessageHeader messageHeader = (RecordMessageHeader)message.getMessageHeader();
            int iHandleType = DBConstants.BOOKMARK_HANDLE;
            Object bookmark = messageHeader.getBookmark(iHandleType);
            Boolean boolUpdate = (Boolean)message.get(RecordMessageHeader.UPDATE_ON_SELECT);
            if (boolUpdate == null)
                boolUpdate = Boolean.FALSE;
            if (bookmark != null)
            {       // A lookup window selected a new record.
                if (record != null)
                    if (boolUpdate.booleanValue())
                        if (record.isModified())
                {
                    if (record.getEditMode() == Constants.EDIT_IN_PROGRESS)
                        record.set();
                    if (record.getEditMode() == Constants.EDIT_ADD)
                        record.add();
                }
                super.handleSelectMessage(message);
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
}
