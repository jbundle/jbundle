/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM
 */

package org.jbundle.base.message.trx.message.external.convert.jibx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.ExternalTrxMessageOut;
import org.jbundle.base.message.trx.message.external.convert.BaseXmlConvertToNative;
import org.jbundle.base.message.trx.transport.soap.SOAPMessageTransport;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Constants;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.jibx.extras.JDOMWriter;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.w3c.dom.Document;
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
public class JibxConvertToNative extends BaseXmlConvertToNative
{

    /**
      * Creates new BaseTrxMessage
     */
    public JibxConvertToNative() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public JibxConvertToNative(ExternalTrxMessageOut message)
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
        if (root != null)
        {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String packageName = (String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.JIBX_PACKAGE_NAME);
                String bindingName = (String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.JIBX_BINDING_NAME);
    
                IMarshallingContext m = JibxContexts.getJAXBContexts().getMarshaller(packageName, bindingName);
                if (m == null)
                    return null;
                synchronized(m)
                {   // Since the marshaller is shared (may want to tweek this for multi-cpu implementations)
                	m.marshalDocument(root, DBConstants.URL_ENCODING, null, out);
                }
                String strXML = out.toString(Constants.STRING_ENCODING);
                return strXML;
            } catch (IOException ex)  {
                ex.printStackTrace();   // Never
            } catch (JiBXException ex)   {
                ex.printStackTrace();
            } catch (java.lang.IllegalArgumentException ex)   {
                ex.printStackTrace();
            }
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
        Object root = m_message.getRawData();
        try {
            String packageName = (String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.JIBX_PACKAGE_NAME);
            String bindingName = (String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.JIBX_BINDING_NAME);

            Node node = null;            
//            DocumentBuilder db = Util.getDocumentBuilder();
            Document doc = null;            
            
            IMarshallingContext mctx = JibxContexts.getJAXBContexts().getMarshaller(packageName, bindingName);
            if (mctx == null)
                return null;
            synchronized(mctx)
            {   // Since the marshaller is shared (may want to tweek this for multi-cpu implementations)
//?            	DomElementMapper domElementMapper  = new DomElementMapper();
//?            	m.marshalDocument(domElementMapper);
//?            	domElementMapper.marshal(root, m);
//?                IBindingFactory bfact = BindingDirectory.getFactory(clazz);
//?                IMarshallingContext mctx = bfact.createMarshallingContext();
                
            	String[] namespaces = JibxContexts.getJAXBContexts().get(packageName, bindingName).getFactory().getNamespaces();
                JDOMWriter jdomWriter = new JDOMWriter(namespaces);
                mctx.setXmlWriter(jdomWriter);
                mctx.marshalDocument(root);
                mctx.endDocument();
                
                org.jdom.Document jdoc = jdomWriter.getDocument();
                
                DOMOutputter dout = new DOMOutputter();
                doc = dout.output(jdoc);
            }
            node = doc.getDocumentElement();

            if (node != null)
                return node;
        } catch (JiBXException ex)   {
            ex.printStackTrace();
        } catch (java.lang.IllegalArgumentException ex)   {
            ex.printStackTrace();
        } catch (JDOMException e) {
			e.printStackTrace();
		}
        return super.getDOM();
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
