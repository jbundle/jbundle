/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external.convert.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;

import org.jbundle.base.message.core.trx.TrxMessageHeader;
import org.jbundle.base.message.trx.message.external.ExternalTrxMessageOut;
import org.jbundle.base.message.trx.message.external.convert.BaseXmlConvertToNative;
import org.jbundle.base.message.trx.transport.soap.SOAPMessageTransport;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
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
public class JaxbConvertToNative extends BaseXmlConvertToNative
{

    /**
      * Creates new BaseTrxMessage
     */
    public JaxbConvertToNative() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public JaxbConvertToNative(ExternalTrxMessageOut message)
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
                String strSOAPPackage = (String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.SOAP_PACKAGE);
    
                Marshaller m = JaxbContexts.getJAXBContexts().getMarshaller(strSOAPPackage);
                if (m == null)
                    return null;
                synchronized(m)
                {   // Since the marshaller is shared (may want to tweek this for multi-cpu implementations)
                    m.marshal( root, out );
                }
                String strXML = out.toString(Constants.STRING_ENCODING);
                return strXML;
            } catch (IOException ex)  {
                ex.printStackTrace();   // Never
            } catch (JAXBException ex)   {
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
            String strSOAPPackage = (String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.SOAP_PACKAGE);

            Node node = null;            
            DocumentBuilder db = Util.getDocumentBuilder();
            Document doc = db.newDocument();            
            
            Marshaller m = JaxbContexts.getJAXBContexts().getMarshaller(strSOAPPackage);
            if (m == null)
                return null;
            synchronized(m)
            {   // Since the marshaller is shared (may want to tweek this for multi-cpu implementations)
                m.marshal( root, doc );
            }
            node = doc.getDocumentElement();

            if (node != null)
                return node;
        } catch (JAXBException ex)   {
            ex.printStackTrace();
        } catch (java.lang.IllegalArgumentException ex)   {
            ex.printStackTrace();
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
        Object root = m_message.getRawData();
        try {
            String strSOAPPackage = (String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.SOAP_PACKAGE);

            Marshaller m = JaxbContexts.getJAXBContexts().getMarshaller(strSOAPPackage);
            if (m != null)
            {
                synchronized(m)
                {   // Since the marshaller is shared (may want to tweek this for multi-cpu implementations)
                    m.marshal( root, resultTarget );
                }
                return true;    // Success
            }
        } catch (JAXBException ex)   {
            ex.printStackTrace();
        } catch (java.lang.IllegalArgumentException ex)   {
            ex.printStackTrace();
        }
        return super.copyMessageToResult(resultTarget);        
    }
}
