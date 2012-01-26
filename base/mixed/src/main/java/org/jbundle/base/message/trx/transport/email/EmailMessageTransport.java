/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.email;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jbundle.base.field.PasswordPropertiesField;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.EMailTrxMessageIn;
import org.jbundle.base.message.trx.message.external.EMailTrxMessageOut;
import org.jbundle.base.message.trx.message.internal.ManualMessage;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.model.Task;
import org.jbundle.model.main.msg.db.MessageInfoTypeModel;
import org.jbundle.model.main.msg.db.MessageStatusModel;
import org.jbundle.model.main.msg.db.MessageTransportModel;
import org.jbundle.model.main.msg.db.MessageTypeModel;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.ExternalMessage;


/**
 * This is the base class to process an external message.
 */
public class EmailMessageTransport extends BaseMessageTransport
{
    protected boolean DEBUG = true;
    /**
     * The smtp host.
     */
    public static final String HOST_PARAM = "host";
    /**
     * Message subject.
     */
    public static final String SUBJECT_PARAM = "subject";
    /**
     * The param for the smtp api.
     */
    public static final String SMTP_HOST = "mail.smtp.host";
    public static final String SMTP_AUTH = "mail.smtp.auth";
    public static final String SMTP_TLS = "mail.smtp.starttls.enable";
    public static final String SEND_TRANSPORT = "sendTransport";
    public static final String SMTP_USERNAME = "smtp.username";
    public static final String SMTP_PASSWORD = "smtp.password";
    /**
     *
     */
    public static final String CONTENT_TYPE_PARAM = "content-type";

