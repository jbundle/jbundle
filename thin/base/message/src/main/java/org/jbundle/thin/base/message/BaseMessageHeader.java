/*
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jbundle.model.Freeable;
import org.jbundle.model.message.MessageHeader;
import org.jbundle.model.util.Constant;
import org.jbundle.model.util.Util;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/** 
 * A BaseMessageHeader is a object describing the destination and type of message.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class BaseMessageHeader extends Object
    implements MessageHeader, Serializable, Freeable, Cloneable
{
	private static final long serialVersionUID = 1L;

    /**
     * The (optional) code of this message(info).
     */
    public static final String MESSAGE_CODE = "messageCode";
    /**
     * The code of this message(info).
     */
    public static final String BASE_PACKAGE = "package";
    /**
     * The code of this message(info).
     */
    public static final String INTERNAL_MESSAGE_CLASS = "internalMessageClass";
    /**
     * If messages can only contain a single detail item.
     */
    public static final String SINGLE_DETAIL_PARAM = "singleDetail";

    /**
     * The destination queue name.
     */
    protected String m_strQueueName = null;
    /**
     * The queue type (Local/JMS).
     */
    protected String m_strQueueType = null;
    /**
     * The (optional) registry ID of the filter I am being sent to.
     */
    protected Integer m_intFilterID = null;
    /**
     * The properties for this header/filter.
     * If you have filter properties, you will have to store them yourself, these are only the key properties.
     */
    protected Object[][] m_mxProperties = null;
    /**
     * The source of this message (This is used to reduce echos).
     */
    protected transient Object m_source = null;

    /**
     * Creates new BaseMessageHeader.
     */
    public BaseMessageHeader()
    {
        super();
    }
    /**
     * Constructor.
     * @param strQueueName Name of the queue.
     * @param strQueueType Type of queue - remote or local.
     * @param source usually the object sending or listening for the message, to reduce echos.
     */
    public BaseMessageHeader(String strQueueName, String strQueueType, Object source, Map<String,Object> properties)
    {
        this();
        this.init(strQueueName, strQueueType, source, properties);
    }
    /**
     * Constructor.
     * @param strQueueName Name of the queue.
     * @param strQueueType Type of queue - remote or local.
     * @param source usually the object sending or listening for the message, to reduce echos.
     */
    public void init(String strQueueName, String strQueueType, Object source, Map<String,Object> properties)
    {
        if (strQueueName == null)
            if ((strQueueType == null)
                || (strQueueType.equals(MessageConstants.INTRANET_QUEUE)))
        {
            strQueueType = MessageConstants.INTRANET_QUEUE;
            strQueueName = MessageConstants.RECORD_QUEUE_NAME;
        }
        if (strQueueType == null)
            strQueueType = MessageConstants.INTERNET_QUEUE;
        m_strQueueName = strQueueName;
        m_strQueueType = strQueueType;
        m_source = source;
        
        m_mxProperties = this.createNameValueTree(null, properties);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        m_strQueueName = null;
        m_strQueueType = null;
        m_source = null;
        m_mxProperties = null;
        m_intFilterID = null;
    }
    /**
     * Clone this message header.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();   // This is usually fine, since a deep clone is not needed
    }
    /**
     * Get the queue name for this message header.
     * @return The queue name.
     */
    public String getQueueName()
    {
        return m_strQueueName;
    }
    /**
     * Get the queue type for this message header.
     * @return The queue type.
     */
    public String getQueueType()
    {
        return m_strQueueType;
    }
    /**
     * Get the queue name for this message header.
     * @return The queue name.
     */
    public void setQueueName(String strQueueName)
    {
        m_strQueueName = strQueueName;
    }
    /**
     * Get the queue type for this message header.
     * @return The queue type.
     */
    public void setQueueType(String strQueueType)
    {
        m_strQueueType = strQueueType;
    }
    /**
     * Get the source code. This is used to reduce echos.
     * @return The source.
     */
    public Object getMessageSource()
    {
        return m_source;
    }
    /**
     * Get the source code. This is used to reduce echos.
     * @return The source.
     */
    public void setMessageSource(Object source)
    {
        m_source = source;
    }
    /**
     * Get the name/value pairs in an ordered tree.
     * Note: Replace this with a DOM tree when it is available in the basic SDK.
     * @return A matrix with the name, type, etc.
     */
    public final Object[][] getNameValueTree()
    {
        return m_mxProperties;
    }
    /**
     * Get the name/value pairs in an ordered tree.
     * Note: Replace this with a DOM tree when it is available in the basic SDK.
     * @return A matrix with the name, type, etc.
     */
    public final void setNameValueTree(Object[][] mxProperties)
    {
        m_mxProperties = mxProperties;
    }
    /**
     * Update this filter with this new information.
     * Override this to do something locally.
     * Remember to call super after updating this filter, as this method updates the remote copy of this filter.
     * <p/>Your code should look something like this:
     * <pre>
     *   mxProperties = super.createNameValueTree(mxProperties, properties);
     *   return this.addNameValue(mxNameValueTree, CHANNEL_NAME, properties.get(CHANNEL_NAME));
     * </pre>
     * @param properties New filter information (ie, bookmark=345).
     */
    public Object[][] createNameValueTree(Object[][] mxProperties, Map<String, Object> properties)
    {
        return mxProperties;
    }
    /**
     * Add this name/value pair to this matrix.
     * @param mxString Source matrix (or null if new).
     * @param strName Name to add
     * @param strValue Value to add
     */
    public final Object[][] addNameValue(Map<String, Object> properties, String strName, Object[][] mxProperties)
    {
        if (properties != null)
            mxProperties = this.addNameValue(mxProperties, strName, properties.get(strName));
        return mxProperties;
    }
    /**
     * Add this name/value pair to this matrix.
     * Note: Be careful as the matrix is updated by this method... If the matrix was the
     * properties matrix, there would be no way to know where the old tree entry was.
     * @param mxString Source matrix (or null if new).
     * @param strName Name to add
     * @param strValue Value to add
     * Skip if the value is null.
     */
    public final Object[][] addNameValue(Object[][] mxString, String strName, Object strValue)
    {
        if (strName == null)
            return mxString;
        if (strValue == null)
            return mxString;
        if (mxString == null)
            mxString = new Object[1][2];
        else
        {
            for (int i = 0; i < mxString.length; i++)
            {   // If it is already there, replace the value.
                if (strName.equalsIgnoreCase((String)mxString[i][MessageConstants.NAME]))
                {
                    mxString[i][MessageConstants.VALUE] = strValue;
                    return mxString;
                }
            }
            Object[][] tempString = mxString;
            mxString = new Object[tempString.length + 1][2];
            for (int i = 0; i < tempString.length; i++)
            {
                mxString[i][MessageConstants.NAME] = tempString[i][MessageConstants.NAME];
                mxString[i][MessageConstants.VALUE] = tempString[i][MessageConstants.VALUE];
            }
        }
        
        int i = mxString.length - 1;
        mxString[i][MessageConstants.NAME] = strName;
        mxString[i][MessageConstants.VALUE] = strValue;
        
        return mxString;
    }
    /**
     * Create a copy of this matrix.
     * @param mxString Source matrix (or null if new).
     * @param strName Name to add
     * @param strValue Value to add
     * Skip if the value is null.
     */
    public final Object[][] cloneMatrix(Object[][] tempString)
    {
        Object[][] mxString = null;
        if (tempString != null)
        {
            mxString = new Object[tempString.length][2];
            for (int i = 0; i < tempString.length; i++)
            {
                mxString[i][MessageConstants.NAME] = tempString[i][MessageConstants.NAME];
                mxString[i][MessageConstants.VALUE] = tempString[i][MessageConstants.VALUE];
            }
        }
        return mxString;
    }
    /**
     * Return the state of this object as a properties object.
     * @return The properties.
     */
    public Map<String,Object> getProperties()
    {
        Map<String,Object> properties = new HashMap<String,Object>();
        if (m_mxProperties != null)
        {
            for (int i = 0; i < m_mxProperties.length; i++)
            {
                if (m_mxProperties[i][MessageConstants.NAME] != null)
                    if (m_mxProperties[i][MessageConstants.VALUE] != null)
                        properties.put((String)m_mxProperties[i][MessageConstants.NAME], m_mxProperties[i][MessageConstants.VALUE]);
            }
        }
        return properties;
    }
    /**
     * Return the state of this object as a properties object.
     * @param strKey The key to return.
     * @return The properties.
     */
    public Object get(String strKey)
    {
        if (m_mxProperties != null)
        {
            for (int i = 0; i < m_mxProperties.length; i++)
            {
                if (strKey != null)
                    if (strKey.equalsIgnoreCase((String)m_mxProperties[i][MessageConstants.NAME]))
                        return m_mxProperties[i][MessageConstants.VALUE];
            }
        }
        return null;    // Not found
    }
    /**
     * The (optional) registry ID of the filter I am being sent to.
     * @return The registry ID of the filter I am being sent to.
     */
    public Integer getRegistryIDMatch()
    {
        return m_intFilterID;
    }
    /**
     * The (optional) registry ID of the filter I am being sent to.
     * Setting this object makes this a private message.
     * @param intFilterID The registry ID of the filter I am being sent to.
     */
    public void setRegistryIDMatch(Integer intFilterID)
    {
        m_intFilterID = intFilterID;
    }
    /**
     * Get the message header data as a XML String.
     * @return
     */
    public StringBuffer addXML(StringBuffer sbXML)
    {
        Util.addStartTag(sbXML, BaseMessage.HEADER_TAG).append(Constant.RETURN);
        for (String strKey : this.getProperties().keySet())
        {
            Object data = this.get(strKey);
            Util.addStartTag(sbXML, strKey);
            sbXML.append(data);
            Util.addEndTag(sbXML, strKey).append(Constant.RETURN);
        }
        Util.addEndTag(sbXML, BaseMessage.HEADER_TAG).append(Constant.RETURN);
        return sbXML;
    }
    /**
     * Setup this message given this internal data structure.
     * @param data The data in an intermediate format.
     */
    public static BaseMessageHeader createMessageHeader(String strMessageHeaderClassName, String strQueueName, String strQueueType, Object source, Map<String,Object> properties)
    {
        BaseMessageHeader messageHeader = (BaseMessageHeader)ClassServiceUtility.getClassService().makeObjectFromClassName(strMessageHeaderClassName);
        if (messageHeader != null)
        	messageHeader.init(strQueueName, strQueueType, source, properties);
        return messageHeader;
    }
}
