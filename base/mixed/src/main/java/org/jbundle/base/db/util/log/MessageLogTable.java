/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.util.log;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.message.app.MessageApplication;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.screen.message.RemoteMessageManager;


/**
 * LogTable - An Override of BaseTable that logs all changes.
 * Note: This requires the BackupServerApp to be running somewhere.
 * The BackupServerApp picks up the messages and writes them to a log/ftp file.
 * The BackupServerApp (and the restore app) is in the tourapp_program project.
 */
public class MessageLogTable extends LogTable
    implements BackupConstants
{
    protected BaseMessage m_message = null;
    protected BaseMessageManager m_messageManager = null;

    /**
     * RecordList Constructor.
     */
    public MessageLogTable()
    {
        super();
    }
    /**
     * RecordList Constructor.
     */
    public MessageLogTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * 
     */
    public void free()
    {
        if (m_message != null)
            m_message.free();
        m_message = null;
        m_messageManager = null;
        super.free();
    }
    /**
     * Log this transaction.
     * @param strLogData Data to log
     */
    public synchronized void logTrx(Object objLogData)
    {
        BaseMessage message = this.getMessageObject();
        message.putNative(MESSAGE_PARAM, objLogData);
        this.getMessageManager().sendMessage(message);
    }
    /**
     * Don't create a new object each time.
     * @return
     */
    public BaseMessage getMessageObject()
    {
        if (m_message == null)
        {
            Map<String,Object> properties = new Hashtable<String,Object>();
            m_message = new MapMessage(new BaseMessageHeader(BACKUP_QUEUE_NAME, BACKUP_QUEUE_TYPE, this, null), properties);
        }
        return m_message;
    }
    /**
     * Don't create a new object each time.
     * @return
     */
    public BaseMessageManager getMessageManager()
    {
        if (m_messageManager == null)
        {
        	MessageApplication messageApplication = null;
        	BaseApplication app = null;
        	if (this.getRecord() != null)
        		if (this.getRecord().getRecordOwner() != null)
        			if (this.getRecord().getRecordOwner().getTask() != null)
        				app = (BaseApplication)this.getRecord().getRecordOwner().getTask().getApplication();
        	if (app != null)
        		messageApplication = app.getEnvironment().getMessageApplication(true, app.getProperties());
        	else
        		messageApplication = this.getDatabase().getDatabaseOwner().getEnvironment().getMessageApplication(true, null);
            m_messageManager = RemoteMessageManager.getMessageManager(messageApplication);
        }
        return m_messageManager;
    }
}
