/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote;

/**
 * @(#)RemoteDatabase.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.model.RemoteException;
import org.jbundle.model.RemoteTarget;


/**
 * A Remote session.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public interface RemoteBaseSession extends RemoteTarget
{
    /**
     * Release the session and its resources.
     * @throws RemoteException 
     */
    public void freeRemoteSession() throws RemoteException;
    /**
     * Build a new remote session and initialize it.
     * @param parentSessionObject The parent session for this new session (if null, parent = me).
     * @param strSessionClassName The class name of the remote session to build.
     * @throws RemoteException 
     */
    public RemoteBaseSession makeRemoteSession(String strSessionClassName) throws RemoteException;
}
