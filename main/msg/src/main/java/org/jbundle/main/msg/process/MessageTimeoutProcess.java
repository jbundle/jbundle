/**
 * @(#)MessageTimeoutProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.process;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.CompareFileFilter;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.message.trx.processor.BaseInternalMessageProcessor;
import org.jbundle.base.message.trx.processor.BaseMessageProcessor;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.main.msg.db.MessageInfoType;
import org.jbundle.main.msg.db.MessageLog;
import org.jbundle.main.msg.db.MessageStatus;
import org.jbundle.main.msg.db.MessageType;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.message.MessageManager;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.thread.PrivateTaskScheduler;
import org.jbundle.thin.base.util.Application;

/**
 *  MessageTimeoutProcess - Process the current message timeouts.
 */
public class MessageTimeoutProcess extends BaseInternalMessageProcessor
{
    public static final String TIMEOUT_QUEUE_NAME = "jobSchedulerQueue";
    /**
     * Default constructor.
     */
    public MessageTimeoutProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageTimeoutProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        return new MessageLog(this);
    }
    /**
     * Add the screen record.
     */
    public Record addScreenRecord()
    {
        return new MessageTimeoutScreenRecord(this);
    }
    /**
     * Add the behaviors.
     */
    public void addListeners()
    {
        super.addListeners();
        
        this.getMainRecord().setKeyArea(MessageLog.kTimeoutKey);
        int iMessageStatus = ((ReferenceField)this.getScreenRecord().getField(MessageTimeoutScreenRecord.kMessageStatusID)).getIDFromCode(MessageStatus.SENT);
        this.getScreenRecord().getField(MessageTimeoutScreenRecord.kMessageStatusID).setValue(iMessageStatus);
        
        this.getMainRecord().addListener(new SubFileFilter(this.getScreenRecord().getField(MessageTimeoutScreenRecord.kMessageStatusID), MessageLog.kMessageStatusID, null, -1, null, -1));
        this.getScreenRecord().getField(MessageTimeoutScreenRecord.kStartTimeout).setToLimit(DBConstants.START_SELECT_KEY);
        this.getMainRecord().addListener(new CompareFileFilter(this.getMainRecord().getField(MessageLog.kTimeoutTime), this.getScreenRecord().getField(MessageTimeoutScreenRecord.kStartTimeout), ">="));
    }
    /**
     * Run Method.
     */
    public void run()
    {
        MessageLog recMessageLog = (MessageLog)this.getMainRecord();
        try {
            Date timeTimeout = null;
            while (recMessageLog.hasNext())
            {
                recMessageLog.next();
                
                timeTimeout = ((DateTimeField)recMessageLog.getField(MessageLog.kTimeoutTime)).getDateTime();
                if (timeTimeout == null)
                    continue;   // Never
                if (timeTimeout.getTime() > System.currentTimeMillis())
                    break;
        
                String strTrxID = recMessageLog.getCounterField().toString();
                recMessageLog.edit();
                int iErrorStatus = ((ReferenceField)recMessageLog.getField(MessageLog.kMessageStatusID)).getIDFromCode(MessageStatus.ERROR);
                recMessageLog.getField(MessageLog.kMessageStatusID).setValue(iErrorStatus);
                recMessageLog.set();
                
                this.processMessageTimeout(strTrxID);
            }
            if (recMessageLog.getEditMode() == DBConstants.EDIT_CURRENT)
                if (timeTimeout != null)    // Always
            {   // More to process, schedule it for later
                MessageManager messageManager = ((Application)getTask().getApplication()).getMessageManager();
                Map<String,Object> properties = new Hashtable<String,Object>();
                properties.put(PrivateTaskScheduler.TIME_TO_RUN, timeTimeout);
                properties.put(PrivateTaskScheduler.NO_DUPLICATE, Constants.TRUE);
                properties.put(DBParams.PROCESS, MessageTimeoutProcess.class.getName());
                if (messageManager != null)
                    messageManager.sendMessage(new MapMessage(new BaseMessageHeader(MessageTimeoutProcess.TIMEOUT_QUEUE_NAME, MessageConstants.INTRANET_QUEUE, this, null), properties));
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * ProcessMessageTimeout Method.
     */
    public void processMessageTimeout(String strTrxID)
    {
        MessageLog recMessageLog = (MessageLog)this.getMainRecord();
        BaseMessage message = (BaseMessage)recMessageLog.createMessage(strTrxID);
        String strMessageError = "Message timeout";
        BaseMessage messageReply = (BaseMessage)BaseMessageProcessor.processErrorMessage(this, message, strMessageError);
        if (messageReply != null)    // No reply if null.
        {
            BaseMessageTransport transport = this.getMessageTransport(message);
            transport.setupReplyMessage(messageReply, message, MessageInfoType.REPLY, MessageType.MESSAGE_IN);
            transport.processIncomingMessage(messageReply, message);
        }
    }

}
