/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message;


/**
 * A JMessageListener is an interface for classes that handle incoming messages.
 */
public interface JMessageListener
{
    /**
     * Handle this message.
     */
    public int handleMessage(BaseMessage message);
    /**
     * Have the message listener add this filter to its list.
     * NOTE: DO NOT call this method, this method is auto-called when you do filter.addMessageListener(listener);
     * @param messageFilter The message filter to add.
     */
    public void addListenerMessageFilter(BaseMessageFilter messageFilter);
    /**
     * Have the message listener remove this filter from its list.
     * NOTE: DO NOT call this method, this method is auto-called when you do filter.addMessageListener(listener);
     * @param messageFilter The message filter to remove.
     */
    public void removeListenerMessageFilter(BaseMessageFilter messageFilter);
    /**
     * Have the message listener remove this filter from its list.
     * NOTE: DO NOT call this method, this method is auto-called when you do filter.addMessageListener(listener);
     * @param messageFilter The message filter to remove.
     */
    public BaseMessageFilter getListenerMessageFilter(int iIndex);
    /**
     * Is this listener going to send its messages to a thin client?
     * @return true if yes.
     */
    public boolean isThinListener();
}
