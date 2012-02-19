/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.core.tree;

import java.util.Iterator;

import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MessageReceiverFilterList;

/**
 * A TreeMessageFilterList files filters in a tree for quick access.
 */
public class TreeMessageFilterList extends MessageReceiverFilterList
{
    /**
     * The node registry for this receiver.
     */
    protected Registry m_registry = null;

    /**
     * Default constructor.
     */
    public TreeMessageFilterList()
    {
        super();
    }
    /**
     * Default constructor.
     * @param receiver My parent receiver.
     */
    public TreeMessageFilterList(BaseMessageReceiver receiver)
    {
        this();
        this.init(receiver);    // The one and only
    }
    /**
     * Default constructor.
     * @param receiver My parent receiver.
     */
    public void init(BaseMessageReceiver receiver)
    {
        super.init(receiver);
    }
    /**
     * Free all the resources belonging to this filter list.
     */
    public void free()
    {
        if (m_registry != null)
            m_registry.free();
        m_registry = null;
        super.free();
    }
    /**
     * Add this message filter to this receive queue.
     * @param The message filter to add.
     */
    public void addMessageFilter(BaseMessageFilter messageFilter)
    {
        super.addMessageFilter(messageFilter);
        this.getRegistry().addMessageFilter(messageFilter);
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
        BaseMessageFilter messageFilter = this.getMessageFilter(intFilterID);
        if (messageFilter == null)
            return false;   // It is possible for a client to try to remove it's remote filter that was removed as a duplicate filter.
        boolean bRemoved = this.getRegistry().removeMessageFilter(messageFilter);
        if (!bRemoved)
            System.out.println("Filter not found on remove");
        bRemoved = super.removeMessageFilter(intFilterID, bFreeFilter);
        return bRemoved;    // Success.
    }
    /**
     * Update this filter with this new information.
     * @param messageFilter The message filter I am updating.
     * @param propKeys New tree key filter information (ie, bookmark=345).
     */
    public void setNewFilterTree(BaseMessageFilter messageFilter, Object[][] propKeys)
    {
        if (propKeys != null)
            if (!propKeys.equals(messageFilter.getNameValueTree()))
        {   // This moves the filter to the new leaf
            this.getRegistry().removeMessageFilter(messageFilter);
            super.setNewFilterTree(messageFilter, propKeys);
            this.getRegistry().addMessageFilter(messageFilter);
        }
    }
    /**
     * Get the list of filters for this message header.
     * Override this to implement another (tree?) filter.
     * @param messageHeader The message header to get the list for (if null, return ALL the filters).
     * @return The list of filters.
     */
    public Iterator<BaseMessageFilter> getFilterList(BaseMessageHeader messageHeader)
    {
        if (messageHeader == null)
            return super.getFilterList(messageHeader);  // ALL The filters
        return this.getRegistry().getFilterList(messageHeader);
    }
    /**
     * Get the filter node.
     * @return The node registry.
     */
    public Registry getRegistry()
    {
        if (m_registry == null)
            m_registry = new Registry(this);
        return m_registry;
    }
}
