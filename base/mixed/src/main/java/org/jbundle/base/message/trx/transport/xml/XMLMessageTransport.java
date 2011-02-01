package org.jbundle.base.message.trx.transport.xml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.XmlTrxMessageIn;
import org.jbundle.base.message.trx.message.external.XmlTrxMessageOut;
import org.jbundle.base.message.trx.processor.BaseMessageProcessor;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.main.msg.db.MessageInfoType;
import org.jbundle.main.msg.db.MessageStatus;
import org.jbundle.main.msg.db.MessageTransport;
import org.jbundle.main.msg.db.MessageType;
import org.jbundle.model.Task;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.ExternalMessage;
import org.jbundle.thin.base.message.TreeMessage;
import org.jbundle.thin.base.remote.proxy.transport.ServletMessage;


/**
 * This is the base class to process an external message.
 */
public class XMLMessageTransport extends BaseMessageTransport
{
    public static final String SOAP_PACKAGE = "jaxb.packageName";

    /**
     * Default constructor.
     */
    public XMLMessageTransport()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public XMLMessageTransport(Task messageSenderReceiver)
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
        return MessageTransport.XML;
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
            if (MessageType.MESSAGE_IN.equalsIgnoreCase((String)message.get(TrxMessageHeader.MESSAGE_PROCESS_TYPE)))
                externalTrxMessageOut = new XmlTrxMessageIn(message, rawData);
            else
                externalTrxMessageOut = new XmlTrxMessageOut(message, rawData);
        }
        return externalTrxMessageOut;
    }
    /**
     * Send the message and (optionally) get the reply.
     * @param nodeMessage The XML tree to send.
     * @param strDest The destination URL string.
     * @return The reply message (or null if none).
     */
    public BaseMessage sendMessageRequest(BaseMessage messageOut)
    {
        String strTrxID = null;
        try {
            // Create a message from the message factory.
            String strXmlMessageOut = messageOut.getExternalMessage().toString();
            // Create an endpoint for the recipient of the message.
            TrxMessageHeader trxMessageHeader = (TrxMessageHeader)messageOut.getMessageHeader();
            String strDest = (String)trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM);
            String strMsgDest = (String)trxMessageHeader.get(TrxMessageHeader.DESTINATION_MESSAGE_PARAM);
            if (strMsgDest != null)
                strDest = strDest + strMsgDest;
            strTrxID = (String)trxMessageHeader.get(TrxMessageHeader.LOG_TRX_ID);

            URL url = new URL(strDest);
            Properties properties = new Properties();
            properties.setProperty("xml", strXmlMessageOut);
            // Send the message to the provider using the connection.
            // Create a message from the message factory.
//x            msg.setProperty(SOAPMessage.WRITE_XML_DECLARATION, DBConstants.TRUE);
//x            msg.getMimeHeaders().setHeader("Content-Type", "text/xml");

            ServletMessage servlet = new ServletMessage(url);
            InputStream in = servlet.sendMessage(properties);

            this.logMessage(strTrxID, messageOut, MessageInfoType.REQUEST, MessageType.MESSAGE_OUT, MessageStatus.SENT, null, null);
            
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader buffReader = new BufferedReader(reader);
            StringBuffer sbReply = new StringBuffer();
            while (true)
            {
                String str = buffReader.readLine();
                if ((str == null) || (str.length() == 0))
                    break;
                if (sbReply.length() > 0)
                    sbReply.append("\n");
                sbReply.append(str);
            }
            if (sbReply.length() > 0)
            {
                BaseMessage messageReply = new TreeMessage(null, null);
                new XmlTrxMessageIn(messageReply, sbReply.toString());
                return messageReply;
            } else {
                System.err.println("No reply");
            }
        } catch(Throwable ex) {
            ex.printStackTrace();
            String strErrorMessage = ex.getMessage();
            this.logMessage(strTrxID, messageOut, MessageInfoType.REQUEST, MessageType.MESSAGE_OUT, MessageStatus.ERROR, strErrorMessage, null);
            return BaseMessageProcessor.processErrorMessage(this, messageOut, strErrorMessage);
        }            

        return null;
    }
    /**
     * From this external message, figure out the message type.
     * @externalMessage The external message just received.
     * @return The message type for this kind of message (transport specific).
     */
    public String getMessageCode(BaseMessage externalMessage)
    {
        if (!(externalMessage.getExternalMessage() instanceof XmlTrxMessageIn))
            return null;
        if (!(externalMessage.getExternalMessage().getRawData() instanceof String))
            return null;
        String strXML = (String)externalMessage.getExternalMessage().getRawData();
        int beginIndex = 0;
        while (beginIndex != -1)
        {
            beginIndex = strXML.indexOf('<', beginIndex);
            if (beginIndex == -1)
                return null;    // No type found.
            beginIndex++;
            if (strXML.charAt(beginIndex) == '?')
                continue;   // XML Directive
            int iEnd = strXML.indexOf(' ', beginIndex);
            int iClose = strXML.indexOf('>', beginIndex);
            if ((iEnd != -1) && (iClose != -1))
            	iEnd = Math.min(iEnd, iClose);
            else if (iEnd == -1)
                iEnd = iClose;
            if (iEnd != -1)
            {
                String code = strXML.substring(beginIndex, iEnd);
                if (code.indexOf(':') != -1)
                	code = code.substring(code.indexOf(':') + 1);	// Remove namespace prefix
                return code;
            }
            break;  // Not found
        }
        return null;    // No type found.
    }
}
