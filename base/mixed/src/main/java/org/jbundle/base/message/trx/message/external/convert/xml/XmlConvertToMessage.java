/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM
 */

package org.jbundle.base.message.trx.message.external.convert.xml;

import java.io.Reader;

import org.jbundle.base.message.trx.message.external.BaseXmlTrxMessageIn;
import org.jbundle.base.message.trx.message.external.ExternalTrxMessageIn;
import org.jbundle.base.message.trx.message.external.convert.BaseXmlConvertToMessage;
import org.jbundle.base.util.Utility;
import org.w3c.dom.Node;


/**
 * Convert the message to native XML.
 * (No marshalling required)
 * @author  don
 * @version 
 */
public class XmlConvertToMessage extends BaseXmlConvertToMessage
{

    /**
      * Creates new BaseTrxMessage
     */
    public XmlConvertToMessage() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public XmlConvertToMessage(ExternalTrxMessageIn externalMessageIn)
    {
        this();
        this.init(externalMessageIn);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param externalMessageIn The message
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
    	String xml = Utility.transferURLStream(null, null, inStream, null);
    	soapTrxMessage.getMessage().setXML(xml);

    	return xml;
    }
    /**
     * Create the root element for this message.
     * You must override this.
     * @return The root element.
     */
    public Object unmarshalRootElement(Node node, BaseXmlTrxMessageIn soapTrxMessage) throws Exception
    {
        return super.unmarshalRootElement(node, soapTrxMessage);
    }
}
