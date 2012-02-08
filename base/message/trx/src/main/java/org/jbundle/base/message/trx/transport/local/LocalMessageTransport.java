/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.local;

import org.jbundle.base.message.core.trx.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.ExternalMapTrxMessageIn;
import org.jbundle.base.message.trx.message.external.ExternalMapTrxMessageOut;
import org.jbundle.base.message.trx.processor.BaseMessageProcessor;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.model.main.msg.db.MessageInfoTypeModel;
import org.jbundle.model.main.msg.db.MessageStatusModel;
import org.jbundle.model.main.msg.db.MessageTransportModel;
import org.jbundle.model.main.msg.db.MessageTypeModel;
import org.jbundle.model.Task;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.ExternalMessage;
import org.jbundle.thin.base.message.TreeMessage;


/**
 * This is the base class to process an external message.
 */
public class LocalMessageTransport extends BaseMessageTransport
{
    /**
     * Param used to specify the local message code.
     */
    public static final String LOCAL_PROCESSOR = "messageInCode";
    
    /**
     * Default constructor.
     */
    public LocalMessageTransport()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public LocalMessageTransport(Task messageSenderReceiver)
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
        return MessageTransportModel.LOCAL;
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
                    && (MessageTypeModel.MESSAGE_IN.equals((String)message.getMessageHeader().get(TrxMessageHeader.MESSAGE_PROCESS_TYPE))))
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
        String strTrxID = (String)trxMessageHeader.get(TrxMessageHeader.LOG_TRX_ID);
        try {
            // Create a fake Incoming message with this message's external data
            String strMessageCode = (String)trxMessageHeader.get(LocalMessageTransport.LOCAL_PROCESSOR);
            TrxMessageHeader trxMessageHeaderIn = new TrxMessageHeader(null, null);
            trxMessageHeaderIn.put(TrxMessageHeader.MESSAGE_CODE, strMessageCode);

            BaseMessage messageIn = new TreeMessage(trxMessageHeaderIn, null);
            new ExternalMapTrxMessageIn(messageIn, messageOut.getExternalMessage().getRawData());
            // And fake send it by calling this:
            BaseMessage messageReply = this.processIncomingMessage(messageIn, null);
            this.logMessage(strTrxID, messageOut, MessageInfoTypeModel.REQUEST, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.SENT, null, null);

            this.setupReplyMessage(messageReply, null, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_OUT);
            int iErrorCode = this.convertToExternal(messageReply, null);
            if (iErrorCode != DBConstants.NORMAL_RETURN)
            {
                String strMessageDescription = this.getTask().getLastError(iErrorCode);
                if ((strMessageDescription == null) || (strMessageDescription.length() == 0))
                    strMessageDescription = "Error converting to external format";
                messageReply = BaseMessageProcessor.processErrorMessage(this, messageOut, strMessageDescription);
            }

            return messageReply;
        } catch (Throwable ex)  {
            ex.printStackTrace();
            String strError = "Error in processing or replying to a message";
            Utility.getLogger().warning(strError);
            this.logMessage(strTrxID, null, MessageInfoTypeModel.REQUEST, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.ERROR, strError, null);
            return BaseMessageProcessor.processErrorMessage(this, messageOut, strError);
        }
    }
}
