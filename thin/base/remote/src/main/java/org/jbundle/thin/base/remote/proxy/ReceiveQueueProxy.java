/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.remote.RemoteReceiveQueue;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;


/**
 *  ReceiveQueueProxy - Receive message proxy.
 * pend(don) Need to keep retrying if timeout as some firewalls won't wait forever.
 */
public class ReceiveQueueProxy extends BaseSessionProxy
    implements RemoteReceiveQueue
{
    /**
     * Constructor.
     */
    public ReceiveQueueProxy()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ReceiveQueueProxy(BaseProxy parentProxy, String strID)
    {
        this();
        this.init(parentProxy, strID);
    }
    /**
     * Constructor.
     */
    public void init(BaseProxy parentProxy, String strID)
    {
        super.init(parentProxy, strID);
    }
    /**
     * Create the proxy transport.
     * @param strCommand The command.
     * @return The transport.
     */
    public BaseTransport createProxyTransport(String strCommand)
    {
        BaseTransport transport = super.createProxyTransport(strCommand);
        return transport;
    }
//------------------------------Remote Implementation---------------------------------    
    /**
     * Receive the next remote message.
     * pend(don) Need to keep retrying if timeout as some firewalls won't wait forever.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     */
    public BaseMessage receiveRemoteMessage() throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(RECEIVE_REMOTE_MESSAGE);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return (BaseMessage)objReturn;
    }
    /**
     * Add a message filter to this remote receive queue.
     * @param messageFilter The message filter to add.
     * @param remoteSession The remote session.
     * @return The filter ID.
     */
    public BaseMessageFilter addRemoteMessageFilter(BaseMessageFilter messageFilter, RemoteSession remoteSession) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(ADD_REMOTE_MESSAGE_FILTER);
        transport.addParam(FILTER, messageFilter);   // Don't use COMMAND
        String strSessionPathID = null;
        if (remoteSession instanceof BaseProxy)
        {   // Always a SessionProxy
            strSessionPathID = ((BaseProxy)remoteSession).getIDPath();
        }
        transport.addParam(SESSION, strSessionPathID);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return (BaseMessageFilter)objReturn;
    }
    /**
     * Remove this remote message filter.
     * @param messageFilter The message filter to remove.
     * @param bFreeFilter If true, free the remote filter.
     */
    public boolean removeRemoteMessageFilter(BaseMessageFilter messageFilter, boolean bFreeFilter) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(REMOVE_REMOTE_MESSAGE_FILTER);
        transport.addParam(FILTER, messageFilter);   // Don't use COMMAND
        transport.addParam(FREE, bFreeFilter);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        if (objReturn instanceof Boolean)
            return ((Boolean)objReturn).booleanValue();
        return true;
    }
    /**
     * Update this filter with this new information.
     * @param messageFilter The message filter I am updating.
     * @param properties New filter information (ie, bookmark=345).
     */
    public void updateRemoteFilterProperties(BaseMessageFilter messageFilter, Object[][] properties, Map<String,Object> propFilter) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(REMOVE_REMOTE_MESSAGE_FILTER);
        transport.addParam(FILTER, messageFilter);   // Don't use COMMAND
        transport.addParam(PROPERTIES, properties);
        transport.addParam(MAP, propFilter);
        transport.sendMessageAndGetReply();
    }
}
