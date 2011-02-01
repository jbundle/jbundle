package org.jbundle.base.message.trx.transport.fax;

import javax.mail.Message;

import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.transport.email.EmailMessageTransport;
import org.jbundle.main.msg.db.MessageTransport;
import org.jbundle.model.Task;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.ExternalMessage;


/**
 * This is the base class to process an external message.
 */
public class FaxMessageTransport extends EmailMessageTransport
{
    /**
     * Default constructor.
     */
    public FaxMessageTransport()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public FaxMessageTransport(Task messageSenderReceiver)
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
        return MessageTransport.FAX;
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
        return super.createExternalMessage(message, rawData);
    }
    /**
     * Is this message a reply (or a request).
     * @param externalMessage The incomming message.
     * @return MessageType.MESSAGE_IN or MessageType.REPLY_IN If this is a reply.
     */
    public String getMessageInfoType(BaseMessage externalMessage)
    {
        return super.getMessageInfoType(externalMessage);
    }
    /**
     * Is this message a reply (or a request).
     * @param externalMessage The incoming message.
     * @return MessageType.MESSAGE_IN or MessageType.REPLY_IN If this is a reply.
     */
    public String getMessageProcessType(BaseMessage externalMessage)
    {
        return super.getMessageProcessType(externalMessage);
    }
    /**
     * Send the message and (optionally) get the reply.
     * @param nodeMessage The XML tree to send.
     * @param strDest The destination URL string.
     * @return The reply message (or null if none).
     */
    public BaseMessage sendMessageRequest(BaseMessage messageOut)
    {
        return super.sendMessageRequest(messageOut);    // You never have an (immediate) reply from an email request.
    }
    /**
     * Get the message destination address
     * @param trxMessageHeader
     * @return
     */
    public static final String FAX_PROVIDER = "faxProvider";
    public String getDestination(TrxMessageHeader trxMessageHeader)
    {
        String strDest = super.getDestination(trxMessageHeader);
        String strProvider = (String)trxMessageHeader.get(FAX_PROVIDER);
        if (strProvider != null)
            strDest = strDest + '@' + strProvider;
        return strDest;
    }
    /**
     * Write this outgoing SOAP message into the log.
     * @param msg The message.
     * @urlEndpoint The destination.
     * @return The message log transaction number.
     */
    public String logMessage(String strTrxID, BaseMessage trxMessage, String strMessageInfoType, String strMessageProcessType, String strMessageStatus, String strContactType, String strPerson, String strMessageDescription, int iUserID, int iMessageReferenceID, Message message, String strDest)
    {
        return super.logMessage(strTrxID, trxMessage, strMessageInfoType, strMessageProcessType, strMessageStatus, strContactType, strPerson, strMessageDescription, iUserID, iMessageReferenceID, message, strDest);
    }
}
