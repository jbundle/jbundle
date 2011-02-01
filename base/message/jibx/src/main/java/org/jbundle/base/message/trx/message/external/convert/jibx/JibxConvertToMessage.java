/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM
 */

package org.jbundle.base.message.trx.message.external.convert.jibx;

import java.io.Reader;

import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.BaseXmlTrxMessageIn;
import org.jbundle.base.message.trx.message.external.ExternalTrxMessageIn;
import org.jbundle.base.message.trx.message.external.convert.BaseXmlConvertToMessage;
import org.jbundle.base.message.trx.transport.soap.SOAPMessageTransport;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
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
public class JibxConvertToMessage extends BaseXmlConvertToMessage
{

    /**
      * Creates new BaseTrxMessage
     */
    public JibxConvertToMessage() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public JibxConvertToMessage(ExternalTrxMessageIn externalMessageIn)
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
        try {
            // create a JAXBContext capable of handling classes generated into
            // the primer.po package
            String packageName = (String)((TrxMessageHeader)soapTrxMessage.getMessage().getMessageHeader()).get(SOAPMessageTransport.JIBX_PACKAGE_NAME);
            String bindingName = (String)((TrxMessageHeader)soapTrxMessage.getMessage().getMessageHeader()).get(SOAPMessageTransport.JIBX_BINDING_NAME);
            if (packageName != null)
            {
                Object obj = null;
                IUnmarshallingContext u = JibxContexts.getJAXBContexts().getUnmarshaller(packageName, bindingName);
                if (u == null)
                    return null;
                synchronized(u)
                {   // Since the marshaller is shared (may want to tweek this for multi-cpu implementations)
                    obj = u.unmarshalDocument( inStream, bindingName);
                }

                return obj;
            }
//+        } catch (XMLStreamException ex)  {
//+            ex.printStackTrace();
        } catch (JiBXException ex)  {
            ex.printStackTrace();
        }

        return null;
    }
    /**
     * Create the root element for this message.
     * You must override this.
     * @return The root element.
     */
    public Object unmarshalRootElement(Node node, BaseXmlTrxMessageIn soapTrxMessage) throws Exception
    {
/*        try {
            // create a JAXBContext capable of handling classes generated into
            // the primer.po package
            String strSOAPPackage = (String)((TrxMessageHeader)soapTrxMessage.getMessage().getMessageHeader()).get(SOAPMessageTransport.SOAP_PACKAGE);
            if (strSOAPPackage != null)
            {
                Object obj = null;
                IUnmarshallingContext u = JibxContexts.getJAXBContexts().getUnmarshaller(strSOAPPackage);
                if (u == null)
                    return null;
                synchronized(u)
                {   // Since the marshaller is shared (may want to tweek this for multi-cpu implementations)
                    obj = u.unmarshalDocument( node );
                }

                return obj;
            }
//+        } catch (XMLStreamException ex)  {
//+            ex.printStackTrace();
        } catch (JiBXException ex)  {
            ex.printStackTrace();
        }
*/
        return super.unmarshalRootElement(node, soapTrxMessage);
    }
}
