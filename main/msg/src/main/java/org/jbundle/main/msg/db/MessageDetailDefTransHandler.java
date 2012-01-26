/**
 * @(#)MessageDetailDefTransHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.db;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;

/**
 *  MessageDetailDefTransHandler - This listener updates the default transport handler for a message detail.
 */
public class MessageDetailDefTransHandler extends FileListener
{
    protected int m_iOriginalDefaultMessageTransportID = 0;
    protected int m_iOriginalMessageTransportID = 0;
    /**
     * Default constructor.
     */
    public MessageDetailDefTransHandler()
    {
        super();
    }
    /**
     * MessageDetailDefTransHandler Method.
     */
    public MessageDetailDefTransHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record)
    {
        super.init(record);
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        m_iOriginalMessageTransportID = (int)this.getOwner().getField(MessageDetail.kMessageTransportID).getValue();
        m_iOriginalDefaultMessageTransportID = (int)this.getOwner().getField(MessageDetail.kDefaultMessageTransportID).getValue();
        super.doValidRecord(bDisplayOption);
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        m_iOriginalMessageTransportID = 0;
        m_iOriginalDefaultMessageTransportID = 0;
        super.doNewRecord(bDisplayOption);
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * ADD_TYPE - Before a write.
     * UPDATE_TYPE - Before an update.
     * DELETE_TYPE - Before a delete.
     * AFTER_UPDATE_TYPE - After a write or update.
     * LOCK_TYPE - Before a lock.
     * SELECT_TYPE - After a select.
     * DESELECT_TYPE - After a deselect.
     * MOVE_NEXT_TYPE - After a move.
     * AFTER_REQUERY_TYPE - Record opened.
     * SELECT_EOF_TYPE - EOF Hit.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {
        if ((iChangeType == DBConstants.AFTER_ADD_TYPE) || (iChangeType == DBConstants.AFTER_UPDATE_TYPE) || (iChangeType == DBConstants.AFTER_DELETE_TYPE))
        {
            BaseField fldMessageDetail = this.getOwner().getField(MessageDetail.kMessageTransportID);
            BaseField fldDefaultMessageDetail = this.getOwner().getField(MessageDetail.kDefaultMessageTransportID);
            Integer newDefaultMessageTransport = -1;    // None (to start)
            boolean bDefaultTransportFlag = fldMessageDetail.equals(fldDefaultMessageDetail);
        
            if (iChangeType == DBConstants.AFTER_ADD_TYPE)
            {
                if (bDefaultTransportFlag)
                    newDefaultMessageTransport = (Integer)fldDefaultMessageDetail.getData();    // Change all the others
                else
                {
                    newDefaultMessageTransport = -2;
                }
            }
            if (iChangeType == DBConstants.AFTER_DELETE_TYPE)
            {
                if (m_iOriginalMessageTransportID == m_iOriginalDefaultMessageTransportID)
                    newDefaultMessageTransport = null;  // It was default before delete, so clear all!
            }
            else if (iChangeType == DBConstants.AFTER_UPDATE_TYPE)
            {
                    if (fldMessageDetail.isModified() | fldDefaultMessageDetail.isModified())
                    {
                        if (bDefaultTransportFlag)
                            newDefaultMessageTransport = (Integer)fldDefaultMessageDetail.getData();    // Change all the others
                        else if (m_iOriginalMessageTransportID == m_iOriginalDefaultMessageTransportID)
                            newDefaultMessageTransport = null;  // If it was default before update, clear all!
                    }
            }
            if ((newDefaultMessageTransport == null) || (newDefaultMessageTransport != -1))
            {
                MessageDetail recMessageDetail = new MessageDetail(this.getOwner().findRecordOwner());
                try {
                    recMessageDetail.setKeyArea(MessageDetail.kContactTypeIDKey);
                    recMessageDetail.addListener(new StringSubFileFilter(this.getOwner().getField(MessageDetail.kContactTypeID).toString(), MessageDetail.kContactTypeID, this.getOwner().getField(MessageDetail.kPersonID).toString(), MessageDetail.kPersonID, this.getOwner().getField(MessageDetail.kMessageProcessInfoID).toString(), MessageDetail.kMessageProcessInfoID));
                    if (newDefaultMessageTransport != null) if (newDefaultMessageTransport == -2)
                    {
                        newDefaultMessageTransport = null;
                        recMessageDetail.close();
                        while (recMessageDetail.hasNext())
                        {
                            recMessageDetail.next();
                            if (!recMessageDetail.getField(MessageDetail.kDefaultMessageTransportID).isNull())
                            {
                                newDefaultMessageTransport = (int)recMessageDetail.getField(MessageDetail.kDefaultMessageTransportID).getValue();
                                break;  // This is the current default (to set in the new record)
                            }
                        }
                    }
                    recMessageDetail.close();
                    while (recMessageDetail.hasNext())
                    {
                        recMessageDetail.next();
                        recMessageDetail.edit();
                        recMessageDetail.getField(MessageDetail.kDefaultMessageTransportID).setData(newDefaultMessageTransport, bDisplayOption, DBConstants.INIT_MOVE);
                        recMessageDetail.set();
                    }
                } catch (DBException ex) {
                    recMessageDetail.free();
                }
            }
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }

}
