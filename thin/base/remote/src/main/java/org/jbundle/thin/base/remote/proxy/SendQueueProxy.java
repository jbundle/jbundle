/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.rmi.RemoteException;

import org.jbundle.model.message.Message;
import org.jbundle.thin.base.remote.RemoteSendQueue;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class SendQueueProxy extends BaseSessionProxy
    implements RemoteSendQueue
{
    /**
     * Constructor.
     */
    public SendQueueProxy()
    {
        super();
    }
    /**
     * Constructor.
     */
    public SendQueueProxy(BaseProxy parentProxy, String strID)
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
     * Send a remote message.
     * @param message The message to send.
     */
    public void sendMessage(Message message) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(SEND_MESSAGE);
        transport.addParam(MESSAGE, message);   // Don't use COMMAND
        transport.sendMessageAndGetReply();
    }
    
}
