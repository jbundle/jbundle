/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.ExternalTrxMessage;
import org.jbundle.base.message.trx.processor.BaseExternalMessageProcessor;
import org.jbundle.base.message.trx.processor.BaseInternalMessageProcessor;
import org.jbundle.base.message.trx.processor.BaseMessageInProcessor;
import org.jbundle.base.message.trx.processor.BaseMessageOutProcessor;
import org.jbundle.base.message.trx.processor.BaseMessageProcessor;
import org.jbundle.base.message.trx.processor.BaseMessageReplyInProcessor;
import org.jbundle.base.message.trx.processor.BaseMessageReplyOutProcessor;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.thread.BaseRecordOwner;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.db.Rec;
import org.jbundle.model.main.db.base.ContactTypeModel;
import org.jbundle.model.main.msg.db.MessageInfoTypeModel;
import org.jbundle.model.main.msg.db.MessageLogModel;
import org.jbundle.model.main.msg.db.MessageProcessInfoModel;
import org.jbundle.model.main.msg.db.MessageStatusModel;
import org.jbundle.model.main.msg.db.MessageTransportModel;
import org.jbundle.model.main.msg.db.MessageTypeModel;
import org.jbundle.model.main.msg.db.MessageVersionModel;
import org.jbundle.model.main.user.db.UserInfoModel;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.ExternalMessage;
import org.jbundle.thin.base.message.MessageRecordDesc;
import org.jbundle.thin.main.msg.db.MessageLog;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * This is the base class to process an external message.
 */
