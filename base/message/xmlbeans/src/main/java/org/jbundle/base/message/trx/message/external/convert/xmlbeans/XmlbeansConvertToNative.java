/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM
 */

package org.jbundle.base.message.trx.message.external.convert.xmlbeans;

import org.jbundle.base.message.trx.message.external.ExternalTrxMessageOut;
import org.jbundle.base.message.trx.message.external.convert.BaseXmlConvertToNative;
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
public class XmlbeansConvertToNative extends BaseXmlConvertToNative
{

    /**
      * Creates new BaseTrxMessage
     */
    public XmlbeansConvertToNative() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public XmlbeansConvertToNative(ExternalTrxMessageOut message)
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
     * Convert this tree to a XML string.
     * Override this.
     * @return The dom tree.
     */
    public String getXML()
    {
        Object root = null;
        if (m_message != null)
            root = m_message.getRawData();
        if (root == null)
            return null;
        else
            return root.toString();
    }
    /**
     * Convert this tree to a DOM object.
     * Currently this is lame because I convert the tree to text, then to DOM.
     * In the future, jaxb will be able to convert directly.
     * @return The dom tree.
     */
    public Node getDOM()
    {
        org.apache.xmlbeans.XmlObject root = null;
        if (m_message != null)
            root = (org.apache.xmlbeans.XmlObject)m_message.getRawData();
        if (root == null)
            return null;
        else
            return root.getDomNode();
    }
}
