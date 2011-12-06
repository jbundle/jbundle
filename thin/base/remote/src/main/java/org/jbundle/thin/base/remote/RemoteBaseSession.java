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

import org.jbundle.model.DBException;


/**
 * A Remote session.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public interface RemoteBaseSession
{
    /**
     * Release the session and its resources.
     * @throws RemoteException TODO
     */
    public void freeRemoteSession() throws RemoteException;
    /**
     * Build a new remote session and initialize it.
     * @param parentSessionObject The parent session for this new session (if null, parent = me).
     * @param strSessionClassName The class name of the remote session to build.
     * @throws RemoteException TODO
     */
    public RemoteBaseSession makeRemoteSession(String strSessionClassName) throws RemoteException;
    /**
     * Do a remote action.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     * @throws RemoteException TODO
     */
    public Object doRemoteAction(String strCommand, Map<String, Object> properties) throws DBException, RemoteException;
}
