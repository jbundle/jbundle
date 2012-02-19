/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */
import java.util.Map;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.remote.RemoteBaseSession;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class BaseSessionProxy extends BaseProxy
    implements RemoteBaseSession, ProxyConstants
{
    /**
     * Constructor.
     */
    public BaseSessionProxy()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseSessionProxy(BaseSessionProxy parentProxy, String strID)
    {
        this();
        this.init(parentProxy, strID);
    }
    /**
     * Constructor.
     */
    public void init(BaseSessionProxy parentProxy, String strID)
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
     * Release the session and its resources.
     */
    public void freeRemoteSession() throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(FREE_REMOTE_SESSION);
        transport.sendMessageAndGetReply();
    }
    /**
     * Build a new remote session and initialize it.
     * @param parentSessionObject The parent session for this new session (if null, parent = me).
     * @param strSessionClassName The class name of the remote session to build.
     */
    public RemoteBaseSession makeRemoteSession(String strSessionClassName) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(MAKE_REMOTE_SESSION);
        transport.addParam(NAME, strSessionClassName);
        String strClassAndID = (String)transport.sendMessageAndGetReply();
        return this.checkForSession(strClassAndID);
    }
    /**
     * Do a remote action.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     */
    public Object doRemoteAction(String strCommand, Map<String, Object> properties) throws DBException, RemoteException
    {
        BaseTransport transport = this.createProxyTransport(DO_REMOTE_ACTION);
        transport.addParam(NAME, strCommand);   // Don't use COMMAND
        transport.addParam(PROPERTIES, properties);
        Object strReturn = transport.sendMessageAndGetReply();
        if (strReturn instanceof String)
        {   // Could be returning a session
            BaseSessionProxy session = this.checkForSession((String)strReturn);
            if (session != null)
                return session;
        }
        Object objReturn = transport.convertReturnObject(strReturn);
        return this.checkDBException(objReturn);
    }
    /**
     * Check for session.
     * @param strClassAndID
     * @return
     */
    public BaseSessionProxy checkForSession(String strClassAndID)
    {
        int iColon = strClassAndID.indexOf(CLASS_SEPARATOR);
        String strSessionClass = null;
        if (iColon != -1)
            strSessionClass = strClassAndID.substring(0, iColon);
        String strID = strClassAndID.substring(iColon + 1);
        if (REMOTE_SESSION.equals(strSessionClass))
            return new SessionProxy(this, strID);
        if (REMOTE_TABLE.equals(strSessionClass))
            return new TableProxy(this, strID);
        if (REMOTE_BASE_SESSION.equals(strSessionClass))
            return new BaseSessionProxy(this, strID);
        return null;    // Not a session
    }
}
