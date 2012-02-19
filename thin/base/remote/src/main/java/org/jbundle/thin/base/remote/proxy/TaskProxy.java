/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */
import java.util.HashMap;
import java.util.Map;

import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.RemoteReceiveQueue;
import org.jbundle.thin.base.remote.RemoteSendQueue;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class TaskProxy extends BaseSessionProxy
    implements RemoteTask, ProxyConstants
{
    /**
     * List of "remote" tasks.
     */
    protected HashMap<String,DatabaseProxy> m_hmDatabaseList = new HashMap<String,DatabaseProxy>();

    /**
     * Constructor.
     */
    public TaskProxy()
    {
        super();
    }
    /**
     * Constructor.
     * This constructor auto-sends the task proxy construction info.
     * @throws RemoteException TODO
     */
    public TaskProxy(ApplicationProxy parentProxy, Map<String,Object> properties) throws RemoteException
    {
        this();
        BaseTransport transport = parentProxy.createProxyTransport(CREATE_REMOTE_TASK); // Don't use my method yet, since I don't have the returned ID
        transport.addParam(PROPERTIES, properties);
        String strID = (String)transport.sendMessageAndGetReply();
        this.init(parentProxy, strID);
    }
    /**
     * Alternate Constructor.
     * If I have already send the create task call.
     */
    public TaskProxy(ApplicationProxy parentProxy, String strID)
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
    /**
     *
     */
    public void addChildProxy(DatabaseProxy dbProxy)
    {
        String strRemoteDatabaseID = dbProxy.getID();
        m_hmDatabaseList.put(strRemoteDatabaseID, dbProxy);
    }
//------------------------------Remote Implementation---------------------------------
    /**
     * Make a table for this database.
     * @param strRecordClassName The record class name.
     * @param strTableSessionClassName The (optional) session name for the table.
     * @param properties The properties for the remote table.
     */
    public RemoteTable makeRemoteTable(String strRecordClassName, String strTableSessionClassName, Map<String, Object> properties, Map<String, Object> propDatabase)
        throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(MAKE_REMOTE_TABLE);
        transport.addParam(NAME, strRecordClassName);
        transport.addParam(SESSION_CLASS_NAME, strTableSessionClassName);
        transport.addParam(PROPERTIES, properties);
        transport.addParam(PROPERTIES_DB, propDatabase);
        String strTableID = (String)transport.sendMessageAndGetReply();
        // See if I have this one already
        TableProxy tableProxy = null;
//x        tableProxy = (TableProxy)this.getChildList().get(strTableID);
        if (tableProxy == null)
            tableProxy = new TableProxy(this, strTableID); // This will add it to my list
        return tableProxy;
    }
    /**
     * Build a new remote session and initialize it.
     * NOTE: This is convenience method to create a task below the current APPLICATION (not below this task)
     * @param properties to create the new remote task
     * @return The remote Task.
     */
    public RemoteTask createRemoteTask(Map<String, Object> properties) throws RemoteException
    {   // Note: This new task's parent is MY application!
        BaseTransport transport = this.createProxyTransport(CREATE_REMOTE_TASK); // Don't use my method yet, since I don't have the returned ID
        transport.addParam(PROPERTIES, properties);
        String strID = (String)transport.sendMessageAndGetReply();
        return new TaskProxy((ApplicationProxy)m_parentProxy, strID);   // Note the parent is MY PARENT not ME. (just like the remote hierarchy)
    }
    /**
     * Create a new remote receive queue.
     * @param strQueueName The queue name.
     * @param strQueueType The queue type (see MessageConstants).
     */
    public RemoteReceiveQueue createRemoteReceiveQueue(String strQueueName, String strQueueType) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(CREATE_REMOTE_RECEIVE_QUEUE);
        transport.addParam(MessageConstants.QUEUE_NAME, strQueueName);
        transport.addParam(MessageConstants.QUEUE_TYPE, strQueueType);
        String strID = (String)transport.sendMessageAndGetReply();
        return new ReceiveQueueProxy(this, strID);
    }    
    /**
     * Create a new remote send queue.
     * @param strQueueName The queue name.
     * @param strQueueType The queue type (see MessageConstants).
     */
    public RemoteSendQueue createRemoteSendQueue(String strQueueName, String strQueueType) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(CREATE_REMOTE_SEND_QUEUE);
        transport.addParam(MessageConstants.QUEUE_NAME, strQueueName);
        transport.addParam(MessageConstants.QUEUE_TYPE, strQueueType);
        String strID = (String)transport.sendMessageAndGetReply();
        return new SendQueueProxy(this, strID);
    }
    /**
     * Set the task that handles messages for this remote session.
     * This is optional, it keeps remote file messages from being send to the client.
     * If you set this, messages generated by remote ClientTable calls will not be sent back up
     * to their client (by setting this remotetask as the client filter).
     * @param messageTask The remote message task that handles messages for this remote task.
     */
    public void setRemoteMessageTask(RemoteTask messageTaskPeer) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(SET_REMOTE_MESSAGE_TASK);
        String strTaskID = null;
        if (messageTaskPeer instanceof BaseProxy)
        {   // Always a TaskProxy
            strTaskID = ((BaseProxy)messageTaskPeer).getIDPath();
        }
        transport.addParam(ID, strTaskID);
        transport.sendMessageAndGetReply();
    }
    /**
     * Log this task in under this (new) user.
     * @param strUserName The user to log this task in under
     * @param strPassword The password (NOTE: this is encrypted - do not send clear text).
     * @exception Throw an exception if logon is not successful.
     * @return The security map for this user
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> login(String strUserName, String strPassword, String strDomain) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(LOGIN);
        transport.addParam(Params.USER_NAME, strUserName);
        transport.addParam(Params.PASSWORD, strPassword);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return (Map)this.checkRemoteException(objReturn);
    }
    /**
     * 
     */
    public DatabaseProxy getDatabaseProxy(String strID)
    {
        return m_hmDatabaseList.get(strID);
    }
}
