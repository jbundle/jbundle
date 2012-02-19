/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy;

import java.util.HashMap;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright  *  Copyright © 2012 jbundle.org. All rights reserved.
� 1997 jbundle.org. All rights reserved.
 */


/**
 *  ApplicationServer - The interface to server objects.
 */
public class BaseProxy extends Object
    implements ProxyConstants
{
    /**
     * Application proxy.
     */
    protected BaseProxy m_parentProxy = null;
    /**
     * The unique remote task ID.
     */
    protected String m_strID = null;
    /**
     * List of "remote" tasks.
     */
    protected HashMap<String,BaseProxy> m_hmChildList = new HashMap<String,BaseProxy>();

    /**
     * Constructor.
     */
    public BaseProxy()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseProxy(BaseProxy parentProxy, String strID)
    {
        this();
        this.init(parentProxy, strID);
    }
    /**
     * Constructor.
     */
    public void init(BaseProxy parentProxy, String strID)
    {
        m_parentProxy = parentProxy;
        m_strID = strID;
        if (m_parentProxy != null)
            m_parentProxy.addChildProxy(this);
    }
    /**
     * Get the object's lookup ID.
     */
    public String getID()
    {
        return m_strID;
    }
    /**
     * Get the object's lookup ID.
     */
    public String getIDPath()
    {
        String strIDPath = this.getID();
        if (this.getParentProxy() != null)
        {
            String strParentPath = this.getParentProxy().getIDPath();
            if (strParentPath != null)
                strIDPath = strParentPath + PATH_SEPARATOR + strIDPath;
        }
        return strIDPath;
    }
    /**
     * Create the proxy transport.
     * @param strCommand The command.
     * @return The transport.
     */
    public BaseTransport createProxyTransport(String strCommand)
    {
        BaseTransport transport = null;
        if (m_parentProxy != null)
            transport = m_parentProxy.createProxyTransport(strCommand);
        transport.setProperty(TARGET, this.getIDPath());    // Only the outermost call will end up with the full path ID.
        return transport;
    }
    /**
     * Get this proxy's parent.
     * @return the parent proxy.
     */
    public BaseProxy getParentProxy()
    {
        return m_parentProxy;
    }
    /**
     * Add this child to the child list.
     */
    public void addChildProxy(BaseProxy childProxy)
    {
        String strID = childProxy.getID();
        m_hmChildList.put(strID, childProxy);
    }
    /**
     * Add this child to the child list.
     */
    public HashMap<String,BaseProxy> getChildList()
    {
        return m_hmChildList;
    }
    /**
     * Retrieve this record from the key.
     * @param strSeekSign Which way to seek null/= matches data also &gt;, &lt;, &gt;=, and &lt;=.
     * @param strKeyArea The name of the key area to seek on.
     * @param objKeyData The data for the seek (The raw data if a single field, a BaseBuffer if multiple).
     * @returns The record (as a vector) if successful, The return code (as an Boolean) if not.
     * @exception DBException File exception.
     * @exception RemoteException RMI exception.
     */
    public Object checkDBException(Object objData) throws DBException, RemoteException
    {
        if (objData instanceof DBException)
            throw (DBException)objData;
        if (objData instanceof RemoteException)
            throw (RemoteException)objData;
        return objData;
    }
    /**
     * Retrieve this record from the key.
     * @param strSeekSign Which way to seek null/= matches data also &gt;, &lt;, &gt;=, and &lt;=.
     * @param strKeyArea The name of the key area to seek on.
     * @param objKeyData The data for the seek (The raw data if a single field, a BaseBuffer if multiple).
     * @returns The record (as a vector) if successful, The return code (as an Boolean) if not.
     * @throws RemoteException TODO
     * @exception DBException File exception.
     */
    public Object checkRemoteException(Object objData) throws org.jbundle.thin.base.remote.RemoteException
    {
        if (objData instanceof RemoteException)
            throw (RemoteException)objData;
        return objData;
    }
}
