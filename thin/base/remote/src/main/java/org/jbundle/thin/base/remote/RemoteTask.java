/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote;

/**
 *  RemoteTask - The interface to server objects.
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */
import java.util.Map;

/**
 *  RemoteTask - The interface to server objects.
 */
public interface RemoteTask extends RemoteBaseSession
{
    /**
     * Make a table for this database.
     * @param strRecordClassName The record class name.
     * @param strTableSessionClassName The (optional) session name for the table.
     * @param properties The properties for the remote table.
     * @param propDatabase The database properties
     * @param remoteDB The remote db do add this new table to.
     * @throws RemoteException exception
     */
    public RemoteTable makeRemoteTable(String strRecordClassName, String strTableSessionClassName, Map<String, Object> properties, Map<String, Object> propDatabase) throws RemoteException;
    /**
     * Build a new remote session and initialize it.
     * NOTE: This is convenience method to create a task below the current APPLICATION (not below this task)
     * @param properties to create the new remote task
     * @return The remote Task.
     * @throws RemoteException exception
     */
    public RemoteTask createRemoteTask(Map<String, Object> properties) throws RemoteException;
    /**
     * Create a new remote send queue.
     * @param strQueueName The queue name.
     * @param strQueueType The queue type (see MessageConstants).
     * @throws RemoteException exception
     */
    public RemoteSendQueue createRemoteSendQueue(String strQueueName, String strQueueType) throws RemoteException;
    /**
     * Create a new remote receive queue.
     * @param strQueueName The queue name.
     * @param strQueueType The queue type (see MessageConstants).
     * @throws RemoteException exception
     */
    public RemoteReceiveQueue createRemoteReceiveQueue(String strQueueName, String strQueueType) throws RemoteException;
    /**
     * Set the task that handles messages for this remote session.
     * This is optional, it keeps remote file messages from being send to the client.
     * If you set this, messages generated by remote ClientTable calls will not be sent back up
     * to their client (by setting this remotetask as the client filter).
     * @param messageTask The remote message task that handles messages for this remote task.
     * @throws RemoteException exception
     */
    public void setRemoteMessageTask(RemoteTask messageTaskPeer) throws RemoteException;
    /**
     * Log this task in under this (new) user.
     * @param strUserName The user to log this task in under
     * @param strPassword The password (NOTE: this is encrypted - do not send clear text).
     * @param strDomain domain
     * @throws RemoteException exception
     * @exception Throw an exception if logon is not successful.
     * @return The security map for this user
     */
    public Map<String,Object> login(String strUserName, String strPassword, String strDomain) throws RemoteException;
}
