/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external.convert;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.message.trx.message.external.BaseXmlTrxMessageIn;
import org.jbundle.base.message.trx.message.external.ExternalTrxMessageIn;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.MessageDataDesc;
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
public class BaseConvertToMessage extends Object
{
    protected ExternalTrxMessageIn m_message = null;

    /**
      * Creates new BaseTrxMessage
     */
    public BaseConvertToMessage() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public BaseConvertToMessage(ExternalTrxMessageIn message)
    {
        this();
        this.init(message);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(ExternalTrxMessageIn message)
    {
        m_message = message;
    }
    /**
     * 
     */
    public void free()
    {
    }
    /**
     * 
     * @return
     */
    public BaseMessage getMessage()
    {
        return m_message.getMessage();
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
     * You SHOULD override this if the unmarshaller has a native method to unmarshall a dom node.
     * @return The root element.
     */
    public Object unmarshalRootElement(Node node, BaseXmlTrxMessageIn soapTrxMessage) throws Exception
    {
            // Override this! (If you can!)
        TransformerFactory tFact = TransformerFactory.newInstance();

        Source source = new DOMSource(node);
        Writer writer = new StringWriter();
        Result result = new StreamResult(writer);

        Transformer transformer = tFact.newTransformer();
        transformer.transform(source, result);

        writer.flush();
        writer.close();
        String strXMLBody = writer.toString();
        
        Reader inStream = new StringReader(strXMLBody);

        Object msg = this.unmarshalRootElement(inStream, soapTrxMessage);

        inStream.close();
        
        return msg;        
    }
    /**
     * Convert the external form to the internal message form.
     * You must override this method.
     * @param root The root object of the marshallable object.
     * @param recordOwner The recordowner
     * @return The error code.
     */
    public int convertMarshallableObjectToInternal(Object root, RecordOwner recordOwner)
    {
        return DBConstants.NORMAL_RETURN;    // Override this!
    }
    /**
     * Convert this date to the standard string date format.
     * which is yyyy-MM-dd.
     * @param date
     * @return
     */
    public Date dateStringToDateFormat(String string)
    {
        try {
            if (string != null)
                return BaseXmlConvertToNative.gDateFormat.parse(string);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Utility to add the standard payload properties to the message
     * @param msg
     * @param message
     */
    public void addPayloadProperties(Object msg, BaseMessage message)
    {
        MessageDataDesc messageDataDesc = message.getMessageDataDesc(null);   // Top level only
        if (messageDataDesc != null)
        {
            Map<String,Class<?>> mapPropertyNames = messageDataDesc.getPayloadPropertyNames(null);
        
            if (mapPropertyNames != null)
            {
                Map<String,Object> properties = this.getPayloadProperties(msg, mapPropertyNames);
                for (String key : properties.keySet())
                {
                    message.put(key, properties.get(key));
                }
            }
        }
    }
    /**
     * Get all the payload properties.
     * @param msg
     * @return
     */
    public Map<String,Object> getPayloadProperties(Object msg, Map<String,Class<?>> mapPropertyNames)
    {
        Map<String,Object> properties = new HashMap<String,Object>();
        
        for (String strKey : mapPropertyNames.keySet())
        {
            this.addPayloadProperty(msg, properties, strKey);            
        }
        this.addPayloadProperty(msg, properties, "Errors"); // For responses only            
        return properties;
    }
    /**
     * Add this payload property to this map.
     * @param msg
     * @param properties
     * @param key
     */
    public void addPayloadProperty(Object msg, Map<String,Object> properties, String key)
    {
        String name = "get" + key;
        try {
            Method method = msg.getClass().getMethod(name, EMPTY_PARAMS);
            if (method != null)
            {
                Object value = method.invoke(msg, EMPTY_DATA);
                if (value != null)
                {
                    if (value.getClass().getName().contains("ErrorsType"))
                        this.addErrorsProperties(properties, value);
                    else
                        properties.put(key, value);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // Ignore this error
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    /**
     * Add the error properties from this message.
     * @param properties
     * @param errorsType
     */
    public void addErrorsProperties(Map<String,Object> properties, Object errorsType)
    {
        try {
            Method method = errorsType.getClass().getMethod("getErrors", EMPTY_PARAMS);
            if (method != null)
            {
                Object value = method.invoke(errorsType, EMPTY_DATA);
                if (value instanceof List<?>)
                {
                    for (Object errorType : (List<?>)value)
                    {
                        method = errorType.getClass().getMethod("getShortText", EMPTY_PARAMS);
                        if (method != null)
                        {
                            value = method.invoke(errorType, EMPTY_DATA);
                            if (value instanceof String)
                                properties.put("Error", value);
                        }
                    }                
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    /**
     * Lookup this OTA code from the code table.
     * @param otaNameCode
     * @param otaDesc
     * @param defaultValue
     * @return
     */
    public String getOTACode(String otaNameCode, String otaDesc, String defaultValue)
    {
        //+String version = this.getVersionCode();
        return defaultValue;
    }
    /**
     * Get the schema version.
     * @return
     */
    public String getVersionCode()
    {
        return null;
    }
    public static final Class<?>[] EMPTY_PARAMS = new Class[0];
    public static final Object[] EMPTY_DATA = new Object[0];
}
