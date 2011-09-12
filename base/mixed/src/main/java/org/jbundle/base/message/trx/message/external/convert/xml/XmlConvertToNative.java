/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external.convert.xml;

import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.message.trx.message.external.ExternalTrxMessageOut;
import org.jbundle.base.message.trx.message.external.convert.BaseXmlConvertToNative;
import org.jbundle.thin.base.message.BaseMessage;
import org.w3c.dom.Node;


/**
 * Convert the message to native XML.
 * (No marshalling required)
 * @author  don
 * @version 
 */
public class XmlConvertToNative extends BaseXmlConvertToNative
{

    /**
      * Creates new BaseTrxMessage
     */
    public XmlConvertToNative() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public XmlConvertToNative(ExternalTrxMessageOut message)
    {
        this();
        this.init(message);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(ExternalTrxMessageOut message)
    {
        super.init(message);
    }
    /**
     * Convert this source message to the ECXML format.
     * Typically you do not override this method, you override the getTransformer method
     * to supply a XSLT document to do the conversion.
     * @param recordOwner The record owner
     * @param source The source XML document.
     * @return The XML tree that conforms to the ECXML format.
     */
    public Object convertInternalToMarshallableObject(RecordOwner recordOwner)
    {
        return this.getXML();
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @return The dom tree.
     */
    public String getXML()
    {
        if (m_message != null)
        {
        	BaseMessage message = m_message.getMessage();
        	return message.getXML(false);
        }
        return super.getXML();	// Null
    }
    /**
     * Convert this tree to a DOM object.
     * Currently this is lame because I convert the tree to text, then to DOM.
     * In the future, jaxb will be able to convert directly.
     * @return The dom tree.
     */
    public Node getDOM()
    {
        if (m_message != null)
        {
        	BaseMessage message = m_message.getMessage();
        	return message.getDOM();
        }
        return super.getDOM();	// Null
    }
    /**
     * Copy this XML structure to this result target.
     * @param resultTarget The target to copy to.
     * @return True if successful.
     */
    public boolean copyMessageToResult( javax.xml.transform.Result resultTarget)
    {
        return super.copyMessageToResult(resultTarget);        // This is what I would do anyway.
    }
}
