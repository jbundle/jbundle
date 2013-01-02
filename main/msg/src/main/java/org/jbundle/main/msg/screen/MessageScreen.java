/**
 * @(#)MessageScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.screen;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.model.main.msg.db.*;
import java.io.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.base.message.trx.message.external.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.base.message.trx.transport.*;

/**
 *  MessageScreen - The special screen for responding to messages..
 */
public class MessageScreen extends Screen
{
    protected BaseMessage m_message = null;
    protected String m_strTrxID = null;
    protected BaseMessageTransport m_transport = null;
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
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties Addition properties to pass to the screen.
     */
    public MessageScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        m_message = null;
        m_transport = null;
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Get the screen display title.
     */
    public String getTitle()
    {
        return "The special screen for responding to messages.";
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
     * Move the original(sent) message params to this screen.
     */
    public void moveMessageParamsToScreen(BaseMessage message)
    {
        // Override
    }
    /**
     * IsReplyMessage Method.
     */
    public boolean isReplyMessage(BaseMessage message)
    {
        // Should look something like this:
        //        String strxxx = (String)this.getServletTask().getProperties().get("xxx");
        return false;
    }
    /**
     * Process the "Add" toolbar command.
     * @return  true    If command was handled.
     */
    public boolean onAdd()
    {
        this.clearStatusText();
        this.processThisMessage();
        return true;
    }
    /**
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
     * @param messageIn The incomming message
     * @return the (empty) reply message.
     */
    public BaseMessage createReplyMessage(BaseMessage messageIn)
    {
        //ProductRequest productRequest = (ProductRequest)messageIn.getMessageDataDesc(null);
        BaseMessage replyMessage = (BaseMessage)this.getMessageProcessInfo().createReplyMessage(messageIn);
        
        //BaseProductResponse responseMessage = (BaseProductResponse)replyMessage.getMessageDataDesc(null);
        //responseMessage.moveRequestInfoToReply(productRequest);
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
     */
    public void moveScreenParamsToMessage(BaseMessage message)
    {
        // Override this
    }
    /**
     * PrintData Method.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        this.addHiddenParam(out, TrxMessageHeader.LOG_TRX_ID, this.getProperty(TrxMessageHeader.LOG_TRX_ID));
        return super.printData(out, iPrintOptions);   // Don't print
    }
    /**
     * Get the path to the target servlet.
     * @param The servlet type (regular html or xhtml)
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
     * @return The sent message.
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
    /**
     * Get the transport for an incomming screen message.
     * \@return The screen transport.
     */
    public BaseMessageTransport getTransport()
    {
        if (m_transport == null)
            m_transport = new ScreenMessageTransport(this.getTask());
        return m_transport;
    }

}
