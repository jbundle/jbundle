/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external;

import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.jbundle.base.message.trx.transport.soap.SOAPMessageTransport;
import org.jbundle.base.model.Utility;
import org.jbundle.thin.base.message.BaseMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * This is the base class for a transaction which is sent externally.
 * The two main sub-classes of this class are InternalTrxMessage and ExternalTrxMessage.
 * An InternalTrxMessage is the data I create internally to send to the destination. It
 * usually contains all the relative information needed to send to the destination.
 * An ExternalTrxMessage is the message converted to a format that the receiver can
 * understand (such as ebXML).
 * @author  don
 * @version 
 */
public class SoapTrxMessageIn extends BaseXmlTrxMessageIn
{

    /**
      * Creates new BaseTrxMessage
     */
    public SoapTrxMessageIn() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public SoapTrxMessageIn(BaseMessage message, Object soapMessage)
    {
        this();
        this.init(message, soapMessage);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(BaseMessage message, Object soapMessage)
    {
        super.init(message, soapMessage);
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public String getXML()
    {
        SOAPMessage message = (SOAPMessage)this.getRawData();
        if (message != null)
            return SOAPMessageTransport.messageToString(message);
        return super.getXML();
    }
    /**
     * Get the SOAP message body as a DOM node.
     * @param message the SOAP message.
     * @param bReturnCopy Return a copy of the message node, instead of the actual node.
     * @return The DOM node containing the message body.
     */
    public org.w3c.dom.Node getMessageBody(Object rawData, boolean bReturnCopy)
    {
        SOAPMessage message = (SOAPMessage)rawData;
        try {
            SOAPPart sp = message.getSOAPPart();

            if (bReturnCopy)
            {
                // We need to do the following steps to add the document to this soap body:
                // 1. Transform this SOAPPart to DOM
                // 2. Add this document to the SOAPBody.
                // 3. Put it back in the SOAP object
    
                // 1. Transform this SOAPPart to DOM
                TransformerFactory tFact = TransformerFactory.newInstance();
                Transformer transformer = tFact.newTransformer();
                Source src = sp.getContent();
    
                DocumentBuilder db = Utility.getDocumentBuilder();
                DocumentFragment docFrag = null;
                synchronized (db)
                {
                    Document doc = this.getScratchDocument(db);
                    docFrag = doc.createDocumentFragment();
                    Result result = new DOMResult(docFrag);
                    transformer.transform(src, result);
                }
    
                // 2. Add this document to the SOAPBody.
                org.w3c.dom.Element el = (org.w3c.dom.Element)docFrag.getFirstChild();
    
                NodeList nodeList = null;
    
                nodeList = el.getChildNodes();
                org.w3c.dom.Element elBody = null;
                for (int i = 0; i < nodeList.getLength(); i++)
                {
                    if ("Body".equalsIgnoreCase(nodeList.item(i).getLocalName()))
                    {
                        elBody = (org.w3c.dom.Element)nodeList.item(i);
                        break;
                    }
                }
                org.w3c.dom.Element elDoc = null;
                nodeList = elBody.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++)
                {
                    String strName = nodeList.item(i).getLocalName();
                    if ((strName != null) && (strName.length() > 0))
                    {       // First item in the body is the OTA message
                        elDoc = (org.w3c.dom.Element)nodeList.item(i);
                        break;
                    }
                }
    
                return elDoc;
            }
            else
            {
                // Retrieve the envelope from the soap part to start building
                // the soap message.
                SOAPEnvelope envelope = sp.getEnvelope();
                
                // Create a soap header from the envelope.
                //?SOAPHeader header = envelope.getHeader();
                
                // Create a soap body from the envelope.
                SOAPBody body = envelope.getBody();

                Iterator<?> iterator = body.getChildElements();
                while (iterator.hasNext())
                {
                    Object nextNode = iterator.next();
                    if (!(nextNode instanceof Element))
                        continue;
                    Element node = (Element)nextNode;
                
                    if (node instanceof SOAPElement)    // The one and only
                        return node;
                }
            }
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
