/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jbundle.base.message.core.trx.TrxMessageHeader;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.model.App;
import org.jbundle.model.Task;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.ExternalMessage;
import org.jbundle.thin.base.screen.BaseApplet;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
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
public class ExternalTrxMessage extends Object
    implements ExternalMessage
{
    /**
     * Scratch XML document.
     */
    protected Document m_doc = null;
    /**
     * Parent message.
     */
    protected BaseMessage m_message = null;
    /**
     * External data.
     */
    protected Object m_rawData = null;

    /**
      * Creates new ExternalTrxMessage.
     */
    public ExternalTrxMessage() 
    {
        super();
    }
    /**
      * Initialize new ExternalTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public ExternalTrxMessage(BaseMessage message, Object objRawMessage)
    {
        this();
        this.init(message, objRawMessage);
    }
    /**
      * Initialize new ExternalTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(BaseMessage message, Object objRawMessage)
    {
        m_message = message;
        if (message != null)
            message.setExternalMessage(this);
        if (objRawMessage != null)
            this.setRawData(objRawMessage);
    }
    /**
     * Free all the resources belonging to this class.
     */
    public void free()
    {
    }
    /**
     * Return the parent message.
     * @return
     */
    public BaseMessage getMessage()
    {
        return m_message;
    }
    /**
     * Get the raw data object for this incoming message.
     * @return the data object (Must override).
     */
    public Object getRawData()
    {
        return m_rawData;
    }
    /**
     * Get the raw data object for this incoming message.
     * @return the data object (Must override).
     */
    public void setRawData(Object rawData)
    {
        m_rawData = rawData;
    }
    /**
     * Convert the external form to the internal message form.
     * You must override this method.
     * @param externalMessage The received message to be converted to internal form.
     * @return The internal message.
     */
    public int convertExternalToInternal(Object recordOwner)
    {
        boolean bSuccess = this.getMessage().setDOM(this.getDOM());
        return bSuccess ? DBConstants.NORMAL_RETURN : DBConstants.ERROR_RETURN; 
    }
    /**
     * Convert this source message to the ECXML format.
     * Typically you do not override this method, you override the getTransformer method
     * to supply a XSLT document to do the conversion.
     * @param source The source XML document.
     */
    public int convertInternalToExternal(Object recordOwner)
    {
        boolean bSuccess = false;
        Node node = this.getMessage().getDOM();
        if (node != null)
            bSuccess = this.setDOM(node);
        else
            bSuccess = this.setXML(this.getMessage().getXML(false));
        return bSuccess ? DBConstants.NORMAL_RETURN : DBConstants.ERROR_RETURN;
    }
    /**
     * Convert this tree to a DOM object.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public Node getDOM()
    {
        return null;
    }
    /**
     * Convert this tree to a DOM object.
     * Override this.
     * @param root The room jaxb item.
     * @return True if successful.
     * @return The dom tree.
     */
    public boolean setDOM(Node node)
    {
        return false;   // Override this to do something!
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public String getXML()
    {
        Node node = this.getDOM();
        return Utility.convertDOMToXML(node);
    }
    /**
     * Convert this message to string.
     */
    public String toString()
    {
        return this.getXML();
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public boolean setXML(String strXML)
    {
        Node node = Utility.convertXMLToDOM(strXML);
        return this.setDOM(node);
    }
    /**
     * Standard header params.
     */
    public static final String[] m_rgstrHeaderParams =
    {
        TrxMessageHeader.DESTINATION_PARAM,
        TrxMessageHeader.DESTINATION_MESSAGE_PARAM,
        TrxMessageHeader.SOURCE_PARAM,
        TrxMessageHeader.SOURCE_MESSAGE_PARAM,
        TrxMessageHeader.REPLY_TO_PARAM,
        TrxMessageHeader.REGISTRY_ID,
        TrxMessageHeader.XSLT_DOCUMENT,
        TrxMessageHeader.MESSAGE_CODE,
        TrxMessageHeader.MESSAGE_PROCESSOR_CLASS,
        TrxMessageHeader.MESSAGE_RESPONSE_ID,
        TrxMessageHeader.MESSAGE_RESPONSE_CODE,
        TrxMessageHeader.MESSAGE_RESPONSE_CLASS,
        TrxMessageHeader.LOG_TRX_ID,
    };
    /**
     * Move the header params from the message map to the header map.
     */
    public void moveHeaderParams(Map<String,Object> mapMessage, Map<String,Object> mapHeader)
    {
        for (int i = 0; i < m_rgstrHeaderParams.length; i++)
        {
            if (mapMessage.get(m_rgstrHeaderParams[i]) != null)
                if (mapHeader.get(m_rgstrHeaderParams[i]) == null)
                {
                    Object objValue = mapMessage.get(m_rgstrHeaderParams[i]);
                    mapMessage.remove(m_rgstrHeaderParams[i]);
                    mapHeader.put(m_rgstrHeaderParams[i], objValue);
                }
        }
    }
    /**
     * Get the scratch document for this message.
     * Creates a new document the first time.
     * @return The scratch document.
     */
    public Document getScratchDocument(DocumentBuilder db)
    {
        if (m_doc == null)
        {
            m_doc = db.newDocument();
        }
        return m_doc;
    }
    /**
     * Use XSLT to convert this source tree into a new tree.
     * @param result If this is specified, transform the message to this result (and return null).
     * @param source The source to convert.
     * @param streamTransformer The (optional) input stream that contains the XSLT document.
     * If you don't supply a streamTransformer, you should override getTransforerStream() method.
     * @return The new tree.
     */
    public String transformMessage(String strXML, StreamSource streamTransformer)
    {
        Reader reader = new StringReader(strXML);
        StreamSource source = new StreamSource(reader);

        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        try {
            TransformerFactory tFact = TransformerFactory.newInstance();
            if (streamTransformer == null)
                streamTransformer = this.getTransformerStream(null);
            Transformer transformer = tFact.newTransformer(streamTransformer);

            if (result == null)
            {
                DocumentBuilder db = Utility.getDocumentBuilder();
                synchronized (db)
                {
                    Document doc = this.getScratchDocument(db);
                    DocumentFragment docFrag = doc.createDocumentFragment();
                        // Add a soap body element to the soap body
                    result = new DOMResult(docFrag);
                    transformer.transform(source, result);
                }

    //            this.printSource(new DOMSource(docFrag), System.out);
//?                org.w3c.dom.Element elRoot = (org.w3c.dom.Element)docFrag.getFirstChild();

                return stringWriter.toString();
            }
            else
            {
                transformer.transform(source, result);
                return stringWriter.toString();
            }
        } catch (TransformerConfigurationException ex)    {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Get the XSLT Document to do the conversion.
     * Override this if you have a standard document to suppyl.
     * @param source The source XML document.
     * @return The XML tree that conforms to the ECXML format.
     */
    public String getXSLTDocument()
    {
        TrxMessageHeader messageHeader = (TrxMessageHeader)this.getMessage().getMessageHeader();
        String strDocument = null;
        if (messageHeader != null)
            strDocument = (String)messageHeader.get(TrxMessageHeader.XSLT_DOCUMENT);
        return strDocument;
    }
    /**
     * Get the XSLT transformer stream from this file or URL String.
     * @param strDocument a URL or file string for the XSL document.
     * @returns The transformer or default transformer if it doesn't exist.
     */
    public StreamSource getTransformerStream(String strDocument)
    {
        StreamSource source = null;
        if (strDocument == null)
            strDocument = this.getXSLTDocument();
        if (strDocument == null)
        {
            Reader reader = new StringReader(XSL_CONVERT);
            source = new StreamSource(reader);
            return source;
        }
        if (strDocument.indexOf(':') == -1)
        {   // See if it is a file name
            try {   // First try it as a filename
                FileReader reader = new FileReader(strDocument);
                if (reader != null)
                    source = new StreamSource(reader);
            } catch (IOException ex)    {
                source = null;
            }                    
            if (source == null)
            {       // Now, try it as a URL
                Task task = null;//this.getTask();
                if (task == null)
                    task = BaseApplet.getSharedInstance();
                BaseApplet applet = null;
                if (task instanceof BaseApplet)
                    applet = (BaseApplet)task;
                App app = null;
                if (task != null)
                    app = task.getApplication();
                URL url = null;
                if (app != null)
                    url = app.getResourceURL(strDocument, applet);
                else
                    url = this.getClass().getClassLoader().getResource(strDocument);

                if (url != null)
                {
                    try {
                        InputStream is = url.openStream();
                        source = new StreamSource(is);
                    } catch (IOException ex)    {
                        source = null;
                    }
                }
            }
        }
        if (source == null)
        {
            try {
                URL url = new URL(strDocument);
                InputStream is = url.openStream();
                source = new StreamSource(is);
            } catch (IOException ex)    {
                source = null;
            }
        }
        return source;
    }
    /**
     * A default XSL document that does no conversion.
     */
    protected String XSL_CONVERT =
"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\"  >" +
//" <xsl:template match=\"\">" +
//"  <xsl:copy-of select=\".\" />" +
//" </xsl:template>" +
"<!-- This template copies the name and attributes and applies templates to child nodes -->" +
"<xsl:template match=\"*\">" +
"  <xsl:copy>" +
"    <xsl:for-each select=\"@*\">" +
"      <xsl:attribute name=\"{name()}\">" +
"        <xsl:value-of select=\".\"/>" +
"      </xsl:attribute>" +
"    </xsl:for-each> " +
"  	<xsl:apply-templates />" +
"  </xsl:copy>" +
"</xsl:template>" +

//"<xsl:template match=\"roomType\">" +
//" <xsl:element name=\"ota:roomType\" namespace=\"http://www.ota.org/xyz\">" +
//"  <xsl:apply-templates />" +
//" </xsl:element>" +
//"</xsl:template>" +

"</xsl:stylesheet>";
}
