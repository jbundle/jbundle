/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.soap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.BaseXmlTrxMessageOut;
import org.jbundle.base.message.trx.message.external.SoapTrxMessageIn;
import org.jbundle.base.message.trx.message.external.SoapTrxMessageOut;
import org.jbundle.base.message.trx.processor.BaseMessageProcessor;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.base.util.Utility;
import org.jbundle.model.main.msg.db.MessageControlModel;
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
public class SOAPMessageTransport extends BaseMessageTransport
{
    public static final String SOAP_PACKAGE = "jaxb.packageName";
    public static final String JIBX_PACKAGE_NAME = "jibx.packageName";
    public static final String JIBX_BINDING_NAME = "jibx.bindingName";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String SOAP_ACTION = "SOAPAction";

    /**
     * The SOAP connection.
     */
    private SOAPConnection m_con = null;
    /**
     * The message factory.
     */
    public static MessageFactory fac = null;
    
    static {
        try {
            fac = MessageFactory.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    };

    /**
     * Default constructor.
     */
    public SOAPMessageTransport()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public SOAPMessageTransport(Task messageSenderReceiver)
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
        try {
            if (m_con != null)
                m_con.close();
        } catch (SOAPException ex)  {
            ex.printStackTrace();
        }
    }
    /**
     * Get the message type.
     * @return the type of message this concrete class processes.
     */
    public String getMessageTransportType()
    {
        return MessageTransportModel.SOAP;
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
                externalTrxMessageOut = new SoapTrxMessageIn(message, rawData);
            else
                externalTrxMessageOut = new SoapTrxMessageOut(message, rawData);
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
        if (m_con == null)
        {
            try {
                m_con = SOAPConnectionFactory.newInstance().createConnection();
            } catch(Exception e) {
                Utility.getLogger().warning("Unable to open a SOAPConnection");
                e.printStackTrace();
            }
        }
        String strTrxID = null;
        try {

            // Create a message from the message factory.
            SOAPMessage msg = null; //mf.createMessage();
            
            msg = this.setSOAPBody(msg, messageOut);
            
            // Create an endpoint for the recipient of the message.
            TrxMessageHeader trxMessageHeader = (TrxMessageHeader)messageOut.getMessageHeader();
            String strDest = (String)trxMessageHeader.get(TrxMessageHeader.DESTINATION_PARAM);
            String strMsgDest = (String)trxMessageHeader.get(TrxMessageHeader.DESTINATION_MESSAGE_PARAM);
            if (strMsgDest != null)
                strDest = strDest + strMsgDest;
            strTrxID = (String)trxMessageHeader.get(TrxMessageHeader.LOG_TRX_ID);

            URL urlEndpoint = new URL(strDest);
            // Send the message to the provider using the connection.
            // Create a message from the message factory.
            if (trxMessageHeader.get(SOAPMessage.WRITE_XML_DECLARATION) != null)
                msg.setProperty(SOAPMessage.WRITE_XML_DECLARATION, (String)trxMessageHeader.get(SOAPMessage.WRITE_XML_DECLARATION));    // true or [false]
            if (trxMessageHeader.get(SOAPMessage.CHARACTER_SET_ENCODING) != null)
                msg.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, (String)trxMessageHeader.get(SOAPMessage.WRITE_XML_DECLARATION));    // [utf-8] or utf-16
            if (trxMessageHeader.get(CONTENT_TYPE) != null)
                msg.getMimeHeaders().setHeader(CONTENT_TYPE, (String)trxMessageHeader.get(CONTENT_TYPE)); // "text/xml"
            if (trxMessageHeader.get(SOAP_ACTION) != null)
                msg.getMimeHeaders().setHeader(SOAP_ACTION, (String)trxMessageHeader.get(SOAP_ACTION)); // Not recommended
            
        	//OutputStream out = new ByteArrayOutputStream();
        	//msg.writeTo(out);
        	//out.flush();
        	//Utility.getLogger().info(out.toString());

            strTrxID = this.logMessage(strTrxID, messageOut, MessageInfoTypeModel.REQUEST, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.SENT, null, null);

            SOAPMessage reply = m_con.call(msg, urlEndpoint);
// To test this locally, use the next two lines.
//            MessageReceivingServlet servlet = new MessageReceivingServlet();
//            SOAPMessage reply = servlet.onMessage(msg);

            if (reply != null)
            {
                BaseMessage messageReply = new TreeMessage(null, null);
                new SoapTrxMessageIn(messageReply, reply);
                return messageReply;
            } else {
                System.err.println("No reply");
            }
        } catch(Throwable ex) {
            ex.printStackTrace();
            String strErrorMessage = ex.getMessage();
            this.logMessage(strTrxID, messageOut, MessageInfoTypeModel.REQUEST, MessageTypeModel.MESSAGE_OUT, MessageStatusModel.ERROR, strErrorMessage, null);
            return BaseMessageProcessor.processErrorMessage(this, messageOut, strErrorMessage);
        }            

