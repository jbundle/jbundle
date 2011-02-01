package org.jbundle.thin.app.test.manual.chat;

/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;


/** 
 * A Record message is a message explaining the changes to this record.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class ChatMessageFilter extends BaseMessageFilter
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
    public ChatMessageFilter()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ChatMessageFilter(String strTopic, String strRoom, String strFilterName, Object source)
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
        super.free();
        
        m_strFilterName = null;
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
     * Get the filter name.
     * @return The filter name.
     */
    public void setFilterName(String strFilterName)
    {
        m_strFilterName = strFilterName;
        this.updateFilterMap(null);  // Make sure any remote filter is updated.
    }
    /**
     * Does this message header match this filter?
     * @param messageHeader The message header to check.
     * @return true if match, false if no match.
     */
    public boolean isFilterMatch(BaseMessageHeader messageHeader)
    {
        boolean bMatch = super.isFilterMatch(messageHeader);
        if (bMatch)
        {
            if (!(messageHeader instanceof ChatMessageHeader))
                return false;   // Never
            String strMessageFilterName = ((ChatMessageHeader)messageHeader).getFilterName();
            if (m_strFilterName == strMessageFilterName)
                return true;    // Both null
            if ((m_strFilterName != null)
                && (m_strFilterName.equalsIgnoreCase(strMessageFilterName)))
                    return true;
            return false;
        }
        return bMatch;
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
        if (properties != null)
        {
            mxProperties = this.addNameValue(mxProperties, TREE_NAME, properties.get(TREE_NAME));
            mxProperties = this.addNameValue(mxProperties, DOOR_NAME, properties.get(DOOR_NAME));
        }
        return mxProperties;
    }
    /**
     * Update this filter with this new information.
     * Override this to do something locally.
     * Remember to call super after updating this filter, as this method updates the remote copy of this filter.
     * @param properties New filter information (ie, bookmark=345).
     * @return The new filter change map (must contain enough information for the remote filter to sync).
     */
    public Map<String,Object> handleUpdateFilterMap(Map<String,Object> propFilter)
    {
        if (propFilter != null)
        {
            String strFilterName = (String)propFilter.get(FILTER_NAME);
            if (strFilterName != null)
                m_strFilterName = strFilterName;
        }
        if (propFilter == null)
            propFilter = new Hashtable<String,Object>();
        propFilter.put(FILTER_NAME, m_strFilterName);   // Make sure remote has the same filter info
        return super.handleUpdateFilterMap(propFilter);   // Update any remote copy of this.
    }
}
