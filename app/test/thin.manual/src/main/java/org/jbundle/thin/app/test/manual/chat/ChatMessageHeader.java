/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.chat;

/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.thin.base.message.BaseMessageHeader;


/** 
 * A Record message is a message explaining the changes to this record.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class ChatMessageHeader extends BaseMessageHeader
    implements Serializable, ChatConstants
{
    private static final long serialVersionUID = 1L;

    /**
     * The filter name.
     */
    protected String m_strFilterName = null;

    /**
     * Creates new RecordMessage.
     */
    public ChatMessageHeader()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ChatMessageHeader(String strTopic, String strRoom, String strFilterName, Object source)
    {
        this();
        this.init(strTopic, strRoom, strFilterName, source);
    }
    /**
     * Constructor.
     */
    public void init(String strTopic, String strRoom, String strFilterName, Object source)
    {
        m_strFilterName = strFilterName;

        Hashtable<String,Object> properties = new Hashtable<String,Object>();
        if (strTopic != null)
            properties.put(TREE_NAME, strTopic);
        if (strRoom != null)
            properties.put(DOOR_NAME, strRoom);
        super.init(CHAT_QUEUE_NAME, CHAT_QUEUE_TYPE, source, properties);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        m_strFilterName = null;

        super.free();
    }
    /**
     * Get the filter name.
     * @return The filter name.
     */
    public String getFilterName()
    {
        return m_strFilterName;
    }
    /**
     * Update this filter with this new information.
     * Override this to do something locally.
     * Remember to call super after updating this filter, as this method updates the remote copy of this filter.
     * @param properties New filter information (ie, bookmark=345).
     */
    public Object[][] createNameValueTree(Object[][] mxProperties, Map<String, Object> properties)
    {
        mxProperties = super.createNameValueTree(mxProperties, properties);
        mxProperties = this.addNameValue(mxProperties, TREE_NAME, properties.get(TREE_NAME));
        mxProperties = this.addNameValue(mxProperties, DOOR_NAME, properties.get(DOOR_NAME));
        return mxProperties;
    }
}
