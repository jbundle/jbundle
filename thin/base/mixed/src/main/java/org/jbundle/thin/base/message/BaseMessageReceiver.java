package org.jbundle.thin.base.message;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;

import org.jbundle.model.message.MessageFilter;
import org.jbundle.model.message.MessageReceiver;
import org.jbundle.thin.base.db.Constant;
import org.jbundle.thin.base.remote.RemoteReceiveQueue;
import org.jbundle.thin.base.util.Util;


/**
 * The base MessageReceiver processes incoming messages.
 * The thread hangs waiting for the next message. When a message arrives,
 * the message is sent to all filters that match this message type.
 */
public abstract class BaseMessageReceiver extends Thread
	implements MessageReceiver
{
    /**
     * My parent message queue.
     */
    protected BaseMessageQueue m_baseMessageQueue = null;
    /**
     * My list of filters (which contain the listener to forward the messages to).
     */
    protected MessageReceiverFilterList m_filterList = null;
    /**
     * This thread.
     */
    protected BaseMessageReceiver m_thread = null;

    /**
     * Constructor.
     */
    public BaseMessageReceiver()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseMessageReceiver(BaseMessageQueue baseMessageQueue)
    {
        this();
        this.init(baseMessageQueue);
    }
    /**
     * Constructor.
     * @param baseMessageQueue My parent message queue.
     */
    public void init(BaseMessageQueue baseMessageQueue)
    {
        m_baseMessageQueue = baseMessageQueue;
        m_thread = null;
    }
    /**
     * Free this object.
     * Note: This doesn't stop the thread, you need to do that yourself in the overidding
     * class by sending a "stop" message.
     */
    public void free()
    {
        this.stopThisThread();
        this.freeFiltersWithListener(null);
        if (m_filterList != null)
            m_filterList.free();
        m_filterList = null;
        m_baseMessageQueue = null;
    }
    /**
     * Stop this thread.
     */
    public void stopThisThread()
    {
        if (m_thread != null)
            m_thread.interrupt();   // This will cause a null message to be send and will stop this thread
        m_thread = null;     // Next, you need to send a message to this receiver in the overriding class.
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed (pass null to free them all).
     */
    public void freeFiltersWithListener(JMessageListener listener)
    {
        this.getMessageFilterList().freeFiltersWithListener(listener);
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed (pass null to free them all).
     */
    public void freeFiltersWithSource(Object objSource)
    {
        this.getMessageFilterList().freeFiltersWithSource(objSource);
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed (pass null to free them all).
     */
    public void freeListenersWithSource(Object objSource)
    {
        this.getMessageFilterList().freeListenersWithSource(objSource);
    }
    /**
     * Get the message filter list.
     * Override this to provide a different message filter list (such as a tree filter).
     * Create a new filter list the first time.
     * @return The filter list.
     */
    public MessageReceiverFilterList getMessageFilterList()
    {
        if (m_filterList == null)
            m_filterList = new MessageReceiverFilterList(this);
        return m_filterList;
    }
    /**
     * Add this message filter to this receive queue.
     * @param The message filter to add.
     */
    public void addMessageFilter(MessageFilter messageFilter)
    {
        this.getMessageFilterList().addMessageFilter((BaseMessageFilter)messageFilter);
    }
    /**
     * Remove this message filter from this queue.
     * Note: This will remove a filter that equals this filter, accounting for a copy
     * passed from a remote client.
     * @param messageFilter The message filter to remove.
     * @param bFreeFilter If true, free this filter.
     * @return True if successful.
     */
    public boolean removeMessageFilter(MessageFilter messageFilter, boolean bFreeFilter)
    {
        return this.removeMessageFilter(((BaseMessageFilter)messageFilter).getFilterID(), bFreeFilter);
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
        return this.getMessageFilterList().removeMessageFilter(intFilterID, bFreeFilter);
    }
    /**
     * Update this filter with this new information.
     * Override this to do something if there is a remote version of this filter.
     * @param messageFilter The message filter I am updating.
     * @param propKeys New key filter information (ie, bookmark=345).
     * @param propFilter New filter information.
     */
    public void setNewFilterProperties(BaseMessageFilter messageFilter, Object[][] mxProperties, Map<String, Object> propFilter)
    {
        this.getMessageFilterList().setNewFilterTree(messageFilter, mxProperties);
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
        return this.getMessageFilterList().getMessageFilter(intFilterID);
    }
    /**
     * Lookup this message listener.
     * This message looks through my list to see if the listener's filter is there.
     * @param listener The listener to find.
     * @return The filter for this listener (or null if not found).
     */
    public BaseMessageFilter findMessageFilter(JMessageListener listener)
    {
        return this.getMessageFilterList().findMessageFilter(listener);
    }
    /**
     * Get the list of filters for this message header.
     * Override this to implement another (tree?) filter.
     * @param messageHeader The message header to get the list for.
     * @return The list of filters.
     */
    public Iterator<BaseMessageFilter> getFilterList(BaseMessageHeader messageHeader)
    {
        return this.getMessageFilterList().getFilterList(messageHeader);
    }
    /**
     * Create a message filter that will send all messages to this listener.
     * @param listener The listener to create a filter for.
     * @param bAddToReceiver Add this filter to this receiver (usually true).
     * @return The new filter for this listener.
     */
    public BaseMessageFilter createDefaultFilter(JMessageListener listener, boolean bAddToReceiver)
    {
        BaseMessageFilter messageFilter = null;
        String strQueueName = MessageConstants.RECORD_QUEUE_NAME; // Default queue name=
        String strQueueType = MessageConstants.INTRANET_QUEUE;    // Default queue type
        if (m_baseMessageQueue != null)
        {
            strQueueName = m_baseMessageQueue.getQueueName();
            strQueueType = m_baseMessageQueue.getQueueType();
        }
        messageFilter = new BaseMessageFilter(strQueueName, strQueueType, null, null);    // Take all messages
        messageFilter.addMessageListener(listener);
        if (bAddToReceiver)
            this.addMessageFilter(messageFilter);
        return messageFilter;
    }
    /**
     * Start running this thread.
     * This method hangs on receiveMessage, then processes the message if it matches
     * the filter and continues with the next message.
     */
    public void run()
    {
        m_thread = this;
        while (m_thread != null)
        {
            int iErrorCode = Constant.NORMAL_RETURN;
            BaseMessage message = this.receiveMessage();        // Hang until a message comes through
            if ((message == null) || (m_thread == null))
                return;      // End of processing, stop this thread.
            Iterator<BaseMessageFilter> iterator = this.getFilterList(message.getMessageHeader());
            while (iterator.hasNext())
            {
                if (message.isConsumed())
                    break;     // All done. with this message, continue with the next message.
                try {
                    BaseMessageFilter filter = iterator.next();
                    if (filter.isFilterMatch(message.getMessageHeader()))
                    {
                        BaseMessage messageToSend = message;
                        for (int i = 0; ; i++)
                        {
                            JMessageListener listener = filter.getMessageListener(i);
                            if (listener == null)
                                break;
                            if (filter.isThinTarget())
                                if (listener.isThinListener())
                                    messageToSend = message.convertToThinMessage(); // If you are sending a thick message to a thin client, convert it first. Note: don't worry about calling this with a thin message... no conversion will be done there.
                            if (listener instanceof RemoteReceiveQueue)
                                if (filter.getRegistryID() != null)
                                    if (messageToSend.getMessageHeader() != null)
                                        if (messageToSend.getMessageHeader().getRegistryIDMatch() == null)
                            {
                                try {
                                    if (messageToSend == message)
                                        messageToSend = (BaseMessage)messageToSend.clone(); // If I change the message, I need a new copy
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                messageToSend.getMessageHeader().setRegistryIDMatch(filter.getRegistryID());    // Typically used for remote filters to match filter ID.
                            }
                            iErrorCode = listener.handleMessage(messageToSend);
                            if (iErrorCode != Constant.NORMAL_RETURN)
                                break;
                            message.setConsumed(messageToSend.isConsumed());
                            if (message.isConsumed())
                                break;     // All done. with this message, continue with the next message.
                        }
                    }
                } catch (ConcurrentModificationException ex)    {
                    Util.getLogger().warning("Message filter Concurrent Modification");   // Warning: Someone modified the filter list while I was cruzin it.
                    iterator = this.getFilterList(message.getMessageHeader());  // Start from the beginning again
                }
            }
        }
    }
    /**
     * Get the message queue for this receiver.
     * @return My parent message queue.
     */
    public BaseMessageQueue getMessageQueue()
    {
        return m_baseMessageQueue;
    }
    /**
     * Block until a message is received.
     * You must override this abstract method.
     * @return The next message.
     */
    public abstract BaseMessage receiveMessage();
}
