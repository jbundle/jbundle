/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.processor;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.message.core.trx.TrxMessageHeader;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.Utility;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.main.db.base.ContactTypeModel;
import org.jbundle.model.main.msg.db.MessageInfoModel;
import org.jbundle.model.main.msg.db.MessageInfoTypeModel;
import org.jbundle.model.main.msg.db.MessageLogModel;
import org.jbundle.model.main.msg.db.MessageProcessInfoModel;
import org.jbundle.model.main.msg.db.MessageTypeModel;
import org.jbundle.model.main.msg.db.ProcessTypeModel;
import org.jbundle.model.main.msg.db.RequestTypeModel;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.main.msg.db.MessageProcessInfo;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * This is the base class to process an external message.
 * This class handles two distinct but related functions:
 * <br>- It converts a message to an external format and sends it, returning a reply.
 * <br>- It converts an incoming external message and processes it, typically sending a reply.
 */
public abstract class BaseMessageProcessor extends BaseProcess
{
    /**
     * The (optional) target transaction message.
     */
    protected BaseMessage m_trxMessage = null;

    /**
     * Default constructor.
     */
    public BaseMessageProcessor()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public BaseMessageProcessor(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);    // The one and only
    }
    /**
     * Initializes the MessageProcessor.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        m_trxMessage = null;
        if (properties != null)   // NOTE: I remove message from the properties
            m_trxMessage = (BaseMessage)properties.remove(DBParams.MESSAGE);
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free all the resources belonging to this class.
     */
    public void free()
    {
        super.free();
        m_trxMessage = null;
    }
    /**
     * Set the (optional) message this processor will be working on.
     */
    public BaseMessage getTargetMessage()
    {
        if (m_trxMessage == null)
            if (this.getProperties() != null)   // NOTE: I remove message from the properties
                m_trxMessage = (BaseMessage)this.getProperties().remove(DBParams.MESSAGE);
        return m_trxMessage;
    }
    /**
     * Set the (optional) message this processor will be working on.
     */
    public void setTargetMessage(BaseMessage trxMessage)
    {
        m_trxMessage = trxMessage;
    }
    /**
     * Given the converted message for return, do any further processing before returing the message.
     * @param internalMessage The internal return message just as it was converted from the source.
     * @return A reply message if applicable.
     */
    public BaseMessage processMessage(BaseMessage message)
    {
        return null;
    }
    /**
     * Given the message (code) name, get the message processor.
     * @param taskParent Task to pass to new process.
     * @param recordMain Record to pass to the new process.
     * @param properties Properties to pass to the new process.
     * @param strMessageName The name of the message to get the processor from (the message code or ID in the MessageInfo file).
     * @param mapMessageInfo Return the message properties in this map.
     * @return The new MessageProcessor.
     */
    public static BaseMessageProcessor getMessageProcessor(Task taskParent, BaseMessage externalTrxMessage, String strDefaultProcessorClass)
    {
        BaseMessageProcessor messageProcessor = null;
        String strClass = null;
        if (externalTrxMessage != null)
            strClass = (String)((TrxMessageHeader)externalTrxMessage.getMessageHeader()).get(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS);
        if ((strClass == null) || (strClass.length() == 0))
            strClass = strDefaultProcessorClass;
        String strPackage = null;
        if (externalTrxMessage != null)
            strPackage = (String)((TrxMessageHeader)externalTrxMessage.getMessageHeader()).get(TrxMessageHeader.BASE_PACKAGE);
        strClass = ClassServiceUtility.getFullClassName(strPackage, strClass);
        messageProcessor = (BaseMessageProcessor)ClassServiceUtility.getClassService().makeObjectFromClassName(strClass);
        if (messageProcessor != null)
        	messageProcessor.init(taskParent, null, null);
        else
            Utility.getLogger().warning("Message processor not found: " + strClass);
        return messageProcessor;
    }
    /**
     * Get the message queue registry ID from this message.
     * @param internalMessage The message to get the registry ID from.
     * @return Integer The registry ID.
     */
    public Integer getRegistryID(BaseMessage internalMessageReply)
    {
        Integer intRegistryID = null;
        Object objRegistryID = null;
        if (internalMessageReply.getMessageHeader() instanceof TrxMessageHeader)    // Always
            objRegistryID = ((TrxMessageHeader)internalMessageReply.getMessageHeader()).getMessageHeaderMap().get(TrxMessageHeader.REGISTRY_ID);
        if (objRegistryID == null)
            objRegistryID = internalMessageReply.get(TrxMessageHeader.REGISTRY_ID);
        if (objRegistryID instanceof Integer)
            intRegistryID = (Integer)objRegistryID;
        else if (objRegistryID instanceof String)
        {
            try {
                intRegistryID = new Integer(Integer.parseInt((String)objRegistryID));
            } catch (NumberFormatException ex)  {
                intRegistryID = null;
            }
        }
        return intRegistryID;
    }
    /**
     * Utility method to get the message log.
     * Useful for pulling a the transation history of a (original) transaction.
     * @param objTrxID An optional transaction (objectID) ID.
     * @return The Message Log.
     */
    public MessageLogModel getMessageLog(Object objTrxID)
    {
        MessageLogModel recMessageLog = (MessageLogModel)this.getRecord(MessageLogModel.MESSAGE_LOG_FILE);
        if (recMessageLog == null)
            recMessageLog = (MessageLogModel)Record.makeRecordFromClassName(MessageLogModel.THICK_CLASS, this);
        if (recMessageLog != null)
            if (objTrxID != null)
        {
            try {
                ((Record)recMessageLog).setHandle(objTrxID, DBConstants.BOOKMARK_HANDLE);
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        return recMessageLog;
    }
    /**
     * Process this error message.
     * Using this message, lookup the correct error processor and run it, using this error text.
     * @param messageOut
     * @param strMessageError
     * @return Reply Error Message (internal format).
     */
    public static BaseMessage processErrorMessage(RecordOwner recordOwner, BaseMessage messageOut, String strMessageError)
    {
        if (strMessageError != null)  // Add the error message to the message so the error processor has the right desc.
            ((TrxMessageHeader)messageOut.getMessageHeader()).put(TrxMessageHeader.MESSAGE_ERROR, strMessageError); // Error Description
        // Get the error message processor for this message/message type
        String strDefaultProcessorClass = null;
        MessageProcessInfoModel recMessageProcessInfo = (MessageProcessInfoModel)recordOwner.getRecord(MessageProcessInfoModel.MESSAGE_PROCESS_INFO_FILE);
        if (recMessageProcessInfo == null)
            recMessageProcessInfo = (MessageProcessInfoModel)Record.makeRecordFromClassName(MessageProcessInfo.THICK_CLASS, recordOwner);
        Object objResponseID = messageOut.getMessageHeader().get(TrxMessageHeader.MESSAGE_ERROR_PROCESSOR);

        if (objResponseID != null)
        {
            if (Utility.isNumeric(objResponseID.toString()))
            {
                recMessageProcessInfo = recMessageProcessInfo.getMessageProcessInfo(objResponseID.toString());
                if (recMessageProcessInfo != null)
                    strDefaultProcessorClass = recMessageProcessInfo.getField(MessageProcessInfo.PROCESSOR_CLASS).toString();
            }
            else
                strDefaultProcessorClass = objResponseID.toString();
        }
        if (strDefaultProcessorClass == null)
        {
            String strMessageProcessInfoID = (String)messageOut.getMessageHeader().get(TrxMessageHeader.MESSAGE_PROCESS_INFO_ID);
            recMessageProcessInfo = recMessageProcessInfo.getMessageProcessInfo(strMessageProcessInfoID);
            if (recMessageProcessInfo != null)
            {
                Record recMessageInfo = ((ReferenceField)recMessageProcessInfo.getField(MessageProcessInfoModel.MESSAGE_INFO_ID)).getReference();
                if (recMessageInfo != null)
                    if ((recMessageInfo.getEditMode() == DBConstants.EDIT_CURRENT) || (recMessageInfo.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                {
                    String strMessageInfoType = ((ReferenceField)recMessageInfo.getField(MessageInfoModel.MESSAGE_INFO_TYPE_ID)).getReference().getField(MessageInfoTypeModel.CODE).toString();
                    String strContactType = ((ReferenceField)recMessageInfo.getField(MessageInfoModel.CONTACT_TYPE_ID)).getReference().getField(ContactTypeModel.CODE).toString();
                    String strRequestType = RequestTypeModel.ERROR; 
                    String strMessageProcessType = ((ReferenceField)recMessageProcessInfo.getField(MessageProcessInfoModel.MESSAGE_TYPE_ID)).getReference().getField(MessageTypeModel.CODE).toString();
                    String strProcessType = ((ReferenceField)recMessageProcessInfo.getField(MessageProcessInfoModel.PROCESS_TYPE_ID)).getReference().getField(ProcessTypeModel.CODE).toString();
                    
                    recMessageProcessInfo = recMessageProcessInfo.getMessageProcessInfo(strMessageInfoType, strContactType, strRequestType, strMessageProcessType, strProcessType);
                    if (recMessageProcessInfo != null)
                        strDefaultProcessorClass = recMessageProcessInfo.getField(MessageProcessInfoModel.PROCESSOR_CLASS).toString();
                }
            }
        }
        if (strDefaultProcessorClass == null)
            strDefaultProcessorClass = BaseMessageErrorProcessor.class.getName();
        // Process this error
        BaseMessageProcessor messageInProcessor = (BaseMessageProcessor)BaseMessageProcessor.getMessageProcessor(recordOwner.getTask(), null, strDefaultProcessorClass);
        Utility.getLogger().info("processIncommingMessage - processor: " + messageInProcessor);
        BaseMessage errorMessage = null;
        if (messageInProcessor != null)
        {   // Always
            errorMessage = messageInProcessor.processMessage(messageOut);
            messageInProcessor.free();
        }
        return errorMessage;
    }
}
