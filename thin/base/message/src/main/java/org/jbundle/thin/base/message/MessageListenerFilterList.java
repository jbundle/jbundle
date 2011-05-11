package org.jbundle.thin.base.message;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The base MessageReceiver processes incoming messages.
 * The thread hangs waiting for the next message. When a message arrives,
 * the message is sent to all filters that match this message type.
 */
public class MessageListenerFilterList extends ArrayList<BaseMessageFilter>
{
	private static final long serialVersionUID = 1L;
	
    /**
     * My owner.
     */
    protected JMessageListener m_messageListener = null;
    
    /**
     * Constructor.
     */
    public MessageListenerFilterList()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageListenerFilterList(JMessageListener messageListener)
    {
        this();
        this.init(messageListener);
    }
    /**
     * Constructor.
     */
    public void init(JMessageListener messageListener)
    {
        m_messageListener = messageListener;
    }
    /**
     * Free.
     */
    public void free()
    {
        Iterator<BaseMessageFilter> iterator = this.iterator();
        while (iterator.hasNext())
        {
            BaseMessageFilter messageFilter = iterator.next();
            boolean bFilterOwner = false;
            if (messageFilter.getMessageListener(0) == this)
                bFilterOwner = true;
            messageFilter.removeFilterMessageListener(m_messageListener);
            if ((messageFilter.getMessageListener(0) == null)
                || (bFilterOwner))
                    messageFilter.free();
            iterator = this.iterator(); // Since I modified the iterator in this method.
        }
        this.clear();
        m_messageListener = null;
    }
    /**
     * Add this message filter to my list.
     */
    public void addMessageFilter(BaseMessageFilter messageFilter)
    {
        this.add(messageFilter);
    }
    /**
     * Remove this message filter from my list.
     */
    public void removeMessageFilter(BaseMessageFilter messageFilter)
    {
        this.remove(messageFilter);
    }
    /**
     * Have the message listener remove this filter from its list.
     * NOTE: DO NOT call this method, this method is auto-called when you do filter.addMessageListener(listener);
     * @param messageFilter The message filter to remove.
     */
    public BaseMessageFilter getMessageFilter(int iIndex)
    {
        if (iIndex >= this.size())
            return null;
        return this.get(iIndex);
    }
}
