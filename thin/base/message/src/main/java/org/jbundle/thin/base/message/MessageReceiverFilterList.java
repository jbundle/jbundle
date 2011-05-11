package org.jbundle.thin.base.message;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jbundle.model.Freeable;
import org.jbundle.model.util.Util;


/**
 * The base MessageReceiver processes incoming messages.
 * The thread hangs waiting for the next message. When a message arrives,
 * the message is sent to all filters that match this message type.
 */
public class MessageReceiverFilterList extends Object
{
    /**
     * My parent message receiver.
     */
    protected BaseMessageReceiver m_receiver = null;
    /**
     * My list of filters (which contain the listener to forward the messages to). Supports concurrent updates to the map.
     */
    protected ConcurrentMap<Integer,BaseMessageFilter> m_mapFilters = null;
    /**
     * The next number to file a filter under.
     */
    protected Integer m_intNext = null;

    /**
     * Constructor.
     */
    public MessageReceiverFilterList()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageReceiverFilterList(BaseMessageReceiver receiver)
    {
        this();
        this.init(receiver);
    }
    /**
     * Constructor.
     * @param baseMessageQueue My parent message queue.
     */
    public void init(BaseMessageReceiver receiver)
    {
        m_receiver = receiver;
        m_mapFilters = new ConcurrentHashMap<Integer,BaseMessageFilter>();
        m_intNext = new Integer(1);
    }
    /**
     * Free this object.
     * Note: This doesn't stop the thread, you need to do that yourself in the overidding
     * class by sending a "stop" message.
     */
    public void free()
    {
        this.freeFiltersWithListener(null);
        if (m_mapFilters.keySet().size() != 0)
            Util.getLogger().warning("Error BaseMessageReceiver.free");   // Shouldn't be any left
        m_mapFilters = null;
        m_intNext = null;
        m_receiver = null;
    }
    /**
     * Get the parent message receiver.
     * @return The message receiver.
     */
    public BaseMessageReceiver getMessageReceiver()
    {
        return m_receiver;
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed (pass null to free them all).
     */
    public void freeFiltersWithListener(JMessageListener listener)
    {
        Object[] rgFilter = m_mapFilters.values().toArray();
        for (int i = 0; i < rgFilter.length; i++)
        {
            BaseMessageFilter filter = (BaseMessageFilter)rgFilter[i];
            for (int j = 0; (filter.getMessageListener(j) != null); j++)
            {
                if ((filter.getMessageListener(j) == listener)
                    || (listener == null))
                        filter.free();
            }
        }
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed (pass null to free them all).
     */
    public void freeFiltersWithSource(Object objSource)
    {
        Object[] rgFilter = m_mapFilters.values().toArray();
        for (int i = 0; i < rgFilter.length; i++)
        {
            BaseMessageFilter filter = (BaseMessageFilter)rgFilter[i];
            if (filter.getMessageSource() == objSource)
                filter.free();
        }
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed (pass null to free them all).
     */
    public void freeListenersWithSource(Object objSource)
    {
        Object[] rgFilter = m_mapFilters.values().toArray();
        for (int i = 0; i < rgFilter.length; i++)
        {
            BaseMessageFilter filter = (BaseMessageFilter)rgFilter[i];
            if (filter.getMessageSource() == objSource)
            {
                while (filter.getMessageListener(0) != null)
                {
                    JMessageListener listener = filter.getMessageListener(0);
                    filter.removeFilterMessageListener(listener);
                    if (listener instanceof Freeable)
                        ((Freeable)listener).free();
                }                
            }
        }
    }
    /**
     * Add this message filter to this receive queue.
     * @param The message filter to add.
     */
    public void addMessageFilter(BaseMessageFilter messageFilter)
    {
        if (messageFilter == null)
            return;
        m_mapFilters.put(m_intNext, messageFilter);
        messageFilter.setMessageReceiver(this.getMessageReceiver(), m_intNext);
        int i = m_intNext.intValue();
        if (i == Integer.MAX_VALUE)
            i = 0;
        m_intNext = new Integer(i + 1);
    }
    /**
     * Remove this message filter from this queue.
     * Note: This will remove a filter that equals this filter, accounting for a copy
     * passed from a remote client.
     * @param messageFilter The message filter to remove.
     * @param bFreeFilter If true, free this filter.
     * @return True if successful.
     */
    public boolean removeMessageFilter(Integer intFilterID, boolean bFreeFilter)
    {
        BaseMessageFilter filter = (BaseMessageFilter)m_mapFilters.remove(intFilterID);
        if (filter == null)
        {
            System.out.println("Error: BaseMessageReceiver.removeMessageFilter");
            return false;
        }
        filter.setMessageReceiver(null, null);        // Make sure free doesn't try to remove filter again.
        if (bFreeFilter)
            filter.free();
        return true;    // Success.
    }
    /**
     * Update this filter with this new information.
     * @param messageFilter The message filter I am updating.
     * @param propKeys New tree key filter information (ie, bookmark=345).
     */
    public void setNewFilterTree(BaseMessageFilter messageFilter, Object[][] mxProperties)
    {
        if (mxProperties != null)
            messageFilter.setNameValueTree(mxProperties);  // Update the new properties.
    }
    /**
     * Remove this message filter from this queue.
     * Note: This will remove a filter that equals this filter, accounting for a copy
     * passed from a remote client.
     * @param messageFilter The message filter to remove.
     * @param bFreeFilter If true, free this filter.
     * @return True if successful.
     */
    public BaseMessageFilter getMessageFilter(Integer intFilterID)
    {
        return (BaseMessageFilter)m_mapFilters.get(intFilterID);
    }
    /**
     * Lookup this message listener.
     * This message looks through my list to see if the listener's filter is there.
     * @param listener The listener to find.
     * @return The filter for this listener (or null if not found).
     */
    public BaseMessageFilter findMessageFilter(JMessageListener listener)
    {
        Iterator<BaseMessageFilter> iterator = m_mapFilters.values().iterator();
        while (iterator.hasNext())
        {
            BaseMessageFilter filter = iterator.next();
            for (int i = 0; (filter.getMessageListener(i) != null); i++)
            {
                if (filter.getMessageListener(i) == listener)
                    return filter;
            }
        }
        return null;    // Error, not found.
    }
    /**
     * Get the list of filters for this message header.
     * Override this to implement another (tree?) filter.
     * @param messageHeader The message header to get the list for.
     * @return The list of filters.
     */
    public Iterator<BaseMessageFilter> getFilterList(BaseMessageHeader messageHeader)
    {
        return m_mapFilters.values().iterator();
    }
}