        return null;
    }
    /**
     * This utility method sticks this message into this soap message's body.
     * @param msg The source message.
     */
    public SOAPMessage setSOAPBody(SOAPMessage msg, BaseMessage message)
    {
        try {
            if (msg == null)
                msg = fac.createMessage();
            // Message creation takes care of creating the SOAPPart - a
            // required part of the message as per the SOAP 1.1
            // specification.
            SOAPPart soappart = msg.getSOAPPart();

            // Retrieve the envelope from the soap part to start building
            // the soap message.
            SOAPEnvelope envelope = soappart.getEnvelope();
            
            // Create a soap header from the envelope.
            //?SOAPHeader header = envelope.getHeader();
            
            // Create a soap body from the envelope.
            SOAPBody body = envelope.getBody();

            DOMResult result = new DOMResult(body);
            if (((BaseXmlTrxMessageOut)message.getExternalMessage()).copyMessageToResult(result))
            {   // Success
            	// Note: For Jabx, I would have to fix the namespace of the message (so don't use JAXB)
                msg.saveChanges();
                return msg;
            }
        } catch(Throwable e) {
            e.printStackTrace();
            Utility.getLogger().warning("Error in constructing or sending message "
            +e.getMessage());
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
        if (!(externalMessage.getExternalMessage() instanceof SoapTrxMessageIn))
            return null;
        SOAPMessage message = (SOAPMessage)((SoapTrxMessageIn)externalMessage.getExternalMessage()).getRawData();
        try {
            // Message creation takes care of creating the SOAPPart - a
            // required part of the message as per the SOAP 1.1
            // specification.
            SOAPPart sp = message.getSOAPPart();
            
            // Retrieve the envelope from the soap part to start building
            // the soap message.
            SOAPEnvelope envelope = sp.getEnvelope();
            
            // Create a soap header from the envelope.
            //?SOAPHeader hdr = envelope.getHeader();
            
            // Get the body
            SOAPBody body = envelope.getBody();
            SOAPElement element = this.getElement(body, null);
            if (element != null)
                return element.getElementName().getLocalName();   // The operation            
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return null;    // No type found.
    }
    /**
     * From this external message, figure out the message type.
     * @externalMessage The external message just received.
     * @return The message type for this kind of message (transport specific).
     */
    public String getMessageVersion(BaseMessage externalMessage)
    {
        String version = null;
        if (!(externalMessage.getExternalMessage() instanceof SoapTrxMessageIn))
            return null;
        SOAPMessage message = (SOAPMessage)((SoapTrxMessageIn)externalMessage.getExternalMessage()).getRawData();
        try {
            // Message creation takes care of creating the SOAPPart - a
            // required part of the message as per the SOAP 1.1
            // specification.
            SOAPPart sp = message.getSOAPPart();
            
            // Retrieve the envelope from the soap part to start building
            // the soap message.
            SOAPEnvelope envelope = sp.getEnvelope();
            
            // Create a soap header from the envelope.
            //?SOAPHeader hdr = envelope.getHeader();
            
            // Get the body
            SOAPBody body = envelope.getBody();
            SOAPElement element = this.getElement(body, null);
            if (element != null)
            {
                QName na = new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
                String attribute = element.getAttributeValue(na);
                if (attribute != null)
                {
                    Record recMessageControl = this.getRecord(MessageControlModel.MESSAGE_CONTROL_FILE);
                    if (recMessageControl == null)
                        recMessageControl = Record.makeRecordFromClassName(MessageControlModel.THICK_CLASS, this);
                    version = ((MessageControlModel)recMessageControl).getVersionFromSchemaLocation(attribute);
                }
            }
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        if (version != null)
            return version;
        return super.getMessageVersion(externalMessage);    // No type found.
    }
    /**
     * Get the element.
     * @param strElementName The element to return (if null, return the first element).
     */
    public SOAPElement getElement(SOAPElement element, String strElementName)
    {
        Iterator<?> iterator = element.getChildElements();
        while (iterator.hasNext())
        {
            javax.xml.soap.Node elMessageType = (javax.xml.soap.Node)iterator.next();
            if (elMessageType instanceof SOAPElement)
            {
                if (strElementName == null)
                    return (SOAPElement)elMessageType;    // The message type
                if (strElementName.equalsIgnoreCase(((SOAPElement)elMessageType).getElementName().getLocalName()))
                    return (SOAPElement)elMessageType;    // The message type
            }
        }
        return null;    // not found
    }
    /**
     * Convert this SOAP message to a text string.
     */
    public static String messageToString(SOAPMessage soapMessage)
    {
        String strTextMessage = null;
        if (soapMessage != null)
        {
            try {

                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                PrintStream os = new PrintStream(ba);

                soapMessage.writeTo(os);

                os.flush();
                ByteArrayInputStream is = new ByteArrayInputStream(ba.toByteArray());
                Reader reader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(reader);
                StringBuffer sb = new StringBuffer();
                String string = null;
                while ((string = br.readLine()) != null)
                {
                    sb.append(string);
                    sb.append('\n');
                }
                strTextMessage = sb.toString();
            } catch (SOAPException ex)  {
                ex.printStackTrace();
            } catch (IOException ex)  {
                ex.printStackTrace();
            }
        }
        return strTextMessage;
    }
    /**
     * A utility method to print this source tree to the given stream.
     */
    public void printSource(Source src, PrintStream out)
    {
        TransformerFactory tFact = TransformerFactory.newInstance();
        try {
            Transformer transformer = tFact.newTransformer();

            Result result = new StreamResult( out );
            transformer.transform(src, result);
        } catch (TransformerConfigurationException ex)  {
            ex.printStackTrace();
        } catch (TransformerException ex)  {
            ex.printStackTrace();
        }
    }
}
