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
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.MessageConstants;


/**
 * Process this RecordMessage.
 */
class HandleBaseScreenUpdate extends Object
    implements Runnable
{
    /**
     * The message I need to process.
     */
    protected BaseMessage m_message = null;
    /**
     *
     */
    protected ScreenFieldView m_screenFieldView = null;

    /**
     * Constructor.
     */
    public HandleBaseScreenUpdate()
    {
        super();
    }
    /**
     * Constructor.
     */
    public HandleBaseScreenUpdate(ScreenFieldView screenFieldView, BaseMessage message)
    {
        this();
        this.init(screenFieldView, message);
    }
    /**
     * Constructor.
     */
    public void init(ScreenFieldView screenFieldView, BaseMessage message)
    {
        m_screenFieldView = screenFieldView;
        m_message = message;
    }
    /**
     *
     */
    public ScreenFieldView getScreenFieldView()
    {
        return m_screenFieldView;
    }
    /**
     * Update the fields on the screen.
     */
    public void run()
    {
        BaseMessage message = m_message;
        if (message.getMessageHeader() instanceof RecordMessageHeader)
        {
            int iChangeType = 0;
            String strChangeType = (String)message.get(MessageConstants.MESSAGE_TYPE_PARAM);
            if (strChangeType != null)
                iChangeType = Integer.parseInt(strChangeType);
            if (iChangeType == DBConstants.SELECT_TYPE)
                this.handleSelectMessage(message);
            else
                this.handleUpdateMessage(message, iChangeType);
        }
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
        int iHandleType = DBConstants.BOOKMARK_HANDLE;
        Object bookmark = ((RecordMessageHeader)message.getMessageHeader()).getBookmark(iHandleType);

        try {
            if (bookmark != null)
            {
                if (recordToUpdate.setHandle(bookmark, iHandleType) != null)
                {
                    recordToUpdate.handleRecordChange(DBConstants.SELECT_TYPE); // Make sure this record also gets the select notification
                    return;
                }
            }
            recordToUpdate.addNew();  // Not found, start with a NEW record!
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
}
