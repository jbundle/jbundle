/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote;

/**
 * @(#)RemoteDatabase.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.model.message.Message;
import org.jbundle.thin.base.message.BaseMessageFilter;

/**
 * RemoteReceiveQueue - Receive incoming messages from the (remote) RMS client.
 * Note: This call blocks until a message is received, so set it up in an independent task.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public interface RemoteReceiveQueue extends RemoteBaseSession
{
    /**
     * Do a remote action.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     * @throws RemoteException TODO
     */
    public Message receiveRemoteMessage() throws RemoteException;
    /**
     * Add a message filter to this remote receive queue.
     * @param messageFilter The message filter to add.
     * @param remoteSession The remote session.
     * @return The filter ID.
     * @throws RemoteException TODO
     */
    public BaseMessageFilter addRemoteMessageFilter(BaseMessageFilter messageFilter, RemoteSession remoteSession) throws RemoteException;
    /**
     * Remove this remote message filter.
     * @param messageFilter The message filter to remove.
     * @param bFreeFilter If true, free the remote filter.
     * @throws RemoteException TODO
     */
    public boolean removeRemoteMessageFilter(BaseMessageFilter messageFilter, boolean bFreeFilter) throws RemoteException;
    /**
     * Update this filter with this new information.
     * @param messageFilter The message filter I am updating.
     * @param properties New filter information (ie, bookmark=345).
     * @throws RemoteException TODO
     */
    public void updateRemoteFilterProperties(BaseMessageFilter messageFilter, Object[][] properties, Map<String,Object> propFilter) throws RemoteException;
}
