/*
 * Task to check the inbox for incomming automated requests.

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.email;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SubjectTerm;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.PasswordPropertiesField;
import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.EMailTrxMessageIn;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.main.msg.db.MessageInfoTypeModel;
import org.jbundle.model.main.msg.db.MessageLogModel;
import org.jbundle.model.main.msg.db.MessageTransportModel;
import org.jbundle.model.main.msg.db.MessageTypeModel;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.TreeMessage;


/**
 * Servlet that receives SOAP messages.
 */
public class MessageReceivingPopClientProcess extends BaseProcess
{
    public static final String STORE_TYPE = "pop3";
    public static final String POP3_HOST = "pop3host";
    public static final String POP3_USERNAME = "pop3username";
    public static final String POP3_PASSWORD = "pop3password";
    public static final String POP3_SSL = "mail.pop3.ssl.enable";
    public static final String POP3_PORT = "mail.pop3.port";
    public static final String POP3_INBOX = "inbox";
    public static final String POP3_INTERVAL = "interval";
    public static final String DEFAULT_INBOX = "INBOX";
    public static final String DEFAULT_INTERVAL = "6000";   // Every minute
    public static final String DEFAULT_PORT = "110";   // Every minute
    
    public static final String REPLY_STRING = "request";
    public static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    
    public static final String PATTERN = "pattern";
    
