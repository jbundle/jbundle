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

/**
 * A Remote session.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public interface RemoteSession extends RemoteBaseSession
{
    /**
     * Get this table for this session.
     * @param strRecordName Table Name or Class Name of the record to find
     * @throws RemoteException TODO
     */
    public RemoteTable getRemoteTable(String strRecordName) throws RemoteException;
    /**
     * Link the filter to this remote session.
     * This is a special method that is needed because the remote link is passed a remote reference to the session
     * even though it is in the same JVM. What you need to do in your implementation is lookup the message filter
     * and call messageFilter.linkRemoteSession(this); See RemoteSession Object for the Only implementation.
     * @param messageFilter A serialized copy of the messageFilter to link this session to.
     * @throws RemoteException TODO
     */
    public org.jbundle.thin.base.message.BaseMessageFilter setupRemoteSessionFilter(org.jbundle.thin.base.message.BaseMessageFilter messageFilter) throws RemoteException;
}
