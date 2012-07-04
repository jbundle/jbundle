/**
 * @(#)NotifyTimeoutProcessHandler.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.db;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.main.msg.process.MessageTimeoutProcess;
import org.jbundle.model.message.MessageManager;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.thread.PrivateTaskScheduler;
import org.jbundle.thin.base.util.Application;

/**
 *  NotifyTimeoutProcessHandler - Notify the message timeout process to watch for a timeout on this message..
 */
public class NotifyTimeoutProcessHandler extends FileListener
{
    private static Date m_lastTime = null;
    public static final int EXTRA_TIME_MS = 1 * 1000;
    /**
     * Default constructor.
     */
    public NotifyTimeoutProcessHandler()
    {
        super();
    }
    /**
     * NotifyTimeoutProcessHandler Method.
     */
    public NotifyTimeoutProcessHandler(Record record)
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
        if ((iChangeType == DBConstants.AFTER_ADD_TYPE) || (iChangeType == DBConstants.AFTER_UPDATE_TYPE))
            if (!this.getOwner().getField(MessageLog.TIMEOUT_TIME).isNull())
        {
            Date timeTimeout = ((DateTimeField)this.getOwner().getField(MessageLog.TIMEOUT_TIME)).getDateTime();
            Date timeNow = new Date();
            if (timeTimeout != null)
            {
                if ((m_lastTime == null)
                    || (m_lastTime.getTime() <= timeNow.getTime() + EXTRA_TIME_MS))
                {   // All the waiting tasks have been run, ping the process to start up again.
                    MessageManager messageManager = ((Application)this.getOwner().getTask().getApplication()).getMessageManager();
                    Map<String,Object> properties = new Hashtable<String,Object>();
                    properties.put(PrivateTaskScheduler.TIME_TO_RUN, timeTimeout);
                    properties.put(PrivateTaskScheduler.NO_DUPLICATE, Constants.TRUE);
                    properties.put(DBParams.PROCESS, MessageTimeoutProcess.class.getName());
                    if (messageManager != null)
                        messageManager.sendMessage(new MapMessage(new BaseMessageHeader(MessageTimeoutProcess.TIMEOUT_QUEUE_NAME, MessageConstants.INTRANET_QUEUE, this, null), properties));
                }
            }
            m_lastTime = timeTimeout;
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }

}
