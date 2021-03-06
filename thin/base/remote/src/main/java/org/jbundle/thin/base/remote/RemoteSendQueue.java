/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote;

/**
 * @(#)RemoteDatabase.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */

import org.jbundle.model.RemoteException;
import org.jbundle.model.message.Message;


/**
 * RemoteSendQueue - Send a message via the remote RMS client.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public interface RemoteSendQueue extends RemoteBaseSession
{
    /**
     * Send a remote message.
     * @param message The message to send.
     * @throws RemoteException TODO
     */
    public void sendMessage(Message message) throws RemoteException;
}
