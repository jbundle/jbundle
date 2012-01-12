/*
 *  @(#)HotelAvailRQScreen.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.screen;

import java.io.PrintWriter;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.ExternalMapTrxMessageIn;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.Screen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.Debug;
import org.jbundle.base.util.Utility;
import org.jbundle.model.main.msg.db.MessageInfoTypeModel;
import org.jbundle.model.main.msg.db.MessageLogModel;
import org.jbundle.model.main.msg.db.MessageProcessInfoModel;
import org.jbundle.model.main.msg.db.MessageTypeModel;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.TreeMessage;


/**
 *  MessageScreen - Enter the message information for this transaction.
 */
public class MessageScreen extends Screen
{
    /**
     *
     */
    public BaseMessageTransport m_transport = null;
    
    /**
     * Default constructor.
     */
    public MessageScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?.
     */
    public MessageScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        // By default, all fields are disabled (enable the target fields).
        for (int iFieldSeq = 0; iFieldSeq < this.getSFieldCount(); iFieldSeq++)
        {
            ScreenField sField = this.getSField(iFieldSeq);
            if (!(sField instanceof ToolScreen))
                sField.setEnabled(false);
        }
        // 2. The first thing to do is to see if this is a message reply or just a screen display
        BaseMessage message = this.getMessage();
        if (message != null)
        {
            this.moveMessageParamsToScreen(message);
            if (this.isReplyMessage(message))
            {
                // If this is a reply message, you may want to display this screen differently.
            }
        }
    }
    /**
     * Move the message params to this screen before I put the screen up.
     * Override this.
     */
    public void moveMessageParamsToScreen(BaseMessage message)
    {
    }
    /**
     * Return true if this post for the screen is a reply message.
     * You should override this to check for the critical information that this screen should supply.
     */
    public boolean isReplyMessage(BaseMessage message)
    {
// Should look something like this:
//        String strxxx = (String)this.getServletTask().getProperties().get("xxx");
        return false;
    }
    /**
     * Process the "Add" toolbar command.
     * @return  true    If command was handled
     */
    public boolean onAdd()
    {
        this.clearStatusText();
        this.processThisMessage();
        return true;
    }
    /*
    * This is the application code for handling the message. Once the
    * message is received the application can retrieve the soap part, the
    * attachment part if there are any, or any other information from the
    * message.
    */
    public void processThisMessage()
    {
        Utility.getLogger().info("On message called in receiving process");
        try {
            BaseMessage messageIn = this.getMessage();
            if (messageIn != null)
            {
                BaseMessage messageReply = this.createReplyMessage(messageIn);
                this.moveScreenParamsToMessage(messageReply);
                // Step 2 - Get the body part of the message
                this.getTransport().setupReplyMessage(messageReply, messageIn, MessageInfoTypeModel.REPLY, MessageTypeModel.MESSAGE_IN);
            
                this.getTransport().processIncomingMessage(messageReply, null);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            Debug.print(ex, "Error in processing or replying to a message");
        }
    }
    /**
     * Given this message in, create the reply message.
     * @param messageIn
     * @return
     */
    public BaseMessage createReplyMessage(BaseMessage messageIn)
    {
//            ProductRequest productRequest = (ProductRequest)messageIn.getMessageDataDesc(null);
        BaseMessage replyMessage = (BaseMessage)this.getMessageProcessInfo().createReplyMessage(messageIn);

//        BaseProductResponse responseMessage = (BaseProductResponse)replyMessage.getMessageDataDesc(null);
//        responseMessage.moveRequestInfoToReply(productRequest);
        if (replyMessage == null)
            replyMessage = new TreeMessage(null, null);
        if (replyMessage.getExternalMessage() == null)
            new ExternalMapTrxMessageIn(replyMessage, null);
        return replyMessage;    // Override this!
    }
    /**
     * GetMessageProcessInfo Method.
     */
    public MessageProcessInfoModel getMessageProcessInfo()
    {
        if (this.getRecord(MessageProcessInfoModel.MESSAGE_PROCESS_INFO_FILE) == null)
            Record.makeRecordFromClassName(MessageProcessInfoModel.MESSAGE_PROCESS_INFO_FILE, this);
        return (MessageProcessInfoModel)this.getRecord(MessageProcessInfoModel.MESSAGE_PROCESS_INFO_FILE);
    }
    /**
     * Move to entered fields to the return message.
     * @param message TODO
     */
    public void moveScreenParamsToMessage(BaseMessage message)
    {
        // Override this
    }
    /**
     * Display this screen in html input format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        this.addHiddenParam(out, TrxMessageHeader.LOG_TRX_ID, this.getProperty(TrxMessageHeader.LOG_TRX_ID));
        return super.printData(out, iPrintOptions);   // Don't print
    }
    /**
     * Get the path to the target servlet.
     * @return the servlet path.
     */
    public String getServletPath(String strServletParam)
    {
        return "message";
    }
    /**
     * Add this hidden param to the output stream.
     * @param out The html output stream.
     * @param strParam The parameter.
     * @param strValue The param's value.
     */
    public void addHiddenParam(PrintWriter out, String strParam, String strValue)
    {
        out.println("<input type=\"hidden\" name=\"" + strParam + "\" value=\"" + strValue + "\">");
    }
    /**
     * This utility method re-creates the source(sent) message from the message log.
     * @return The sent message
     */
    public BaseMessage getMessage()
    {
        String strTrxID = this.getProperty(TrxMessageHeader.LOG_TRX_ID);
        if (m_strTrxID != null)
            if (m_strTrxID.equalsIgnoreCase(strTrxID))
                return m_message;   // Cached message is what you are looking for.
        m_strTrxID = null;
        m_message = null;
        if (strTrxID != null)
        {   // Good, they are referencing a transaction (access the transaction properties).
            MessageLogModel recMessageLog = (MessageLogModel)this.getRecord(MessageLogModel.MESSAGE_LOG_FILE);
            if (recMessageLog == null)
                recMessageLog = (MessageLogModel)Record.makeRecordFromClassName(MessageLogModel.THICK_CLASS, this);
            if (recMessageLog != null)
                m_message = (BaseMessage)recMessageLog.createMessage(strTrxID);
        }
        return m_message;
    }
    protected BaseMessage m_message = null;
    protected String m_strTrxID = null;
    /**
     * Get the transport for an incomming screen message.
     * @return The screen transport.
     */
    public BaseMessageTransport getTransport()
    {
        if (m_transport == null)
            m_transport = new ScreenMessageTransport(this.getTask());
        return m_transport;
    }
}
