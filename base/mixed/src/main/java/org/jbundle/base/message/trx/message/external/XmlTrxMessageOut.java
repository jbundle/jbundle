/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.thin.base.message.BaseMessage;
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
public class XmlTrxMessageOut extends BaseXmlTrxMessageOut
{

    /**
      * Creates new BaseTrxMessage
     */
    public XmlTrxMessageOut() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public XmlTrxMessageOut(BaseMessage message, Object strHtml)
    {
        this();
        this.init(message, strHtml);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(BaseMessage message, Object strHtml)
    {
        super.init(message, strHtml);
    }
    /**
     * Convert this tree to a DOM object.
     * Currently this is lame because I convert the tree to text, then to DOM.
     * In the future, jaxb will be able to convert directly.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public Node getDOM()
    {
        if (this.getRawData() instanceof String)
        return Utility.convertXMLToDOM((String)this.getRawData());
        return super.getDOM();
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public String getXML()
    {
        if (this.getRawData() instanceof String)
            return (String)this.getRawData();
        return super.getXML();
    }
    /**
     * Convert this source message to the ECXML format.
     * Typically you do not override this method, you override the getTransformer method
     * to supply a XSLT document to do the conversion.
     * @param source The source XML document.
     */
    public int convertInternalToExternal(Object recordOwner)
    {
        if (this.getXSLTDocument() != null)
        {    // Now use the XSLT document to convert this XSL document.
            String strXML = this.getMessage().getXML(true);
            String strResultXML = this.transformMessage(strXML, null);
            this.setRawData(strResultXML);
            return DBConstants.NORMAL_RETURN;    // Success
        }
        return super.convertInternalToExternal(recordOwner);
    }
}
