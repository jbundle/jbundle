/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.client;

import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.ExternalMapTrxMessageIn;
import org.jbundle.base.message.trx.message.external.ExternalMapTrxMessageOut;
import org.jbundle.base.message.trx.processor.BaseExternalMessageProcessor;
import org.jbundle.base.message.trx.processor.BaseMessageInProcessor;
import org.jbundle.base.message.trx.processor.BaseMessageProcessor;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.base.message.trx.transport.local.LocalMessageTransport;
import org.jbundle.main.msg.db.MessageProcessInfo;
import org.jbundle.main.msg.db.MessageTransport;
import org.jbundle.main.msg.db.MessageType;
import org.jbundle.model.Task;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.ExternalMessage;


/**
 * This is the base class to process an external message.
 */
public class ClientMessageTransport extends BaseMessageTransport
{
    
    /**
     * Default constructor.
     */
    public ClientMessageTransport()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public ClientMessageTransport(Task messageSenderReceiver)
    {
        this();
        this.init(messageSenderReceiver);    // The one and only
    }
    /**
     * Initializes the MessageProcessor.
     */
    public void init(Task messageSenderReceiver)
    {
        super.init(messageSenderReceiver, null, null);
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
    public String getMessageTransportType()
    {
        return MessageTransport.CLIENT;
    }
    /**
     * Get the external message container for this Internal message.
     * Typically, the overriding class supplies a default format for the transport type.
     * <br/>NOTE: The message header from the internal message is copies, but not the message itself.
     * @param The internalTrxMessage that I will convert to this external format.
     * @return The (empty) External message.
     */
    public ExternalMessage createExternalMessage(BaseMessage message, Object rawData)
    {
        ExternalMessage externalTrxMessage = super.createExternalMessage(message, rawData);
        if (externalTrxMessage == null)
        {
            if ((message.getMessageHeader() != null)
                    && (MessageType.MESSAGE_IN.equals((String)message.getMessageHeader().get(TrxMessageHeader.MESSAGE_PROCESS_TYPE))))
                externalTrxMessage = new ExternalMapTrxMessageIn(message, rawData);
            else
                externalTrxMessage = new ExternalMapTrxMessageOut(message, rawData);
        }
        return externalTrxMessage;
    }
    /**
     * Send the message and (optionally) get the reply.
     * @param messageOut The message to send to be processed.
     * @return The reply message (or null if none).
     */
    public BaseMessage sendMessageRequest(BaseMessage messageOut)
    {
        TrxMessageHeader trxMessageHeader = (TrxMessageHeader)messageOut.getMessageHeader();
        
        BaseMessage messageReply = null;
        String strMessageCode = (String)trxMessageHeader.get(LocalMessageTransport.LOCAL_PROCESSOR);
        if (trxMessageHeader.getMessageInfoMap() != null)
        {
            trxMessageHeader.getMessageInfoMap().remove(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS);
            trxMessageHeader.getMessageInfoMap().remove(TrxMessageHeader.BASE_PACKAGE);
        }
        MessageProcessInfo recMessageProcessInfo = (MessageProcessInfo)this.getRecord(MessageProcessInfo.kMessageProcessInfoFile);
        if (recMessageProcessInfo == null)
            recMessageProcessInfo = new MessageProcessInfo(this);
        messageOut.setMessageHeader(null);  // Since I am basically change this from out to in
        recMessageProcessInfo.setupMessageHeaderFromCode(messageOut.getMessage(), strMessageCode, null);
        recMessageProcessInfo.free();
        String strDefaultProcessorClass = BaseMessageInProcessor.class.getName();
        BaseExternalMessageProcessor messageInProcessor = (BaseExternalMessageProcessor)BaseMessageProcessor.getMessageProcessor(this.getTask(), (BaseMessage)messageOut.getMessage(), strDefaultProcessorClass);
        if (messageInProcessor != null)
        {   // Always
                // Note: Since recProduct is already read you may be able to speed things up by passing recProduct to this process..
            messageReply = messageInProcessor.processMessage((BaseMessage)messageOut.getMessage());
            messageInProcessor.free();
        }
        messageOut.setMessageHeader(trxMessageHeader);  // Restore this to the original message
        // Note messageReply is an internal message, ready to use updating the record
        ExternalMessage externalTrxMessage = this.createExternalMessage(messageReply, null);
        if (externalTrxMessage != null)
            externalTrxMessage.convertInternalToExternal(this);    // Convert my standard message to the OTA EC message
        return messageReply;
    }
}
