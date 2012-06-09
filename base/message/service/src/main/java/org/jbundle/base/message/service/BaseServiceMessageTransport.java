package org.jbundle.base.message.service;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.core.trx.TrxMessageHeader;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.main.msg.db.MessageInfoTypeModel;
import org.jbundle.model.main.msg.db.MessageStatusModel;
import org.jbundle.model.main.msg.db.MessageTransportModel;
import org.jbundle.model.main.msg.db.MessageTypeModel;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.ExternalMessage;
import org.jbundle.thin.base.message.TreeMessage;

public class BaseServiceMessageTransport extends BaseMessageTransport
{

    /**
     * Constructor.
     */
    public BaseServiceMessageTransport()
    {
        super();
    }
    /**
     * Constructor.
     * @param application The parent application.
     * @param strParams The task properties.
     */
    public BaseServiceMessageTransport(RecordOwnerParent task, Record recordMain, Map<String, Object> properties)
    {
        this();
        this.init(task, recordMain, properties);
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free all the resources belonging to this task.
     */
    public void free()
    {
    	super.free();
    }
    /**
     * This is the application code for handling the message.. Once the
     * message is received the application can retrieve the soap part, the
     * attachment part if there are any, or any other information from the
     * message.
     * @param message The incoming message to process.
     */
    public Object processMessage(Object message)
    {
        Utility.getLogger().info("processMessage called in service message");
        BaseMessage msgReplyInternal = null;
        try {
            BaseMessage messageIn = new TreeMessage(null, null);
            new ServiceTrxMessageIn(messageIn, message);

            msgReplyInternal = this.processIncomingMessage(messageIn, null);
            Utility.getLogger().info("msgReplyInternal: " + msgReplyInternal);

            int iErrorCode = this.convertToExternal(msgReplyInternal, null);
            Utility.getLogger().info("externalMessageReply: " + msgReplyInternal);
            Object msg = null;//fac.createMessage();

            if (iErrorCode == DBConstants.NORMAL_RETURN)
            {
                msg = msgReplyInternal.getExternalMessage().getRawData();
                String strTrxID = (String)msgReplyInternal.getMessageHeader().get(TrxMessageHeader.LOG_TRX_ID);
                this.logMessage(strTrxID, msgReplyInternal, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.SENTOK, null, null);    // Sent (no reply required)
            }

            return msg;
        } catch (Throwable ex)  {
            ex.printStackTrace();
            String strError = "Error in processing or replying to a message";
            Utility.getLogger().warning(strError);
            if (msgReplyInternal != null)
            {
                String strTrxID = (String)msgReplyInternal.getMessageHeader().get(TrxMessageHeader.LOG_TRX_ID);
                this.logMessage(strTrxID, msgReplyInternal, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.ERROR, strError, null);
            }
            return null;
        }
    }
    /**
     * Get the message type.
     * @return the type of message this concrete class processes.
     */
    public String getMessageTransportType()
    {
        return MessageTransportModel.SERVICE;
    }
    /**
     * From this external message, figure out the message type.
     * @externalMessage The external message just received.
     * @return The message type for this kind of message (transport specific).
     */
    public String getMessageCode(BaseMessage externalMessage)
    {
        if (!(externalMessage.getExternalMessage() instanceof ServiceTrxMessageIn))
            return null;
        Object message = externalMessage.getExternalMessage().getRawData();
        if (message != null)
            return "OTA_" + message.getClass().getSimpleName();
        return null;    // No type found.
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
        ExternalMessage externalTrxMessageOut = super.createExternalMessage(message, rawData);
        if (externalTrxMessageOut == null)
        {
            if (MessageTypeModel.MESSAGE_IN.equalsIgnoreCase((String)message.get(TrxMessageHeader.MESSAGE_PROCESS_TYPE)))
                externalTrxMessageOut = new ServiceTrxMessageIn(message, rawData);
            else
                externalTrxMessageOut = new ServiceTrxMessageOut(message, rawData);
        }
        return externalTrxMessageOut;
    }
    @Override
    public BaseMessage sendMessageRequest(BaseMessage messageOut) {
        return null;    // Service transport is one-way (out)
    }
}
