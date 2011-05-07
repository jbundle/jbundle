/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
package org.jbundle.thin.base.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jbundle.model.message.MessageFilter;
import org.jbundle.thin.base.util.Util;


/** 
 * A BaseMessageFilter is a filter for messages.
 * It looks a a message header to see if the message should be processed.
 * A MessageFilter to used by MessageListeners and MessageReceivers to filter messages.
 * If the message receiver gets a message that matches this filter, then the message
 * listener will be sent the message.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * <pre>
 * Be sure to call receiver.addFilter(xxx) as this class must not add itself to the receiver automatically.
 * and filter.setListener(xxx);
 * to make this work.
 * </pre>
 * @author  Administrator
 * @version 1.0.0
 */
public class BaseMessageFilter extends BaseMessageHeader
    implements MessageFilter, Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;

	/**
     * The message receiver that I belong to.
     */
    protected transient BaseMessageReceiver m_messageReceiver = null;
    /**
     * The message listeners that I send these messages to.
     */
    protected transient Vector<JMessageListener> m_vListenerList = null;
    /**
     * The registry number.
     */
    protected Integer m_intID = null;
    /**
     * The (optional) ID of the remote filter.
     */
    protected Integer m_intRemoteID = null;
    /**
     * The (optional) ID of the remote filter.
     */
    protected String m_strRemoteQueueName = null;
    /**
     * The (optional) ID of the remote filter.
     */
    protected String m_strRemoteQueueType = null;
    /**
     * This is the unique ID registered with the message server's copy of the filter.
     */
    protected Integer m_intRegistryID = null;
    /**
     * Should I create the remote filter properties? For example, you would not for a memory table.
     * Note: This is set back to true after being send over the wire.
     */
    protected boolean m_bCreateRemoteFilter = true;
    /**
     * Should I update the remote filter properties? For example, you would not for a client table.
     * Note: This is set back to true after being send over the wire.
     */
    protected boolean m_bUpdateRemoteFilter = true;
    /**
     * An empty matrix.
     */
    public static final Object[][] EMPTY_MATRIX = new Object[0][0];
    /**
     * Is the client a thin client?
     */
    protected boolean m_bThinTarget = false;

    /**
     * Creates new BaseMessageFilter.
     */
    public BaseMessageFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param strQueueName Name of the queue.
     * @param strQueueType Type of queue - remote or local.
     * @param source usually the object sending or listening for the message, to reduce echos.
     */
    public BaseMessageFilter(String strQueueName, String strQueueType, Object source, Map<String,Object> properties)
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
        m_messageReceiver = null;
        m_vListenerList = null;
        m_bCreateRemoteFilter = true;
        m_bUpdateRemoteFilter = true;
        m_bThinTarget = false;
        super.init(strQueueName, strQueueType, source, properties);
    }
    /**
     * Free this object.
     * If I belong to a message listener, set my reference to null, and free the listener.
     * If I belong to a messagereceiver, remove this filter from it.
     */
    public void free()
    {
        if (m_messageReceiver != null)
            m_messageReceiver.removeMessageFilter(this, false); // Remove from this and don't free again
        m_messageReceiver = null;
        
        while (this.getMessageListener(0) != null)
        {
            JMessageListener listener = this.getMessageListener(0);
            this.removeFilterMessageListener(listener);
        }
        if (m_vListenerList != null)
            m_vListenerList.clear();
        m_vListenerList = null;
        
        super.free();
        m_intID = null;
    }
    /**
     * Set the filter ID.
     * @param intID The ID of this filter.
     */
    public void setFilterID(Integer intID)
    {
        m_intID = intID;
    }
    /**
     * Set the filter ID.
     * @return The ID of this filter.
     */
    public Integer getFilterID()
    {
        return m_intID;
    }
    /**
     * Get the filter ID.
     * @return The filter ID.
     */
    public Integer getRemoteFilterID()
    {
        return m_intRemoteID;
    }
    /**
     * Get the remote filter queue name..
     * @return The remote queue name.
     */
    public String getRemoteFilterQueueName()
    {
        return m_strRemoteQueueName;
    }
    /**
     * Get the remote filter queue type..
     * @return The remote queue type.
     */
    public String getRemoteFilterQueueType()
    {
        return m_strRemoteQueueType;
    }
    /**
     * Get the ID of this filter at the bottom-level message server.
     * @return The ID at the message server (If registry is null, this must be the message server).
     */
    public Integer getRegistryID()
    {
        if (m_intRegistryID != null)
            return m_intRegistryID;
        return this.getFilterID();
    }
    /**
     * Get the ID of this filter at the bottom-level message server.
     * @return The ID at the message server (If registry is null, this must be the message server).
     */
    public void setRegistryID(Integer intRegistryID)
    {
        m_intRegistryID = intRegistryID;
    }
    /**
     * Set the links to the remote filter.
     * @param strRemoteQueueName The remote queue name.
     * @param strRemoteQueueType The remote queue type.
     * @param intRemoteQueueID The remote queue ID.
     */
    public void setRemoteFilterInfo(String strRemoteQueueName, String strRemoteQueueType, Integer intRemoteQueueID, Integer intRegistryID)
    {
        m_strRemoteQueueName = strRemoteQueueName;
        m_strRemoteQueueType = strRemoteQueueType;
        m_intRemoteID = intRemoteQueueID;
        m_intRegistryID = intRegistryID;
    }
    /**
     * Does this message header match this filter?
     * @param messageHeader The message header to check.
     * @return true if match, false if no match.
     */
    public boolean isFilterMatch(BaseMessageHeader messageHeader)
    {
        boolean bMatch = true;
        if (messageHeader != null)
        {
            if (this.getQueueName() != null)
                if (!this.getQueueName().equals(messageHeader.getQueueName()))
                    return false;   // No match
            if (this.getQueueType() != null)
                if (!this.getQueueType().equals(messageHeader.getQueueType()))
                    return false;   // No match
            if (this.getMessageSource() != null)
                if (this.getMessageSource() == messageHeader.getMessageSource())
                    return false;   // If the source codes are the same object, don't echo!
            Object[][] mxMessageHeader = messageHeader.getNameValueTree();
            Object[][] mxMessageFilter = this.getNameValueTree();
            bMatch = this.compareTrees(mxMessageHeader, mxMessageFilter, false);
            if (bMatch)
                if (messageHeader.getRegistryIDMatch() != null)
                    if (!messageHeader.getRegistryIDMatch().equals(this.getRegistryID()))
                        bMatch = false;
        }
        return bMatch;    // MATCH!!!
    }
    /**
     * Compare these two name/value trees.
     * @param mxMessageHeader The header name value tree.
     * @param mxMessageFilter The filter name value tree.
     * @param bFullTree If true, the trees have to match exactly, if false all the entries in the filter must match the starting entries in the header.
     */
    public boolean compareTrees(Object[][] mxMessageHeader, Object[][] mxMessageFilter, boolean bFullTree)
    {
        if (mxMessageHeader == null)
            mxMessageHeader = EMPTY_MATRIX;
        if (mxMessageFilter == null)
            mxMessageFilter = EMPTY_MATRIX;
        if (mxMessageFilter.length != mxMessageHeader.length)
            if (bFullTree)
                return false;    // Different sizes = unequal
        if (mxMessageFilter.length > mxMessageHeader.length)
            return false;    // Not enough entries in the header = unequal
        for (int iIndex = 0; iIndex < mxMessageFilter.length; iIndex++)
        {
            String strHeaderKey = (String)mxMessageHeader[iIndex][0];
            Object objHeaderValue = mxMessageHeader[iIndex][1];
            String strFilterKey = (String)mxMessageFilter[iIndex][0];
            Object objFilterValue = mxMessageFilter[iIndex][1];
            if ((strHeaderKey == strFilterKey)
                && (objHeaderValue == objFilterValue))
                    continue;   // Match so far
            if (((strHeaderKey != null)
                && (strHeaderKey.equalsIgnoreCase(strFilterKey)))
                    && ((objHeaderValue != null)
                        && (objHeaderValue.equals(objFilterValue))))
                            continue;   // Match so far
            return false;   // No match
        }
        return true;    // Equal.
    }
    /**
     * Add this message listener to the listener list.
     * Also adds the filter to my filter list.
     * @param listener The message listener to set this filter to.
     */
    public void addMessageListener(JMessageListener listener)
    {
        if (listener == null)
            return; // Never
        this.removeDuplicateFilters(listener);  // Remove any duplicate record filters
        if (m_vListenerList == null)
            m_vListenerList = new Vector<JMessageListener>();
        for (int i = 0; i < m_vListenerList.size(); i++)
        {
            if (m_vListenerList.get(i) == listener)
            {
                Util.getLogger().warning("--------Error-Added message listener twice--------");
                return;     // I'm sure you didn't mean to do that.
            }
        }
        m_vListenerList.add(listener);
        listener.addListenerMessageFilter(this);
    }
    /**
     * If there is another RecordMessageFilter supplying messages to this listener, remove it.
     * This typically fixes the problem in TableModelSession where a grid listener is added for the session and then also when thin needs a listener
     * @param listenerToCheck
     */
    public void removeDuplicateFilters(JMessageListener listenerToCheck)
    {
        for (int iIndex = 0; ; iIndex++)
        {
            BaseMessageFilter filter = listenerToCheck.getListenerMessageFilter(iIndex);
            if (filter == null)
                break;
            if (this.isSameFilter(filter))
            {
                if (filter.getMessageListener(1) == null)
                {   // Only free if the message list is the same and only
                    filter.removeFilterMessageListener(listenerToCheck);
                // DO NOT free the listener.
                    filter.free();     // Cool, it was a dup... get rid of it.
                    iIndex--;
                }
            }
        }
    }
    /**
     * Are these filters functionally the same?
     * Override this to compare filters.
     * @return true if they are.
     */
    public boolean isSameFilter(BaseMessageFilter filter)
    {
        if (filter.getClass().equals(this.getClass()))
        {
            if (filter.isFilterMatch(this))
                ;   // return true;? todo (don) You need to figure out how to compare filters
        }
        return false;
    }
    /**
     * Set the message listener.
     * @param listener The message listener to set this filter to.
     */
    public void removeFilterMessageListener(JMessageListener listener)
    {
        if (listener == null)
            return; // Never
        if (m_vListenerList == null)
            return; // Never
        for (int i = 0; i < m_vListenerList.size(); i++)
        {
            if (m_vListenerList.get(i) == listener)
                m_vListenerList.remove(i);   // Does this work?
        }
        listener.removeListenerMessageFilter(this);
    }
    /**
     * Get the message listener for this filter.
     * @return The listener.
     */
    public JMessageListener getMessageListener(int iIndex)
    {
        if (m_vListenerList == null)
            return null;
        if (iIndex >= m_vListenerList.size())
            return null;
        return m_vListenerList.get(iIndex);
    }
    /**
     * Set the message receiver for this filter.
     * @param messageReceiver The message receiver.
     */
    public void setMessageReceiver(BaseMessageReceiver messageReceiver, Integer intID)
    {
        if ((messageReceiver != null) || (intID != null))
            if ((m_intID != null) || (m_messageReceiver != null))
                Util.getLogger().warning("BaseMessageFilter/285----Error - Filter added twice.");
        m_messageReceiver = messageReceiver;
        m_intID = intID;
    }
    /**
     * Get the message receiver for this filter.
     * @return The message receiver.
     */
    public BaseMessageReceiver getMessageReceiver()
    {
        return m_messageReceiver;
    }
    /**
     * Try to figure out the remote session that this filter belongs to.
     * Override this as this implementation is empty!
     * @return The remote session.
     */
    public Object getRemoteSession()
    {
        return null;
    }
    /**
     * Link this filter to this remote session.
     * This is ONLY used in the server (remote) version of a filter.
     * Override this to finish setting up the filter (such as behavior to adjust this filter).
     * Or if the server version of this filter is different, you have a chance to change the filter.
     * @param remoteSession The remote session to link/set.
     */
    public BaseMessageFilter linkRemoteSession(Object remoteSession)
    {
        return this;    // Don't change the filter.
    }
    /**
     * Do I send this type of message to the remote server?
     * @return true If I do (default).
     */
    public boolean isSendRemoteMessage(BaseMessage message)
    {
        return true;
    }
    /**
     * Is this the remote version of a filter?
     * (It is a remote filter if the listener is a remote session).
     * @return true if this is the remote version of a filter.
     */
    public boolean isRemoteFilter()
    {
        for (int i = 0; (this.getMessageListener(i) != null); i++)
        {
            if (this.getMessageListener(i) instanceof java.rmi.server.UnicastRemoteObject)
                return true;
        }
        return false;
    }
    /**
     * Update this object's filter with this new map information.
     * @param propFilter New filter information (ie, bookmark=345) pass null to sync the local and remote filters.
     */
    public final void updateFilterMap(Map<String,Object> propFilter)
    {
        if (propFilter == null)
            propFilter = new HashMap<String,Object>();     // Then handleUpdateFilterMap can modify the filter
        propFilter = this.handleUpdateFilterMap(propFilter);   // Update this object's local filter.
        this.setFilterMap(propFilter);            // Update any remote copy of this.
    }
    /**
     * Update this object's filter with this new tree information.
     * @param propTree Changes to the current property tree.
     */
    public final void updateFilterTree(Map<String, Object> propTree)
    {
        Object[][] mxProperties = this.cloneMatrix(this.getNameValueTree());
        mxProperties = this.createNameValueTree(mxProperties, propTree);  // Update these properties
        this.setFilterTree(mxProperties);   // Update any remote copy of this.
    }
    /**
     * Update this object's filter with this new tree information.
     * @param strKey The tree key to add or change.
     * @param objValue Changes to the property tree key.
     */
    public final void updateFilterTree(String strKey, Object objValue)
    {
        Object[][] mxProperties = this.cloneMatrix(this.getNameValueTree());
        mxProperties = this.addNameValue(mxProperties, strKey, objValue); // Add/replace this property
        this.setFilterTree(mxProperties);   // Update any remote copy of this.
    }
    /**
     * Update this filter with this new map information.
     * Override this to make sure the remote version gets the current or updated filter properties.
     * Remember to call super after updating this filter to run any of the inherited methods.
     * @param propFilter New filter information (ie, bookmark=345).
     * @return The new filter change map (must contain enough information for the remote filter to sync).
     */
    public Map<String,Object> handleUpdateFilterMap(Map<String,Object> propFilter)
    {
        return propFilter;  // Override this to update the local filter.
    }
    /**
     * Update the remote filter with this new map information.
     * @param propFilter New filter information (ie, bookmark=345).
     */
    public final void setFilterMap(Map<String, Object> propFilter)
    {
        if (this.getMessageReceiver() != null)
            this.getMessageReceiver().setNewFilterProperties(this, null, propFilter);   // Update any remote copy of this.
    }
    /**
     * Update this filter and the remote filter with this new key tree.
     * @param mxProperties New tree filter.
     */
    public final void setFilterTree(Object[][] mxProperties)
    {
        if (this.getMessageReceiver() != null)
            this.getMessageReceiver().setNewFilterProperties(this, mxProperties, null);   // Update any remote copy of this.
        else
        	this.setNameValueTree(mxProperties);	// Rarely called (typically during initialization - before the receiver is set up)
    }
    /**
     * Should I Create the remote filter properties?
     * @return true if you should update the remote filter properties.
     */
    public boolean isCreateRemoteFilter()
    {
        return m_bCreateRemoteFilter;
    }
    /**
     * Should I create the remote filter properties?
     * @return true if you should update the remote filter properties.
     */
    public void setCreateRemoteFilter(boolean bCreateRemoteFilter)
    {
        m_bCreateRemoteFilter = bCreateRemoteFilter;
        if (!bCreateRemoteFilter)
            this.setUpdateRemoteFilter(bCreateRemoteFilter);    // Don't update if you don't create.
    }
    /**
     * Should I update the remote filter properties?
     * @return true if you should update the remote filter properties.
     */
    public boolean isUpdateRemoteFilter()
    {
        return m_bUpdateRemoteFilter;
    }
    /**
     * Should I update the remote filter properties?
     * @return true if you should update the remote filter properties.
     */
    public void setUpdateRemoteFilter(boolean bUpdateRemoteFilter)
    {
        m_bUpdateRemoteFilter = bUpdateRemoteFilter;
    }
    /**
     * Is this filter owned by a thin client?
     * A ClientSessionMessage filter is ALWAYS owned by a thin client!
     * @return true if the client is thin.
     */
    public boolean isThinTarget()
    {
        return m_bThinTarget;
    }
    /**
     * Is this filter owned by a thin client?
     * A ClientSessionMessage filter is ALWAYS owned by a thin client!
     * @return true if the client is thin.
     */
    public void setThinTarget(boolean bThinTarget)
    {
        m_bThinTarget = bThinTarget;
    }
}
