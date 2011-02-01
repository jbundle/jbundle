package org.jbundle.base.db.event;

/**
 * @(#)CloseOnFreeHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.trx.server.TrxMessageListener;
import org.jbundle.base.message.trx.transport.direct.DirectMessageTransport;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.main.msg.db.MessageTransport;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.thread.SwingSyncPageWorker;
import org.jbundle.thin.base.thread.SyncPage;
import org.jbundle.thin.base.util.Application;


/**
 * Send this message after this record updates.
 * NOTE: Since this is a one-time shot, this listener is REMOVED after it sends it payload!
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SendMessageAfterUpdateHandler extends FileListener
{
    protected BaseMessage m_message = null;

    /**
     * Constructor.
     */
    public SendMessageAfterUpdateHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param freeable A object to free when this record is freed.
     */
    public SendMessageAfterUpdateHandler(BaseMessage message)
    {
        this();
        this.init(null, message);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param freeable A object to free when this record is freed.
     * @param recDependent The record to free when this record is freed.
     * @param bCloseOnFree If true, the record is freed.
     */
    public void init(Record record, BaseMessage message)
    {
        super.init(record);
        m_message = message;
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
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
        if (iChangeType == DBConstants.AFTER_UPDATE_TYPE)
        {
            BaseMessageManager messageManager = ((Application)this.getOwner().getTask().getApplication()).getMessageManager();
            if (messageManager != null)
            {
                BaseMessageHeader messageHeader = m_message.getMessageHeader();
                String strQueueType = messageHeader.getQueueType();
                String strQueueName = messageHeader.getQueueName();
                if (MessageConstants.LOCAL_QUEUE.equalsIgnoreCase(strQueueType))
                {
                    BaseMessageReceiver messageReceiver = messageManager.getMessageQueue(strQueueName, strQueueType).getMessageReceiver();
                    if (!messageReceiver.getFilterList(messageHeader).hasNext())
                    {   // First time, make sure this receiver can handle my messages\
                        BaseMessageFilter messageFilter = new BaseMessageFilter(strQueueName, strQueueType, null, null);
                        String strProcessClass = null;
                        Map<String,Object> properties = null;
                        // Set up the trx message hander in my local message listener
                        BaseApplication application = (BaseApplication)this.getOwner().getTask().getApplication();
                        // Note: By adding this to the message app, I don't have to worry about a message being added to a freed app.
                        application = (BaseApplication)application.getEnvironment().getMessageApplication(false, application.getProperties());
                        new TrxMessageListener(messageFilter, application, strProcessClass, properties);   // This listener was added to the filter
                        messageReceiver.addMessageFilter(messageFilter);
                    }
                }
                if (MessageTransport.DIRECT.equalsIgnoreCase((String)messageHeader.get(MessageTransport.SEND_MESSAGE_BY_PARAM)))
                {
                    if (this.getOwner().getTask() instanceof SyncPage)
                    {   // Since this may be time-consuming, display the hour glass (and lock the window, since I'm using your task).
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put("message", m_message);
                        SwingSyncPageWorker worker = new SwingSyncPageWorker(((SyncPage)this.getOwner().getTask()), map, true)
                        {
                            public void done()
                            {
                                DirectMessageTransport transport = new DirectMessageTransport((Task)m_syncPage);
                                BaseMessage message = (BaseMessage)this.get("message");
                                transport.sendMessage(message, null);                    
                                transport.free();
                            }
                        };
                        worker.start();
                    }
                    else
                    {
                        DirectMessageTransport transport = new DirectMessageTransport(this.getOwner().getTask());
                        transport.sendMessage(m_message, null);                    
                        transport.free();
                    }
                }
                else
                    messageManager.sendMessage(m_message);
            }
            m_message = null;
            
            this.getOwner().removeListener(this, true);    // ONE TIME SHOT
        }
        return iErrorCode;
    }
}
