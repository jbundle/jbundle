/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM
 */

package org.jbundle.base.message.trx.message.external.convert.xmlbeans;

import java.io.Reader;

import org.jbundle.base.message.trx.message.external.BaseXmlTrxMessageIn;
import org.jbundle.base.message.trx.message.external.ExternalTrxMessageIn;
import org.jbundle.base.message.trx.message.external.convert.BaseXmlConvertToMessage;
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
public class XmlbeansConvertToMessage extends BaseXmlConvertToMessage
{

    /**
      * Creates new BaseTrxMessage
     */
    public XmlbeansConvertToMessage() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public XmlbeansConvertToMessage(ExternalTrxMessageIn externalMessageIn)
    {
        this();
        this.init(externalMessageIn);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param externalMessageIn TODO
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(ExternalTrxMessageIn externalMessageIn)
    {
        super.init(externalMessageIn);
    }
    /**
     * Create the root element for this message.
     * You must override this.
     * @return The root element.
     */
    public Object unmarshalRootElement(Reader inStream, BaseXmlTrxMessageIn soapTrxMessage) throws Exception
    {
        return null;    // Override this!
    }
    /**
     * Create the root element for this message.
     * You must override this.
     * @return The root element.
     */
    public Object unmarshalRootElement(Node node, BaseXmlTrxMessageIn soapTrxMessage) throws Exception
    {
        return super.unmarshalRootElement(node, soapTrxMessage);    // Override this if you can!
    }
}
