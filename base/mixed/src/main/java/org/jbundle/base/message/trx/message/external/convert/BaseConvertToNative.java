/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external.convert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jbundle.base.message.trx.message.external.ExternalTrxMessageOut;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.Utility;
import org.jbundle.thin.base.db.Converter;
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
public class BaseConvertToNative extends Object
{
    protected ExternalTrxMessageOut m_message = null;


    /**
      * Creates new BaseTrxMessage
     */
    public BaseConvertToNative() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public BaseConvertToNative(ExternalTrxMessageOut message)
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
    * Convert this source message to the ECXML format.
    * Typically you do not override this method, you override the getTransformer method
    * to supply a XSLT document to do the conversion.
    * @param recordOwner The record owner
    * @param source The source XML document.
    * @return The XML tree that conforms to the ECXML format.
    */
   public Object convertInternalToMarshallableObject(RecordOwner recordOwner)
   {
       return null;    // Override this
   }
   /**
    * Convert this tree to a XML string.
    * Override this.
    * @return The dom tree.
    */
   public String getXML()
   {
       return null;    // Override this       
   }
   /**
    * Convert this tree to a DOM object.
    * Currently this is lame because I convert the tree to text, then to DOM.
    * In the future, jaxb will be able to convert directly.
    * @return The dom tree.
    */
   public Node getDOM()
   {
       return null; // Override this!
   }
   /**
    * Copy this XML structure to this result target.
    * @param resultTarget The target to copy to.
    * @return True if successful.
    */
   public boolean copyMessageToResult(javax.xml.transform.Result resultTarget)
   {
       Node tree = this.getDOM();
       return Utility.copyTreeToResult(tree, resultTarget);
   }
   public static final String DATE_FORMAT = "yyyy-MM-dd";
   public static final SimpleDateFormat gDateFormat = new SimpleDateFormat(DATE_FORMAT);
   /**
    * Convert this date to the standard string date format.
    * which is yyyy-MM-dd.
    * @param date
    * @return
    */
   public String dateToStringDateFormat(Date date)
   {
       if (date == null)
           return null;
       return gDateFormat.format(date);
   }
   public static final BigDecimal DEFAULT_VERSION = new BigDecimal("1.000");   // NO NO NO
   /**
    * Get the version of this message.
    * @return
    */
   public BigDecimal getVersion()
   {
       return new BigDecimal("1.005");
   }
   /**
    * Get the current timestamp.
    * @return
    */
   public XMLGregorianCalendar getTimeStamp()
   {
       GregorianCalendar cal = new GregorianCalendar();
       DatatypeFactory dt;
       try {
           dt = DatatypeFactory.newInstance();
           return dt.newXMLGregorianCalendar(cal);
       } catch (DatatypeConfigurationException e) {
           e.printStackTrace();
       }
       return null;
   }
   /**
    * Move the standard payload properties from the message to the xml.
    * @param message
    * @param msg
    */
   public void setPayloadProperties(BaseMessage message, Object msg)
   {
       MessageDataDesc messageDataDesc = message.getMessageDataDesc(null);   // Top level only
       if (messageDataDesc != null)
       {
           Map<String,Class<?>> mapPropertyNames = messageDataDesc.getPayloadPropertyNames(null);
           if (mapPropertyNames != null)
           {
               for (String strKey : mapPropertyNames.keySet())
               {
                   Class<?> classKey = mapPropertyNames.get(strKey);
                   this.setPayloadProperty(message, msg, strKey, classKey);
               }
           }
       }
       if (message.get("Version") == null)
           this.setPayloadProperty(DEFAULT_VERSION, msg, "Version", Float.class);
       this.setPayloadProperty(this.getTimeStamp(), msg, "TimeStamp", org.joda.time.DateTime.class); // Current timestamp
   }
   /**
    * Move this standard payload properties from the message to the xml.
    * @param message
    * @param msg
    * @param strKey
    */
   public void setPayloadProperty(BaseMessage message, Object msg, String strKey, Class<?> classKey)
   {
       Object data = message.get(strKey);
       if (data == null)
           return;
       this.setPayloadProperty(data, msg, strKey, classKey);
   }
   /**
    * Move this standard payload data to the xml.
    * @param message
    * @param msg
    * @param strKey
    */
   public void setPayloadProperty(Object data, Object msg, String strKey, Class<?> classKey)
   {
       String name = "set" + strKey;
       try {
           Class<?>[] params = {classKey};
           Method method = msg.getClass().getMethod(name, params);
           if (method != null)
           {
               Object[] datas = new Object[1];
               if (data != null)
                   if (!classKey.isAssignableFrom(data.getClass()))
                   {
                       data = this.convertObjectToDatatype(data, classKey);
                   }
               datas[0] = data;
               method.invoke(msg, datas);
           }
       } catch (SecurityException e) {
           e.printStackTrace();
       } catch (NoSuchMethodException e) {
           // Ignore this one
       } catch (InvocationTargetException e) {
           e.printStackTrace();
       } catch (IllegalAccessException e) {
           e.printStackTrace();
       }
       
   }
   /**
    * 
    * @param objData
    * @param classData
    * @return
    */
   public Object convertObjectToDatatype(Object objData, Class<?> classData)
   {
       try {
           objData =  Converter.convertObjectToDatatype(objData, classData, null);
           if (classData == XMLGregorianCalendar.class)
           {
               try {
                   DatatypeFactory dt = DatatypeFactory.newInstance();
                   objData = dt.newXMLGregorianCalendar((String)objData);
               } catch (DatatypeConfigurationException e) {
                   e.printStackTrace();
               }
           }
           else if (classData == BigDecimal.class)
               objData = new BigDecimal((String)objData);
           else if (classData == BigInteger.class)
               objData = new BigInteger((String)objData);
           else if (classData == org.joda.time.DateTime.class)
        	   objData = new org.joda.time.DateTime(objData);

           return objData;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
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
}