public abstract class BaseMessageTransport extends BaseRecordOwner
{
    /**
     * Default constructor.
     */
    public BaseMessageTransport()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public BaseMessageTransport(Task messageSenderReceiver)
    {
        this();
        this.init(messageSenderReceiver, null, null);    // The one and only
    }
    /**
     * Initialization.
     */
    public BaseMessageTransport(RecordOwnerParent parent, Rec recordMain, Map<String, Object> properties)
    {
        this();
        this.init(parent, recordMain, properties);
    }
    /**
     * Initialize the RecordOwner.
     * @param parentSessionObject Parent that created this session object.
     * @param record Main record for this session (opt).
     * @param objectID ObjectID of the object that this SessionObject represents (usually a URL or bookmark).
     */
    public void init(RecordOwnerParent parent, Rec recordMain, Map<String, Object> properties)
    {
        super.init(parent, recordMain, properties);
        if (properties != null)
            m_propTransport = properties;
        else
            m_propTransport = this.getTransportProperties();
    }
    /**
     * Get this Property for this key.
     * Override this to do something other than return the parent's property.
     * @param strProperty The key to lookup.
     * @return The value for this key.
     */
    public String getProperty(String strProperty)
    {
        if (m_propTransport != null)
            if (m_propTransport.get(strProperty) != null)
                return (String)m_propTransport.get(strProperty);
        return super.getProperty(strProperty);
    }
    /**
     * Properties from the transport file.
     */
    protected Map<String,Object> m_propTransport = null;
    /**
     * Get the properties that go with this transport type.
     */
    public Map<String,Object> getTransportProperties()
    {
        Map<String,Object> propMessageTransport = null;
        Record recMessageTransport = Record.makeRecordFromClassName(MessageTransportModel.THICK_CLASS, this);
        recMessageTransport.setKeyArea(MessageTransportModel.CODE_KEY);
        String strMessageType = this.getMessageTransportType();
        recMessageTransport.getField(MessageTransportModel.CODE).setString(strMessageType);
        try {
            if (recMessageTransport.seek(null))
            {
                PropertiesField fldProperty = (PropertiesField)recMessageTransport.getField(MessageTransportModel.PROPERTIES);
                propMessageTransport = fldProperty.loadProperties();
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            recMessageTransport.free();
            recMessageTransport = null;
        }
        return propMessageTransport;
    }
    /**
     * Free all the resources belonging to this class.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the message type.
     * @return the type of message this concrete class processes.
     */
    public abstract String getMessageTransportType();
    /**
     * Using this transport, send this message (using this processor) and (optionally) process the reply.
     * @param internalTrxMessage The message to send.
     * @param messageOutProcessor The message out processor.
     */
    public void sendMessage(BaseMessage messageOut, BaseInternalMessageProcessor messageOutProcessor)
    {
        int iErrorCode = this.convertToExternal(messageOut, messageOutProcessor);    // Convert my standard message to the external format for this message
        BaseMessage messageReplyIn = null;
        if (iErrorCode != DBConstants.NORMAL_RETURN)
        {
            String strMessageDescription = this.getTask().getLastError(iErrorCode);
            if ((strMessageDescription == null) || (strMessageDescription.length() == 0))
                strMessageDescription = "Error converting to external format";
            iErrorCode = this.getTask().setLastError(strMessageDescription);
            messageReplyIn = BaseMessageProcessor.processErrorMessage(this, messageOut, strMessageDescription);
        }
        else
        {
            Utility.getLogger().info("sendMessage externalTrxMessage = " + messageOut);

            messageReplyIn = this.sendMessageRequest(messageOut);
        }
        if (messageReplyIn != null)    // No reply if null.
        {
            this.setupReplyMessage(messageReplyIn, messageOut, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_IN);
            Utility.getLogger().info("externalMessageReply: " + messageReplyIn);
            this.processIncomingMessage(messageReplyIn, messageOut);
        }
    }
    /**
     * Using this transport, process this internal message and convert to the external format.
     * (I'm getting this message ready to send externally)
     * @param messageOut The message to send.
     * @param messageOutProcessor The message out processor.
     * @return Error code
     */
    public int convertToExternal(BaseMessage messageOut, BaseInternalMessageProcessor messageOutProcessor)
    {
        String strMessageInfoType = MessageInfoTypeModel.REQUEST;
        String strMessageProcessType = MessageTypeModel.MESSAGE_OUT;
        if (messageOut.getMessageHeader() != null)
        {
            strMessageInfoType = (String)messageOut.getMessageHeader().get(TrxMessageHeader.MESSAGE_INFO_TYPE);
            strMessageProcessType = (String)messageOut.getMessageHeader().get(TrxMessageHeader.MESSAGE_PROCESS_TYPE);
        }
        if (messageOutProcessor == null)
        {
            String strDefaultProcessorClass = MessageTypeModel.MESSAGE_OUT.equals(strMessageProcessType) ? BaseMessageOutProcessor.class.getName() : BaseMessageReplyOutProcessor.class.getName();
            messageOutProcessor = (BaseInternalMessageProcessor)BaseMessageProcessor.getMessageProcessor(this.getTask(), messageOut, strDefaultProcessorClass);
        }
        messageOut.createMessageDataDesc();
        messageOutProcessor.processMessage(messageOut);
        String strMessageStatus = MessageTypeModel.MESSAGE_OUT.equals(strMessageProcessType) ? MessageStatusModel.SENT : MessageStatusModel.SENTOK;   // Reply does not wait for a reply
        ExternalMessage externalTrxMessage = this.createExternalMessage(messageOut, null);
        Utility.getLogger().info("sendMessage  externalTrxMessage = " + externalTrxMessage);
        String strTrxID = this.logMessage(null, messageOut, strMessageInfoType, strMessageProcessType, strMessageStatus, null, null);
        if (messageOut.getMessageHeader() instanceof TrxMessageHeader)
            ((TrxMessageHeader)messageOut.getMessageHeader()).put(TrxMessageHeader.LOG_TRX_ID, strTrxID);
        int iErrorCode = DBConstants.ERROR_RETURN;
        if (externalTrxMessage != null)
            iErrorCode = externalTrxMessage.convertInternalToExternal(this);    // Convert my standard message to the OTA EC message
        String strMessageDescription = null;
        if (iErrorCode != DBConstants.NORMAL_RETURN)
        {
            strMessageDescription = this.getTask().getLastError(iErrorCode);
            if ((strMessageDescription == null) || (strMessageDescription.length() == 0))
                strMessageDescription = "Error converting to external format";
            iErrorCode = this.getTask().setLastError(strMessageDescription);
            strMessageStatus = MessageStatusModel.ERROR;
        }
        this.logMessage(strTrxID, messageOut, strMessageInfoType, strMessageProcessType, strMessageStatus, strMessageDescription, null); // Log it with the external info
        return iErrorCode;
    }
    /**
     * Send the message and (optionally) get the reply.
     * Override this to do something.
     * @param nodeMessage The XML tree to send.
     * @param strDest The destination URL string.
     * @return The reply message (or null if none).
     */
    public abstract BaseMessage sendMessageRequest(BaseMessage messageOut);
    /**
     * Here is an incoming message.
     * Figure out what it is and process it.
     * Note: the caller should have logged this message since I have no way to serialize the raw data.
     * @param messageReplyIn - The incoming message
     * @param messageOut - The (optional) outgoing message that this is a reply to (null if unknown).
     * @return The optional return message.
     */
    public BaseMessage processIncomingMessage(BaseMessage messageReplyIn, BaseMessage messageOut)
    {
        messageReplyIn = this.convertExternalReplyToInternal(messageReplyIn, messageOut);

        String strMessageInfoType = this.getMessageInfoType(messageReplyIn);
        String strDefaultProcessorClass = MessageInfoTypeModel.REQUEST.equals(strMessageInfoType) ? BaseMessageInProcessor.class.getName() : BaseMessageReplyInProcessor.class.getName();
        BaseExternalMessageProcessor messageInProcessor = (BaseExternalMessageProcessor)BaseMessageProcessor.getMessageProcessor(this.getTask(), messageReplyIn, strDefaultProcessorClass);
        Utility.getLogger().info("processIncommingMessage - processor: " + messageInProcessor);
        BaseMessage messageReply = null;
        if (messageInProcessor == null)
        {
            String strErrorMessage = "Message in processor not found " + strDefaultProcessorClass;
            messageReply = BaseMessageProcessor.processErrorMessage(this, messageReplyIn, strErrorMessage);
        }
        else
        {
            messageReply = messageInProcessor.processMessage(messageReplyIn);
            messageInProcessor.free();
        }
        // Next step - setup the reply
        if (messageReply != null)   // Never for replies.
        {
            this.setupReplyMessage(messageReply, messageReplyIn, null, null);
            if ((messageReply.getMessageDataDesc(null) != null)
                && (messageReplyIn.getMessageDataDesc(null) != null))
                    ((MessageRecordDesc)messageReply.getMessageDataDesc(null)).moveRequestInfoToReply(messageReplyIn);

            Utility.getLogger().info("returning msgReplyInternal: " + messageReplyIn);
            return messageReply;    // Have the caller process (send) this return message
        }
        return null;
    }
    /**
     * Here is an incoming message.
     * Figure out what it is and process it.
     * Note: the caller should have logged this message since I have no way to serialize the raw data.
     * @param messageReplyIn - The incoming message
     * @param messageOut - The (optional) outgoing message that this is a reply to (null if unknown).
     * @return The converted reply message.
     */
    public BaseMessage convertExternalReplyToInternal(BaseMessage messageReplyIn, BaseMessage messageOut)
    {
        if (messageReplyIn.getExternalMessage() == null)
            if (messageReplyIn.getMessage() != null)
                return messageReplyIn;   // Probably an error reply message
        String strMessageProcessType = this.getMessageProcessType(messageReplyIn);
        String strMessageInfoType = this.getMessageInfoType(messageReplyIn);
        if (MessageTypeModel.MESSAGE_IN.equals(strMessageProcessType))
        {   // Process a normal message request.
            String strMessageCode = this.getMessageCode(messageReplyIn);
            String strMessageVersion = this.getMessageVersion(messageReplyIn);
            if (strMessageVersion == null)
                strMessageVersion = this.getTask().getProperty("version");
            Record recMessageProcessInfo = this.getRecord(MessageProcessInfoModel.MESSAGE_PROCESS_INFO_FILE);
            if (recMessageProcessInfo == null)
                recMessageProcessInfo = Record.makeRecordFromClassName(MessageProcessInfoModel.THICK_CLASS, this);
            this.addMessageTransportType(messageReplyIn);  // This will insure I get the transport properties
            ((MessageProcessInfoModel)recMessageProcessInfo).setupMessageHeaderFromCode(messageReplyIn, strMessageCode, strMessageVersion);
            messageReplyIn.createMessageDataDesc();
        }
//x        this.createExternalMessage(messageReplyIn, messageReplyIn.getExternalMessage().getRawData());    // Convert the external format to the correct class (if necessary)
        // Step 3- Unmarshall the message into the standard form.
        int iErrorCode = messageReplyIn.getExternalMessage().convertExternalToInternal(this);
        String strTrxID = this.logMessage(null, messageReplyIn, strMessageInfoType, strMessageProcessType, MessageStatusModel.RECEIVED, null, null);
        Utility.getLogger().info("processIncommingMessage  type: in " + messageReplyIn);
        if (messageOut != null)
            if (messageReplyIn.getMessageDataDesc(null) instanceof MessageRecordDesc)  // Always
                ((MessageRecordDesc)messageReplyIn.getMessageDataDesc(null)).moveRequestInfoToReply(messageOut);
        if ((iErrorCode != DBConstants.NORMAL_RETURN)
                || (messageReplyIn.getExternalMessage() == null)
                    || (messageReplyIn.getExternalMessage().getRawData() == null))
            {   // Set error return message: message type unknown
                String strMessageError = this.getTask().getLastError(iErrorCode);
                if ((strMessageError == null) || (strMessageError.length() == 0))
                    strMessageError = "Error converting to internal format";
                if (messageReplyIn.getMessageHeader() instanceof TrxMessageHeader)
                    if (((TrxMessageHeader)messageReplyIn.getMessageHeader()).get(TrxMessageHeader.MESSAGE_ERROR) != null)
                        strMessageError = ((TrxMessageHeader)messageReplyIn.getMessageHeader()).get(TrxMessageHeader.MESSAGE_ERROR).toString();
                this.logMessage(strTrxID, messageReplyIn, strMessageInfoType, strMessageProcessType, MessageStatusModel.ERROR, strMessageError, null);
                return BaseMessageProcessor.processErrorMessage(this, messageReplyIn, strMessageError);
            }
        return messageReplyIn;
    }
    /**
     * Is this message a reply (or a request).
     * @param externalMessage The incomming message.
     * @return MessageType.MESSAGE_IN or MessageType.REPLY_IN If this is a reply.
     */
    public String getMessageInfoType(BaseMessage externalMessage)
    {
        if (externalMessage != null)
            if (externalMessage.getMessageHeader() != null)
                if (externalMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_INFO_TYPE) != null)
                    return (String)externalMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_INFO_TYPE);
        return MessageInfoTypeModel.REQUEST;   // Override this to check for sure.
    }
    /**
     * Is this message a reply (or a request).
     * @param externalMessage The incomming message.
     * @return MessageType.MESSAGE_IN or MessageType.REPLY_IN If this is a reply.
     */
    public String getMessageProcessType(BaseMessage externalMessage)
    {
        if (externalMessage.getMessageHeader() != null)
            if (externalMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_PROCESS_TYPE) != null)
                return (String)externalMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_PROCESS_TYPE);
        return MessageTypeModel.MESSAGE_IN;   // Override this to check for sure.
    }
    /**
     * From this external message, figure out the message type.
     * @externalMessage The external message just received.
     * @return The message type for this kind of message (transport specific).
     */
    public String getMessageCode(BaseMessage externalTrxMessage)
    {
        if (externalTrxMessage.getMessageHeader() == null)
            return null;
        return (String)externalTrxMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_CODE);
    }
    /**
     * From this external message, figure out the message type.
     * @externalMessage The external message just received.
     * @return The message type for this kind of message (transport specific).
     */
    public String getMessageVersion(BaseMessage externalTrxMessage)
    {
        if (externalTrxMessage.getMessageHeader() == null)
            return null;
        return (String)externalTrxMessage.getMessageHeader().get(MessageVersionModel.VERSION);
    }
    /**
     * Set up a message header for this reply using the message header for the original message.
     * @param messageReply The (internal or external) reply message (typically without a header).
     * @param trxMessageHeaderIncomming The header of the message that I'm replying to.
     * @param The type of message to tag this reply as (Reply in, Reply out, or null = unknown).
     */
    public void setupReplyMessage(BaseMessage messageReply, BaseMessage messageOut, String strMessageInfoType, String strMessageProcessType)
    {
        if (messageReply != null)    // No reply if null.
        {
            TrxMessageHeader trxMessageHeaderIncomming = null;
            if (messageOut != null)
                trxMessageHeaderIncomming = (TrxMessageHeader)messageOut.getMessageHeader();
            if (trxMessageHeaderIncomming != null)
            {
                TrxMessageHeader replyHeader = (TrxMessageHeader)messageReply.getMessageHeader();
                if (replyHeader == null)
                    messageReply.setMessageHeader(replyHeader = new TrxMessageHeader(null, null));
                replyHeader.putAll(trxMessageHeaderIncomming.createReplyHeader());
                if (strMessageInfoType != null)
                    ((TrxMessageHeader)messageReply.getMessageHeader()).put(TrxMessageHeader.MESSAGE_INFO_TYPE, strMessageInfoType);   // Make sure this is seen as a reply
                if (strMessageProcessType != null)
                    ((TrxMessageHeader)messageReply.getMessageHeader()).put(TrxMessageHeader.MESSAGE_PROCESS_TYPE, strMessageProcessType);   // Make sure this is seen as a reply
                if (((TrxMessageHeader)messageReply.getMessageHeader()).getMessageInfoMap() != null)
                    ((TrxMessageHeader)messageReply.getMessageHeader()).getMessageInfoMap().remove(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS);   // The reply never uses the message in's processor
                Record recMessageProcessInfo = this.getRecord(MessageProcessInfoModel.MESSAGE_PROCESS_INFO_FILE);
                if (recMessageProcessInfo == null)
                    recMessageProcessInfo = Record.makeRecordFromClassName(MessageProcessInfoModel.THICK_CLASS, this);
                this.addMessageTransportType(messageReply);  // This will insure I get the transport properties
                ((MessageProcessInfoModel)recMessageProcessInfo).setupMessageHeaderFromCode(messageReply, null, null);
            }
            if (strMessageInfoType != null)
                ((TrxMessageHeader)messageReply.getMessageHeader()).put(TrxMessageHeader.MESSAGE_INFO_TYPE, strMessageInfoType);   // Make sure this is seen as a reply
            if (strMessageProcessType != null)
                ((TrxMessageHeader)messageReply.getMessageHeader()).put(TrxMessageHeader.MESSAGE_PROCESS_TYPE, strMessageProcessType);   // Make sure this is seen as a reply
        }
    }
    /**
     * Get the external message container for this Internal message.
     * Typically, the overriding class supplies a default format for the transport type.
     * <br/>NOTE: The message header from the internal message is copies, but not the message itself.
     * @param The internalTrxMessage that I will convert to this external format.
     * @return The (empty) External message.
     */
    public String getMessageClassName(TrxMessageHeader trxMessageHeader)
    {
        String strMessageClass = this.getProperty(trxMessageHeader, TrxMessageHeader.EXTERNAL_MESSAGE_CLASS);
        String strPackage = (String)trxMessageHeader.get(TrxMessageHeader.BASE_PACKAGE);
        strMessageClass = ClassServiceUtility.getFullClassName(strPackage, strMessageClass);
        return strMessageClass;
    }
    /**
     * Get this transport specific param.
     * Get this param. The transport properties specify param names by adding the word
     * param after it. For example, to specify a different param for the messageClass
     * for a SOAP transport, the SOAP property messageClassParam=SOAPMessageClass should
     * be set in the SOAP transport properties and the SOAPMessageClass should be set in
     * the messageProperties so it knows which message class to use for SOAP messages.
     * @param trxMessageHeader The message header to find a param in.
     * @param strParam The param name.
     * @return The parameter.
     */
    public String getProperty(TrxMessageHeader trxMessageHeader, String strParamName)
    {
        String strProperty = (String)trxMessageHeader.get(strParamName);
        if (strProperty == null)
            strProperty = this.getProperty(strParamName);
        return strProperty;
    }
    /**
     * Get the external message container for this Internal message.
     * Typically, the overriding class supplies a default format for the transport type.
     * <br/>NOTE: The message header from the internal message is copied, but not the message itself.
     * @param rawData The raw data
     * @param The internalTrxMessage that I will convert to this external format.
     * @return The (empty) External message.
     */
    public ExternalMessage createExternalMessage(BaseMessage message, Object rawData)
    {
        ExternalTrxMessage externalTrxMessage = null;
        TrxMessageHeader trxMessageHeader = (TrxMessageHeader)message.getMessageHeader();
        String strMessageClass = null;
        if (trxMessageHeader != null)
            strMessageClass = this.getMessageClassName(trxMessageHeader);
        externalTrxMessage = (ExternalTrxMessage)ClassServiceUtility.getClassService().makeObjectFromClassName(strMessageClass);
        if (externalTrxMessage != null)
        	externalTrxMessage.init(message, rawData);
        // Note: A null externalTrxMessageOut is never returned, because the concrete transport class supplies the default class.
        return externalTrxMessage;
    }
    /**
     * From this external message, get the message information (in a map).
     * @param externalMessage The external message.
     */
    public void addMessageTransportType(BaseMessage externalTrxMessage)
    {
        String strTransportType = this.getMessageTransportType();
        Record recMessageTransport = this.getRecord(MessageTransportModel.MESSAGE_TRANSPORT_FILE);
        if (recMessageTransport == null)
            recMessageTransport = Record.makeRecordFromClassName(MessageTransportModel.THICK_CLASS, this);
        try {
            recMessageTransport.setKeyArea(MessageTransportModel.CODE_KEY);
            recMessageTransport.getField(MessageTransportModel.CODE).setString(strTransportType);
            if (recMessageTransport.seek(DBConstants.EQUALS))
            {   // Always
                TrxMessageHeader trxMessageHeader = (TrxMessageHeader)externalTrxMessage.getMessageHeader();
                if (trxMessageHeader == null)
                {
                    trxMessageHeader = new TrxMessageHeader(null, null);
                    externalTrxMessage.setMessageHeader(trxMessageHeader);
                }
                Map<String,Object> mapTransport = trxMessageHeader.getMessageTransportMap();
                if (mapTransport == null)
                    mapTransport = new HashMap<String,Object>();
                mapTransport.put(MessageTransportModel.TRANSPORT_ID_PARAM, recMessageTransport.getCounterField().toString());
                trxMessageHeader.setMessageTransportMap(mapTransport);
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Write this outgoing message into the log.
     * @param The (optional) TrxID If this is non null this is a new transaction, otherwise, update this trx ID.
     * @param msg The message.
     * @urlEndpoint The destination.
     * @return The message log transaction number.
     */
    public String logMessage(String strTrxIDIn, BaseMessage trxMessage, String strMessageInfoType, String strMessageProcessType, String strMessageStatus, String strMessageDescription, String strMessageMimeType)
    {
        String strTrxID = strTrxIDIn;
        int iUserID = -1;
        String strContactType = null;
        String strContact = null;
        int iMessageReferenceID = -1;

        Task taskParent = this.getTask();
        Record recMessageLog = this.getRecord(MessageLog.MESSAGE_LOG_FILE);
        if (recMessageLog == null)
            recMessageLog = Record.makeRecordFromClassName(MessageLogModel.THICK_CLASS, this);
        try {
            if (strTrxID != null)
            {
                recMessageLog.getField(MessageLogModel.ID).setString(strTrxID);
                recMessageLog.setKeyArea(MessageLogModel.ID);
                if (recMessageLog.seek(null))
                {
                    recMessageLog.edit();
                }
                else
                    strTrxID = null;
            }
            if (strTrxID == null)
                recMessageLog.addNew();
            ReferenceField fldReference = (ReferenceField)recMessageLog.getField(MessageLogModel.MESSAGE_INFO_TYPE_ID);
            int iMessageInfoTypeID = fldReference.getIDFromCode(strMessageInfoType);
            fldReference.setValue(iMessageInfoTypeID);  // Message type
            fldReference = (ReferenceField)recMessageLog.getField(MessageLogModel.MESSAGE_TYPE_ID);
            int iMessageProcessTypeID = fldReference.getIDFromCode(strMessageProcessType);
            fldReference.setValue(iMessageProcessTypeID);  // Message type
            
            fldReference = (ReferenceField)recMessageLog.getField(MessageLogModel.MESSAGE_STATUS_ID);
            String objNativeMessage = null;
            if (objNativeMessage == null)
                if (trxMessage != null)
                    if (trxMessage.getExternalMessage() != null)
                        if (!MessageStatusModel.ERROR.equals(strMessageStatus))
                            objNativeMessage = trxMessage.getExternalMessage().toString();
            if (objNativeMessage == null)
                if (strMessageStatus == null)
                    strMessageStatus = MessageStatusModel.TRX_ID_HOLD;
            if (strMessageStatus == null)
                strMessageStatus = MessageStatusModel.UNKNOWN;
            int iMessageTimeout = 0;
            if (trxMessage != null)
                if (MessageStatusModel.SENT.equalsIgnoreCase(strMessageStatus))
                {
                    if ((trxMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_RESPONSE_ID) == null)
                        && (trxMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_RESPONSE_CODE) == null)
                        && (trxMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_RESPONSE_CLASS) == null))
                            strMessageStatus = MessageStatusModel.SENTOK;    // If I'm not expecting a response, status is sent okay.
                    else
                        if (trxMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_TIMEOUT) != null)
                        {
                            try {
                                iMessageTimeout = Integer.parseInt((String)trxMessage.getMessageHeader().get(TrxMessageHeader.MESSAGE_TIMEOUT));
                            } catch (NumberFormatException ex) {
                                iMessageTimeout = 0;
                            }
                        }
                }
            recMessageLog.getField(MessageLogModel.TIMEOUT_SECONDS).setValue(iMessageTimeout);
            if ((fldReference.isNull()) || (!MessageStatusModel.ERROR.equalsIgnoreCase(fldReference.getReference().getField(MessageStatusModel.CODE).toString())))
            {   // Can't change error status to something else
                int iMessageStatusID = fldReference.getIDFromCode(strMessageStatus);
                fldReference.setValue(iMessageStatusID);  // Message type
            }
            
            String strMessageTransport = this.getMessageTransportType();
            fldReference = (ReferenceField)recMessageLog.getField(MessageLogModel.MESSAGE_TRANSPORT_ID);
            int iMessageTransportID = fldReference.getIDFromCode(strMessageTransport);
            fldReference.setValue(iMessageTransportID);  // Message type
            
            String strReferenceType = null;
            int iMessageProcessInfoID = -1;

            if (!MessageStatusModel.ERROR.equals(strMessageStatus))
                if ((trxMessage != null) && (trxMessage.getMessageHeader() != null))
            {
                TrxMessageHeader trxMessageHeader = (TrxMessageHeader)trxMessage.getMessageHeader();
                if (strMessageDescription == null)
                    strMessageDescription = Utility.convertObjectToString(trxMessageHeader.get(TrxMessageHeader.DESCRIPTION));
                strContactType = Utility.convertObjectToString(trxMessageHeader.get(TrxMessageHeader.CONTACT_TYPE));
                if (strContactType != null)
                    if (strContactType.length() > 0)
                        if (!Utility.isNumeric(strContactType))
                {       // Convert the contact type from the record name to the ID.
                    Record recContactType = ((ReferenceField)recMessageLog.getField(MessageLogModel.CONTACT_TYPE_ID)).getReferenceRecord();
                    recContactType.setKeyArea(ContactTypeModel.CODE_KEY);
                    recContactType.getField(ContactTypeModel.CODE).setString(strContactType);
                    try {
                        if (recContactType.seek(null))
                        {   // Success
                            strContactType = recContactType.getField(ContactTypeModel.ID).toString();
                        }
                    } catch (DBException ex)    {
                        ex.printStackTrace();
                    }
                }
                strContact = Utility.convertObjectToString(trxMessageHeader.get(TrxMessageHeader.CONTACT_ID));
                Object objUserID = trxMessageHeader.get(DBParams.USER_ID);
                if (objUserID == null)
                    objUserID = trxMessageHeader.get(DBParams.USER_NAME);
                if (objUserID instanceof Integer)
                    iUserID = ((Integer)objUserID).intValue();
                else if (objUserID instanceof String)
                {
                    if (Utility.isNumeric((String)objUserID))
                        iUserID = Integer.parseInt((String)objUserID);
                    else
                    {
                        Record recUserInfo = Record.makeRecordFromClassName(UserInfoModel.THICK_CLASS, this);
                        if (((UserInfoModel)recUserInfo).getUserInfo((String)objUserID, false))
                            iUserID = (int)recUserInfo.getField(UserInfoModel.ID).getValue();
                        recUserInfo.free();
                    }
                }
                Object objMessageReferenceID = trxMessageHeader.get(TrxMessageHeader.REFERENCE_ID);
                if (objMessageReferenceID == null)
                	objMessageReferenceID = trxMessage.get(TrxMessageHeader.REFERENCE_ID);
                if (objMessageReferenceID instanceof Integer)
                    iMessageReferenceID = ((Integer)objMessageReferenceID).intValue();
                else if (objMessageReferenceID instanceof String)
                {
                    if (Utility.isNumeric((String)objMessageReferenceID))
                        iMessageReferenceID = Integer.parseInt((String)objMessageReferenceID);
                }
                if (trxMessageHeader.get(TrxMessageHeader.REFERENCE_TYPE) != null)
                	strReferenceType = (String)trxMessageHeader.get(TrxMessageHeader.REFERENCE_TYPE);
                if (strReferenceType == null)
                		strReferenceType = (String)trxMessage.get(TrxMessageHeader.REFERENCE_TYPE);
                if (trxMessageHeader.get(TrxMessageHeader.MESSAGE_PROCESS_INFO_ID) != null)
                {
                    Object objMessageProcessInfoID = trxMessageHeader.get(TrxMessageHeader.MESSAGE_PROCESS_INFO_ID);
                    if (objMessageProcessInfoID instanceof Integer)
                    	iMessageProcessInfoID = ((Integer)objMessageProcessInfoID).intValue();
                    else if (objMessageProcessInfoID instanceof String)
                    {
                        if (Utility.isNumeric((String)objMessageProcessInfoID))
                        	iMessageProcessInfoID = Integer.parseInt((String)objMessageProcessInfoID);
                    }
                }
            }

            if (iUserID != -1)
                recMessageLog.getField(MessageLogModel.USER_ID).setValue(iUserID);
            else if (taskParent != null)
                recMessageLog.getField(MessageLogModel.USER_ID).setString(((BaseApplication)taskParent.getApplication()).getUserID());

            if (strContactType != null)
                recMessageLog.getField(MessageLogModel.CONTACT_TYPE_ID).setString(strContactType);
            if (strContact != null)
                recMessageLog.getField(MessageLogModel.CONTACT_ID).setString(strContact);
            if (strMessageDescription != null)
                recMessageLog.getField(MessageLogModel.DESCRIPTION).setString(strMessageDescription);
            recMessageLog.getField(MessageLogModel.MESSAGE_TIME).setValue(DateTimeField.currentTime());
            if (iMessageProcessInfoID != -1)
                recMessageLog.getField(MessageLogModel.MESSAGE_PROCESS_INFO_ID).setValue(iMessageProcessInfoID);
            if (strReferenceType != null)
            	recMessageLog.getField(MessageLogModel.REFERENCE_TYPE).setString(strReferenceType);
            if (iMessageReferenceID != -1)
                recMessageLog.getField(MessageLogModel.REFERENCE_ID).setValue(iMessageReferenceID);
            if (strMessageInfoType != null)
                recMessageLog.getField(MessageLogModel.MESSAGE_DATA_TYPE).setString(strMessageInfoType);

            if (!MessageStatusModel.ERROR.equals(strMessageStatus))
                if (trxMessage != null)
            {
                recMessageLog.getField(MessageLogModel.MESSAGE_CLASS_NAME).setString(trxMessage.getClass().getName());
                if (trxMessage.getMessageHeader() != null)
                {
                    recMessageLog.getField(MessageLogModel.MESSAGE_HEADER_CLASS_NAME).setString(trxMessage.getMessageHeader().getClass().getName());
                    recMessageLog.getField(MessageLogModel.MESSAGE_QUEUE_NAME).setString(trxMessage.getMessageHeader().getQueueName());
                    recMessageLog.getField(MessageLogModel.MESSAGE_QUEUE_TYPE).setString(trxMessage.getMessageHeader().getQueueType());
                    Map<String,Object> propHeaderInfo = ((TrxMessageHeader)trxMessage.getMessageHeader()).getMessageInfoMap();
                    if (propHeaderInfo != null)
                        ((PropertiesField)recMessageLog.getField(MessageLogModel.MESSAGE_INFO_PROPERTIES)).setProperties(propHeaderInfo);
                    Map<String,Object> propMessageHeader = ((TrxMessageHeader)trxMessage.getMessageHeader()).getMessageHeaderMap();
                    if (propMessageHeader != null)
                        ((PropertiesField)recMessageLog.getField(MessageLogModel.MESSAGE_HEADER_PROPERTIES)).setProperties(propMessageHeader);
                    Map<String,Object> propHeaderTransport = ((TrxMessageHeader)trxMessage.getMessageHeader()).getMessageTransportMap();
                    if (propHeaderTransport != null)
                        ((PropertiesField)recMessageLog.getField(MessageLogModel.MESSAGE_TRANSPORT_PROPERTIES)).setProperties(propHeaderTransport);
                }
                String strMessage = trxMessage.getXML(false);
                recMessageLog.getField(MessageLogModel.XML_MESSAGE_DATA).setString(strMessage);
                if (trxMessage.getMessageDataDesc(null) != null)
                    recMessageLog.getField(MessageLogModel.MESSAGE_DATA_CLASS_NAME).setString(trxMessage.getMessageDataDesc(null).getClass().getName());
                if (trxMessage.getExternalMessage() != null)
                    recMessageLog.getField(MessageLogModel.EXTERNAL_MESSAGE_CLASS_NAME).setString(trxMessage.getExternalMessage().getClass().getName());
            }
            if (objNativeMessage != null)
                recMessageLog.getField(MessageLogModel.MESSAGE_DATA).setString(objNativeMessage.toString());

            if (MessageStatusModel.ERROR.equals(strMessageStatus))
                recMessageLog.getField(MessageLogModel.ERROR_TEXT).setString(strMessageDescription);

            if (strTrxID == null)
            {
                recMessageLog.add();
                strTrxID = recMessageLog.getLastModified(DBConstants.BOOKMARK_HANDLE).toString();
            }
            else
                recMessageLog.set();
            
        } catch (DBException ex)    {
            ex.printStackTrace();
            strTrxID = null;
        } finally {
            // No, leave it here!
            // recMessageLog.free();
        }
        
        if ((MessageStatusModel.TRX_ID_HOLD.equals(strMessageStatus)) || (strTrxIDIn == null))
            if (trxMessage != null)
                if (trxMessage.getMessageHeader() instanceof TrxMessageHeader)
                    ((TrxMessageHeader)trxMessage.getMessageHeader()).put(TrxMessageHeader.LOG_TRX_ID, strTrxID);

        return strTrxID;
    }
}
