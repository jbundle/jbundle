/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external;

import org.jbundle.base.message.core.trx.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.convert.BaseConvertToNative;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.Utility;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.util.osgi.finder.ClassServiceUtility;
import org.w3c.dom.Node;


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
public class BaseXmlTrxMessageOut extends ExternalTrxMessageOut
{
    protected BaseConvertToNative m_convertToNative = null;

    /**
      * Creates new BaseTrxMessage
     */
    public BaseXmlTrxMessageOut() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public BaseXmlTrxMessageOut(BaseMessage message, Object root)
    {
        this();
        this.init(message, root);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(BaseMessage message, Object root)
    {
        super.init(message, root);
    }
    /**
     * 
     */
    public void free()
    {
        if (m_convertToNative == null)
            m_convertToNative.free();
        m_convertToNative = null;
        super.free();
    }
    /**
     * Get the unmarshaller.
     * @return
     */
    public BaseConvertToNative getConvertToNative()
    {
        if (m_convertToNative == null)
        {
            BaseMessageHeader trxMessageHeader = this.getMessage().getMessageHeader();
            String strMessageClass = (String)trxMessageHeader.get(TrxMessageHeader.MESSAGE_MARSHALLER_CLASS);
            String strPackage = (String)trxMessageHeader.get(TrxMessageHeader.BASE_PACKAGE);
            strMessageClass = ClassServiceUtility.getFullClassName(strPackage, strMessageClass);
            m_convertToNative = (BaseConvertToNative)ClassServiceUtility.getClassService().makeObjectFromClassName(strMessageClass);
            if (m_convertToNative != null)
            	m_convertToNative.init(this);
        }
        return m_convertToNative;
    }
    /**
     * Convert this source message to the ECXML format.
     * Typically you do not override this method, you override the getTransformer method
     * to supply a XSLT document to do the conversion.
     * @param source The source XML document.
     */
    public int convertInternalToExternal(Object recordOwner)
    {
        if (this.getConvertToNative() == null)
        {
            BaseMessageHeader trxMessageHeader = this.getMessage().getMessageHeader();
            String strMessageClass = (String)trxMessageHeader.get(TrxMessageHeader.MESSAGE_MARSHALLER_CLASS);
            String strPackage = (String)trxMessageHeader.get(TrxMessageHeader.BASE_PACKAGE);
            strMessageClass = ClassServiceUtility.getFullClassName(strPackage, strMessageClass);
            return ((RecordOwner)recordOwner).getTask().setLastError("Converter does not exist: " + strMessageClass);
        }
        Object root = this.getConvertToNative().convertInternalToMarshallableObject((RecordOwner)recordOwner);
        if (root != null)
        {
            this.setRawData(root);
            return DBConstants.NORMAL_RETURN;    // Success
        }
        return super.convertInternalToExternal(recordOwner);
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public String getXML()
    {
        if (this.getConvertToNative() != null)
        {
            String strXML = this.getConvertToNative().getXML();
            if (strXML != null)
                return strXML;
        }
        return null;
    }
    /**
     * Convert this tree to a DOM object.
     * Currently this is lame because I convert the tree to text, then to DOM.
     * In the future, jaxb will be able to convert directly.
     * @return The dom tree.
     */
    public Node getDOM()
    {
        if (this.getConvertToNative() != null)
        {
            Node node = this.getConvertToNative().getDOM();
            if (node != null)
                return node;
        }
        String strXML = this.getXML();
        return Utility.convertXMLToDOM(strXML);
    }
    /**
     * Copy this XML structure to this result target.
     * @param resultTarget The target to copy to.
     * @return True if successful.
     */
    public boolean copyMessageToResult(javax.xml.transform.Result resultTarget)
    {
        return this.getConvertToNative().copyMessageToResult(resultTarget);
    }
}