    /**
     * Default constructor.
     */
    public EmailMessageTransport()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public EmailMessageTransport(Task messageSenderReceiver)
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
        return MessageTransportModel.EMAIL;
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
                externalTrxMessage = new EMailTrxMessageIn(message, (String)rawData);
            else
                externalTrxMessage = new EMailTrxMessageOut(message, rawData);
        }
        return externalTrxMessage;
    }
    /**
     * Is this message a reply (or a request).
     * @param externalMessage The incomming message.
     * @return MessageType.MESSAGE_IN or MessageType.REPLY_IN If this is a reply.
     */
    public String getMessageInfoType(BaseMessage externalMessage)
    {
        String strSubject = (String)externalMessage.getMessageHeader().get(EmailMessageTransport.SUBJECT_PARAM);
        if (strSubject != null)
            if (strSubject.toUpperCase().startsWith("RE:"))
                return MessageInfoTypeModel.REPLY;   // This is (most likely) a reply to my message.
        return super.getMessageInfoType(externalMessage);
    }
    /**
     * Is this message a reply (or a request).
     * @param externalMessage The incoming message.
     * @return MessageType.MESSAGE_IN or MessageType.REPLY_IN If this is a reply.
     */
    public String getMessageProcessType(BaseMessage externalMessage)
    {
        String strSubject = (String)externalMessage.getMessageHeader().get(EmailMessageTransport.SUBJECT_PARAM);
        if (strSubject != null)
            if (strSubject.toUpperCase().startsWith("RE:"))
                return MessageTypeModel.MESSAGE_IN;   // This is (most likely) a reply to my message.
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
        // create some properties and get the default Session
        TrxMessageHeader trxMessageHeader = (TrxMessageHeader)messageOut.getMessageHeader();

        String strFrom = (String)trxMessageHeader.get(TrxMessageHeader.REPLY_TO_PARAM);
        if (strFrom == null)
            strFrom = this.getProperty((TrxMessageHeader)messageOut.getMessageHeader(), TrxMessageHeader.REPLY_TO_PARAM); // See if there is an email default.
        String strSubject = (String)trxMessageHeader.get(SUBJECT_PARAM);
        String strTrxID = (String)trxMessageHeader.get(TrxMessageHeader.LOG_TRX_ID);
        if (strSubject == null)
            strSubject = "Message"; // Lame
        if (strTrxID != null)
            strSubject = strSubject + " (" + strTrxID + ")";

        Properties props = new Properties();
        String strSendBy = (String)trxMessageHeader.get(SEND_TRANSPORT);
        if (strSendBy == null)
            strSendBy = "smtp";
        String strHost = (String)trxMessageHeader.get(SMTP_HOST);
        if (strHost == null)
            strHost = (String)trxMessageHeader.get(HOST_PARAM);
        props.put(SMTP_HOST, strHost);
        String strAuth = (String)trxMessageHeader.get(SMTP_AUTH);   // "true"?
        if (strAuth != null)
            props.put(SMTP_AUTH, strAuth);
        if ((String)trxMessageHeader.get(SMTP_TLS)!= null)   // "true"?
            props.put(SMTP_TLS, (String)trxMessageHeader.get(SMTP_TLS));
        if ((String)trxMessageHeader.get("mail.smtp.socketFactory.port")!= null)
            props.put("mail.smtp.socketFactory.port", (String)trxMessageHeader.get("mail.smtp.socketFactory.port"));
        if ((String)trxMessageHeader.get("mail.smtp.socketFactory.class")!= null)
            props.put("mail.smtp.socketFactory.class", (String)trxMessageHeader.get("mail.smtp.socketFactory.class"));
        if ((String)trxMessageHeader.get("mail.smtp.socketFactory.fallback")!= null)
            props.put("mail.smtp.socketFactory.fallback", (String)trxMessageHeader.get("mail.smtp.socketFactory.fallback"));
        Session session = Session.getDefaultInstance(props);
        Properties sessionProp = session.getProperties();
        if (!sessionProp.equals(props))
            session = Session.getInstance(props);
        session.setDebug(DEBUG);
        
        String username = (String)trxMessageHeader.get(SMTP_USERNAME);
        if (username == null)
            if (DBConstants.TRUE.equalsIgnoreCase(strAuth))
                username = strFrom;
        String password = (String)trxMessageHeader.get(SMTP_PASSWORD);
        password =  PasswordPropertiesField.decrypt(password);

        Transport transport = null;
      
        String strDest = this.getDestination(trxMessageHeader);
        MimeMessage msg = new MimeMessage(session);
        try
        {
            InternetAddress sender = new InternetAddress(strFrom);
            InternetAddress[] address = {new InternetAddress(strDest)};

            msg.setFrom(sender);
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(strSubject);
            msg.setSentDate(new Date());
            
            String msgHtml = (String)messageOut.get(ManualMessage.MESSAGE_PARAM);  // The physical message
            if (msgHtml == null)
                msgHtml = (String)messageOut.getExternalMessage().getRawData();

            if (msgHtml == null)
                msgHtml = "";
            // add the Multipart to the message
            String strContentType = (String)trxMessageHeader.get(CONTENT_TYPE_PARAM);
            if (strContentType == null)
                strContentType = "text/html";
            msg.setContent(msgHtml, strContentType);
           
            msg.saveChanges();
             
            transport = session.getTransport(strSendBy);
            //         if (DBConstants.TRUE.equalsIgnoreCase(strAuth))
            transport.connect(strHost, username, password);
            //xaddress = msg.getAllRecipients(); //Don't do this - bug in javax.mail
            transport.sendMessage(msg, address);
            // send the message
            this.logMessage(strTrxID, messageOut, MessageInfoTypeModel.REQUEST, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.SENT, null, null, null, -1, -1, msg, strDest);
        } catch (MessagingException mex) {
            mex.printStackTrace();
            String strError = mex.getMessage();
            Exception ex = null;
            if ((ex = mex.getNextException()) != null)
            {
                strError = strError + ", " + ex.getMessage();
                ex.printStackTrace();
            }
            this.logMessage(strTrxID, messageOut, MessageInfoTypeModel.REQUEST, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.ERROR, null, null, strError, -1, -1, msg, strDest);
        } finally {
            try
            {
                transport.close();
            }
            catch (Exception e)
            {}
        }
        return null;    // You never have an (immediate) reply from an email request.
    }
    /**
     * Get the message destination address
     * @param trxMessageHeader
     * @return
     */
    public String getDestination(TrxMessageHeader trxMessageHeader)
    {
        String strDest = (String)trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM);
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
        Utility.getLogger().info("Sending message to URL: " + strDest);
        String strMessageFrom = null;
        String strMessageTo = null;
        String strContentType = null;
        String strContent = null;
        if (message != null)
        {
            try {
                Object objContent = message.getContent();
                strContentType = message.getContentType();
                Address[] rgstrMessageFrom = message.getFrom();
                if (rgstrMessageFrom != null) if (rgstrMessageFrom.length > 0)
                    strMessageFrom = rgstrMessageFrom[0].toString();
                Address[] rgstrMessageTo = message.getAllRecipients();
                if (rgstrMessageTo != null) if (rgstrMessageTo.length > 0)
                    strMessageTo = rgstrMessageTo[0].toString();
                if ((strMessageDescription == null) || (strMessageDescription.length() == 0))
                    strMessageDescription = message.getSubject();
                strContent = objContent.toString();
            } catch (IOException ex)    {
                ex.printStackTrace();
            } catch (MessagingException ex)    {
                ex.printStackTrace();
            }
        }
        // Be sure to call super!
        return super.logMessage(strTrxID, trxMessage, strMessageInfoType, strMessageProcessType, strMessageStatus, strMessageDescription, strContentType);
    }
}
