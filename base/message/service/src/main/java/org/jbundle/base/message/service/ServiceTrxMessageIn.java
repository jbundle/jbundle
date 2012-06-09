/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.service;

import org.jbundle.base.message.trx.message.external.BaseXmlTrxMessageIn;
import org.jbundle.thin.base.message.BaseMessage;


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
public class ServiceTrxMessageIn extends BaseXmlTrxMessageIn
{

    /**
      * Creates new BaseTrxMessage
     */
    public ServiceTrxMessageIn() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public ServiceTrxMessageIn(BaseMessage message, Object soapMessage)
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
     * Convert this DOM Tree to the JAXB message tree.
     * Don't override this method, override the unmarshalRootElement method.
     * @param nodeBody The source message.
     * @return The JAXB object root.
     */
    public Object convertToMessage()
    {
        return this.getRawData();   // Servicemix already did the conversion and marshalling.
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public String getXML()
    {
        Object message = this.getRawData();
        //if (message != null)
        //    return SOAPMessageTransport.messageToString(message);
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
        return super.getMessageBody(rawData, bReturnCopy);
    }
}
