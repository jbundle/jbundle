package org.jbundle.thin.base.remote.proxy;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.remote.RemoteDatabase;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class DatabaseProxy extends BaseSessionProxy
    implements RemoteDatabase, ProxyConstants
{

    /**
     * Constructor.
     */
    public DatabaseProxy()
    {
        super();
    }
    /**
     * Constructor.
     */
    public DatabaseProxy(TaskProxy parentProxy, String strID)
    {
        this();
        this.init(parentProxy, strID);
    }
    /**
     * Constructor.
     */
    public void init(TaskProxy parentProxy, String strID)
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
     * Close the physical database (usually overridden).
     */
    public void close() throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(CLOSE);
        transport.sendMessageAndGetReply();
    }
    /**
     * Commit the transactions since the last commit.
     * Override this for SQL implementations.
     * @exception DBException An exception.
     */
    public void commit() throws RemoteException, DBException
    {
        BaseTransport transport = this.createProxyTransport(COMMIT);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        this.checkDBException(objReturn);
    }
    /**
     * Rollback the transactions since the last commit.
     * Override this for SQL implementations.
     * @exception DBException An exception.
     */
    public void rollback()
        throws RemoteException, DBException
    {
        BaseTransport transport = this.createProxyTransport(ROLLBACK);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        this.checkDBException(objReturn);
    }
    /**
     * Get the database properties (opt).
     */
    public Map<String, Object> getDBProperties() throws RemoteException, DBException
    {
        BaseTransport transport = this.createProxyTransport(GET_DB_PROPERTIES);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return (Map)this.checkDBException(objReturn);
    }
    /**
     * Get the database properties.
     * @return The database properties object (Always non-null).
     */
    public void setDBProperties(Map<String, Object> properties) throws RemoteException, DBException
    {
        BaseTransport transport = this.createProxyTransport(SET_DB_PROPERTIES);
        transport.addParam(PROPERTIES, properties);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        this.checkDBException(objReturn);
    }
}