    /**
     * Initialization.
     */
    public MessageReceivingPopClientProcess()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public MessageReceivingPopClientProcess(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
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
     * Run the code in this process.
     */
    public void run()
    {
        Folder folder = this.getInboxFolder();
        
        if (folder != null)
            this.processMail(folder);

        try {
            folder.close(true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    /**
     * Open the poop3 inbox.
     * @return The INBOX folder.
     */
    public Folder getInboxFolder()
    {
        String strMessageType = MessageTransportModel.EMAIL;
        String strClassName = null;
        
        Record recMessageTransport = this.getRecord(MessageTransportModel.MESSAGE_TRANSPORT_FILE);
        if (recMessageTransport == null)
            recMessageTransport = Record.makeRecordFromClassName(MessageTransportModel.THICK_CLASS, this);
        recMessageTransport.setKeyArea(MessageTransportModel.CODE_KEY);
        recMessageTransport.getField(MessageTransportModel.CODE).setString(strMessageType);
        Map<String,Object> properties = null;
        try {
            if (recMessageTransport.seek(null))
            {
                PropertiesField fldProperty = (PropertiesField)recMessageTransport.getField(MessageTransportModel.PROPERTIES);
                strClassName = fldProperty.getProperty(MessageTransportModel.TRANSPORT_CLASS_NAME_PARAM);
                properties = fldProperty.loadProperties();
                this.setProperties(properties);
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }

        if (strClassName == null)
            strClassName = EmailMessageTransport.class.getName();

        String strHost = this.getProperty(POP3_HOST);
        int iPort = 110;
        if (this.getProperty(POP3_PORT) != null)
            iPort = Integer.parseInt(this.getProperty(POP3_PORT));
        String strUsername = this.getProperty(POP3_USERNAME);
        String strPassword = this.getProperty(POP3_PASSWORD);
        strPassword = PasswordPropertiesField.decrypt(strPassword);
        String strInbox = this.getProperty(POP3_INBOX);
        if (strInbox == null)
            strInbox = DEFAULT_INBOX;
        String strInterval = this.getProperty(POP3_INTERVAL);
        if (strInterval == null)
            strInterval = DEFAULT_INTERVAL;
        try
        {
            Properties props = Utility.mapToProperties(properties);//System.getProperties();
            if ((props.getProperty(POP3_SSL) != null)
                    && (props.getProperty(POP3_SSL).equalsIgnoreCase(DBConstants.TRUE)))
            {
                props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
                props.setProperty("mail.pop3.socketFactory.fallback", "false");
                 if (this.getProperty(POP3_PORT) != null)
                     if (this.getProperty("mail.pop3.socketFactory.port") == null)
                         props.setProperty("mail.pop3.socketFactory.port", this.getProperty(POP3_PORT));
            }
            
            // Get a Session object
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(true);
            
            // Get a Store object
            URLName url = new URLName(STORE_TYPE, strHost, iPort, "", strUsername, strPassword);

//          Store store = session.getStore("imap");
            Store store = session.getStore(url);
            // Connect
            store.connect();
           
            // Open a Folder
            Folder folder = store.getFolder(strInbox);
            if (folder == null || !folder.exists()) 
            {
                Utility.getLogger().warning("Invalid folder");
                System.exit(1); 
            }
            folder.open(Folder.READ_WRITE);

            // Check mail once in "freq" MILLIseconds
//?            int freq = Integer.parseInt(strInterval);
            return folder;
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    /**
     * Go through all the mail and process the messages.
     * @param folder The inbox folder.
     */
    public void processMail(Folder folder)
    {
        try {
            int iCount = folder.getMessageCount();
            if (iCount == 0)
                return;

            String pattern = REPLY_STRING;
            Message[] messages = null;
            if (this.getProperty(PATTERN) == null)
            {
                messages = folder.getMessages();
            }
            else
            {
                pattern = this.getProperty(PATTERN);
                SubjectTerm st = new SubjectTerm(pattern);
                // Get some message references
                messages = folder.search(st);
            }
            
            for (int iMsgnum = 0; iMsgnum < messages.length; iMsgnum++)
            {
                Message message = messages[iMsgnum];
                String strSubject = message.getSubject();
                if (strSubject != null)
                    if ((pattern == null) || (strSubject.indexOf(pattern) != -1))
                        this.processThisMessage(message);
                message.setFlag(Flags.Flag.SEEN, true);  // Archives the message (so I don't process it again)
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /*
    * This is the application code for handling the message. Once the
    * message is received the application can retrieve the soap part, the
    * attachment part if there are any, or any other information from the
    * message.
    * @param message The incoming message to process.
    */
    public String processThisMessage(Message message)
    {
        String strContent = this.getContentString(message);

        Utility.getLogger().info("Processing an incoming email message");
        try {
            EmailMessageTransport msgTransport = new EmailMessageTransport(this.getTask());

            // Step 2 - Get the body part of the message
            String strFrom = DBConstants.BLANK;
            Address[] rgFrom = message.getFrom();
            if ((rgFrom != null) && (rgFrom.length > 0))
                strFrom = rgFrom[0].toString();
            String strReplyTo = DBConstants.BLANK;
            Address[] rgReplyTo = message.getReplyTo();
            if ((rgReplyTo != null) && (rgReplyTo.length > 0))
                strReplyTo = rgReplyTo[0].toString();
            String strSubject = message.getSubject();

            BaseMessage messageReply = new TreeMessage(null, null);
            new EMailTrxMessageIn(messageReply, strContent);
            String strTrxID = this.getTrxIDFromSubject(strSubject);
            if (strTrxID != null)
            {   // Good, they are referencing a transaction (access the transaction properties).
                MessageLogModel recMessageLog = (MessageLogModel)this.getRecord(MessageLogModel.MESSAGE_LOG_FILE);
                if (recMessageLog == null)
                    recMessageLog = (MessageLogModel)Record.makeRecordFromClassName(MessageLogModel.THICK_CLASS, this);
                BaseMessage messageIn = (BaseMessage)recMessageLog.createMessage(strTrxID);
                msgTransport.setupReplyMessage(messageReply, messageIn, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_IN);
            }
            else
            {   // Error or Incoming request by email?
            }

            TrxMessageHeader messageReplyHeader = (TrxMessageHeader)messageReply.getMessageHeader();
            if (messageReplyHeader != null)
            {
                messageReplyHeader.put(TrxMessageHeader.SOURCE_PARAM, strFrom);
                messageReplyHeader.put(TrxMessageHeader.DESTINATION_PARAM, strReplyTo);
                if (strSubject != null)
                    messageReplyHeader.put(EmailMessageTransport.SUBJECT_PARAM, strSubject);
    
                msgTransport.processIncomingMessage(messageReply, null);
            }

            return null;
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Error in processing or replying to a message");
            return null;
        }
    }
    /**
     * Utility to extract the trx ID from the subject line.
     * Re: Subject (123)
     */
    public String getTrxIDFromSubject(String strSubject)
    {
        String strTrxID = null;
        if (strSubject != null)
        {
            int iLastParen = strSubject.lastIndexOf(')');
            int iStartParen = strSubject.lastIndexOf('(');
            if (iLastParen != -1)
                if (iStartParen != -1)
                    if (iLastParen > iStartParen)
                    {
                        strTrxID = strSubject.substring(iStartParen + 1, iLastParen);
                        if (!Utility.isNumeric(strTrxID))
                            strTrxID = null;    // Must be numeric
                    }
        }
        return strTrxID;
    }
    /**
     * Get the message content as a string.
     * @param message The message.
     * @return The message content.
     */
    public String getContentString(Message message)
    {
        try {
            String strContentType = message.getContentType();
            Object content = message.getContent();
            if (content instanceof MimeMultipart)
            {
                for (int index = 0; ; index++)
                {
                    BodyPart bodyPart = ((javax.mail.internet.MimeMultipart)content).getBodyPart(index);
                    Object contents = bodyPart.getContent();
                    if (contents != null)
                        return contents.toString();
                }
            }
            return message.getContent().toString();     // pend(don) FIX THIS!
        } catch (IOException ex)    {
            ex.printStackTrace();
        } catch (MessagingException ex)    {
            ex.printStackTrace();
        }
        return null;
    }
}
