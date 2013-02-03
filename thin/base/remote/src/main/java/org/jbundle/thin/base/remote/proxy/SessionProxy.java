/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */

import org.jbundle.model.RemoteException;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class SessionProxy extends BaseSessionProxy
    implements RemoteSession
{
    /**
     * Constructor.
     */
    public SessionProxy()
    {
        super();
    }
    /**
     * Constructor.
     */
    public SessionProxy(BaseProxy parentProxy, String strID)
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
     * Get this table for this session.
     * @param strRecordName Table Name or Class Name of the record to find
     */
    public RemoteTable getRemoteTable(String strRecordName) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(GET_REMOTE_TABLE);
        transport.addParam(NAME, strRecordName);
        String strTableID = (String)transport.sendMessageAndGetReply();
        // See if I have this one already
        TableProxy tableProxy = (TableProxy)this.getChildList().get(strTableID);
        if (tableProxy == null)
            tableProxy = new TableProxy(this, strTableID); // This will add it to my list
        return tableProxy;
    }
    /** Link the filter to this remote session.
     * This is a special method that is needed because the remote link is passed a remote reference to the session
     * even though it is in the same JVM. What you need to do in your implementation is lookup the message filter
     * and call messageFilter.linkRemoteSession(this); See RemoteSession Object for the Only implementation.
     * @param messageFilter A serialized copy of the messageFilter to link this session to.
     */
    public org.jbundle.thin.base.message.BaseMessageFilter setupRemoteSessionFilter(org.jbundle.thin.base.message.BaseMessageFilter messageFilter)
        throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(SETUP_REMOTE_SESSION_FILTER);
        transport.addParam(FILTER, messageFilter);   // Don't use COMMAND
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return (org.jbundle.thin.base.message.BaseMessageFilter)objReturn;
    }
}
