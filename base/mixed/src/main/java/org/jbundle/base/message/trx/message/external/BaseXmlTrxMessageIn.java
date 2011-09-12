/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external;

import javax.xml.soap.SOAPMessage;

import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.convert.BaseConvertToMessage;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.util.ThinUtil;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


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
public class BaseXmlTrxMessageIn extends ExternalTrxMessageIn
{
    protected BaseConvertToMessage m_convertToMessage = null;

    /**
      * Creates new BaseTrxMessage
     */
    public BaseXmlTrxMessageIn() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public BaseXmlTrxMessageIn(BaseMessage message, SOAPMessage soapMessage)
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
     * 
     */
    public void free()
    {
        if (m_convertToMessage == null)
            m_convertToMessage.free();
        m_convertToMessage = null;
        super.free();
    }
    /**
     * Get the unmarshaller.
     * @return
     */
    public BaseConvertToMessage getConvertToMessage()
    {
        if (m_convertToMessage == null)
        {
            BaseMessageHeader trxMessageHeader = this.getMessage().getMessageHeader();
            String strMessageClass = (String)trxMessageHeader.get(TrxMessageHeader.MESSAGE_MARSHALLER_CLASS);
            String strPackage = (String)trxMessageHeader.get(TrxMessageHeader.BASE_PACKAGE);
            strMessageClass = ClassServiceUtility.getFullClassName(strPackage, strMessageClass);
            m_convertToMessage = (BaseConvertToMessage)ClassServiceUtility.getClassService().makeObjectFromClassName(strMessageClass);
            if (m_convertToMessage != null)
            	m_convertToMessage.init(this);
        }
        return m_convertToMessage;
    }
    /**
     * Convert the external form to the internal message form.
     * You must override this method.
     * @param externalMessage The received message to be converted to internal form.
     * @return The internal message.
     */
    public int convertExternalToInternal(Object recordOwner)
    {
        // Step 4 - Convert the JAXB objects to my internal format
        Object root = this.convertToMessage();
        if (root == null)
            return DBConstants.ERROR_RETURN;
        return this.getConvertToMessage().convertMarshallableObjectToInternal(root, (RecordOwner)recordOwner);
    }
    /**
     * Convert this DOM Tree to the JAXB message tree.
     * Don't override this method, override the unmarshalRootElement method.
     * @param nodeBody The source message.
     * @return The JAXB object root.
     */
    public Object convertToMessage()
    {
        try {
            Object reply = this.getRawData();

            org.w3c.dom.Node nodeBody = this.getMessageBody(reply, false);

            if (this.getConvertToMessage() == null)
                return null;
            Object msg = this.getConvertToMessage().unmarshalRootElement(nodeBody, this);

            return msg;
        } catch(Throwable ex) {
            this.setMessageException(ex);
        }
        return null;    // Error
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public String getXML()
    {
        return super.getXML();  // For now
    }
    /**
     * Get the SOAP message body as a DOM node.
     * @param message the SOAP message.
     * @param bReturnCopy Return a copy of the message node, instead of the actual node.
     * @return The DOM node containing the message body.
     */
    public org.w3c.dom.Node getMessageBody(Object rawData, boolean bReturnCopy)
    {
        return this.getDOM();    // (by default) Override this to return the actual dom message
    }
    /**
     * If an exception occurred, set it in the message
     * @param ex
     */
    public void setMessageException(Throwable ex)
    {
        String strErrorMessage = ex.getMessage();
        BaseMessage message = this.getMessage();
        if ((message != null) && (message.getMessageHeader() instanceof TrxMessageHeader))
            ((TrxMessageHeader)message.getMessageHeader()).put(TrxMessageHeader.MESSAGE_ERROR, strErrorMessage);
        else
            ex.printStackTrace();
    }
}
