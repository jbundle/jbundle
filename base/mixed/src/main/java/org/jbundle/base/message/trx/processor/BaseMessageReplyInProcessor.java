/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.processor;

import java.util.Map;
import java.util.Properties;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.Environment;
import org.jbundle.model.main.msg.db.MessageLogModel;
import org.jbundle.model.main.msg.db.MessageStatusModel;
import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.MessageConstants;


/**
 * This is the base class to process an external message.
 * This class handles two distinct but related functions:
 * <br>- It converts a message to an external format and sends it, returning a reply.
 * <br>- It converts an incoming external message and processes it, typically sending a reply.
 */
public class BaseMessageReplyInProcessor extends BaseExternalMessageProcessor
{
    /**
     * Default constructor.
     */
    public BaseMessageReplyInProcessor()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public BaseMessageReplyInProcessor(RecordOwnerParent taskParent, Record recordMain, Properties properties)
    {
        this();
        this.init(taskParent, recordMain, properties);    // The one and only
    }
    /**
     * Initializes the MessageProcessor.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free all the resources belonging to this class.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Given the converted message for return, do any further processing before returning the message.
     * @param internalMessage The internal return message just as it was converted from the source.
     */
    public BaseMessage processMessage(BaseMessage message)
    {
        if (message == null)
            return null;
        this.updateLogFiles(message, true);  // Need to change log status to SENTOK (also associate return message log trx ID) (todo)
        String strReturnQueueName = null;
        if (message.getMessageHeader() instanceof TrxMessageHeader)
            strReturnQueueName = (String)((TrxMessageHeader)message.getMessageHeader()).getMessageHeaderMap().get(MessageConstants.RETURN_QUEUE_NAME);
        if (strReturnQueueName == null)
            strReturnQueueName = MessageConstants.TRX_RETURN_QUEUE;
        TrxMessageHeader privateMessageHeader = new TrxMessageHeader(strReturnQueueName, MessageConstants.INTERNET_QUEUE, null);
        if (message.getMessageHeader().getRegistryIDMatch() == null)
        {   // The registry is probably in the message header
            Integer intRegistryID = this.getRegistryID(message);
            if (intRegistryID != null)
                message.getMessageHeader().setRegistryIDMatch(intRegistryID);
        }
        privateMessageHeader.setRegistryIDMatch(message.getMessageHeader().getRegistryIDMatch());
        message.setMessageHeader(privateMessageHeader);

        Environment env = null;
        if ((this.getTask() != null)
            && (this.getTask().getApplication() != null))
                env = ((BaseApplication)this.getTask().getApplication()).getEnvironment();
        if (env == null)
            env = Environment.getEnvironment(null);
        App app = null;
        if (this.getTask() != null)
        	app = this.getTask().getApplication();
        BaseMessageManager msgManager = env.getMessageManager(app, true);
        msgManager.sendMessage(message);
        return null;
    }
    /**
     * Process this internal message.
     * @param messageResponseIn The message to process.
     * @return (optional) The return message if applicable.
     */
    public BaseMessage getOriginalMessage(BaseMessage messageResponseIn)
    {
        BaseMessage messageOrig = null;
        TrxMessageHeader trxMessageHeader = (TrxMessageHeader)messageResponseIn.getMessageHeader();
        String strOrigTrxID = (String)trxMessageHeader.get(TrxMessageHeader.ORIG_LOG_TRX_ID);
        MessageLogModel recMessageLog = this.getMessageLog(strOrigTrxID);
        if ((recMessageLog != null)
            && ((recMessageLog.getEditMode() == DBConstants.EDIT_CURRENT)
                || (recMessageLog.getEditMode() == DBConstants.EDIT_IN_PROGRESS)))
        {
            messageOrig = (BaseMessage)recMessageLog.createMessage(strOrigTrxID);
        }
        return messageOrig;
    }
    /**
     * After the processing has finished, update the MessageLog files (both for the original and this response)
     * @param messageResponseIn
     * @param bSuccess TODO
     */
    public void updateLogFiles(BaseMessage messageResponseIn, boolean bSuccess)
    {
        TrxMessageHeader trxMessageHeader = (TrxMessageHeader)messageResponseIn.getMessageHeader();
        String strOrigTrxID = (String)trxMessageHeader.get(TrxMessageHeader.ORIG_LOG_TRX_ID);
        String strMessageLogID = (String)trxMessageHeader.get(TrxMessageHeader.LOG_TRX_ID);
        Record recMessageLog = (Record)this.getMessageLog(strOrigTrxID);
        if ((recMessageLog != null)
            && ((recMessageLog.getEditMode() == DBConstants.EDIT_CURRENT)
                || (recMessageLog.getEditMode() == DBConstants.EDIT_IN_PROGRESS)))
        {
            try {
                // First update the log of my sent message
                recMessageLog.edit();
                ReferenceField fldReference = (ReferenceField)recMessageLog.getField(MessageLogModel.MESSAGE_STATUS_ID);
                if ((fldReference.isNull()) || (!MessageStatusModel.ERROR.equalsIgnoreCase(fldReference.getReference().getField(MessageStatusModel.CODE).toString())))
                {   // Can't change error status to something else
                    int iMessageStatusID = fldReference.getIDFromCode(MessageStatusModel.SENTOK);
                    if (!bSuccess)
                        iMessageStatusID = fldReference.getIDFromCode(MessageStatusModel.IGNORED);
                    fldReference.setValue(iMessageStatusID);  // Message status
                }
                recMessageLog.getField(MessageLogModel.RESPONSE_MESSAGE_LOG_ID).setString(strMessageLogID);
                recMessageLog.set();
                // Now update the log of this message.
                String strOrigContact = recMessageLog.getField(MessageLogModel.CONTACT_ID).toString();
                String strOrigContactType = recMessageLog.getField(MessageLogModel.CONTACT_ID).toString();
                String strReferenceType = recMessageLog.getField(MessageLogModel.REFERENCE_TYPE).toString();
                String strReferenceID = recMessageLog.getField(MessageLogModel.REFERENCE_ID).toString();
                recMessageLog = (Record)this.getMessageLog(strMessageLogID);
                if ((recMessageLog != null)
                        && ((recMessageLog.getEditMode() == DBConstants.EDIT_CURRENT)
                            || (recMessageLog.getEditMode() == DBConstants.EDIT_IN_PROGRESS)))
                {
                    recMessageLog.edit();
                    recMessageLog.getField(MessageLogModel.RESPONSE_MESSAGE_LOG_ID).setString(strOrigTrxID);
                    if ((strOrigContact != null) && (recMessageLog.getField(MessageLogModel.CONTACT_ID).isNull()))
                        recMessageLog.getField(MessageLogModel.CONTACT_ID).setString(strOrigContact);
                    if ((strOrigContactType != null) && (recMessageLog.getField(MessageLogModel.CONTACT_TYPE_ID).isNull()))
                        recMessageLog.getField(MessageLogModel.CONTACT_TYPE_ID).setString(strOrigContactType);
                    if ((strReferenceType != null) && (recMessageLog.getField(MessageLogModel.REFERENCE_TYPE).isNull()))
                        recMessageLog.getField(MessageLogModel.REFERENCE_TYPE).setString(strReferenceType);
                    if ((strReferenceID != null) && (recMessageLog.getField(MessageLogModel.REFERENCE_ID).isNull()))
                        recMessageLog.getField(MessageLogModel.REFERENCE_ID).setString(strReferenceID);
                    recMessageLog.set();
                }
            } catch (DBException ex) {
                ex.printStackTrace();
            }
        }
    }
}
