/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.jbundle.model.RemoteException;
import org.jbundle.thin.base.remote.ApplicationServer;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;
import org.jbundle.thin.base.remote.proxy.transport.EncodedTransport;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class ApplicationProxy extends BaseProxy
    implements ApplicationServer, ProxyConstants
{
    /**
     * Server URL.
     */
    protected String m_strServer = null;
    /**
     * Server URL.
     */
    protected String m_strBaseServletPath = null;
    /**
     * Remote application name.
     */
    protected String m_strRemoteApp = null;
    
    /**
     * Constructor.
     */
    public ApplicationProxy()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ApplicationProxy(String strServer, String strBaseServletPath, String strRemoteApp)
    {
        this();
        this.init(strServer, strBaseServletPath, strRemoteApp);
    }
    /**
     * Constructor.
     */
    public void init(String strServer, String strBaseServletPath, String strRemoteApp)
    {
        super.init(null, null);
        m_strServer = strServer;
        m_strBaseServletPath = strBaseServletPath;
        m_strRemoteApp = strRemoteApp;
    }
    /**
     * Get the server name.
     */
    public String getAppServerName()
    {
        return m_strServer;
    }
    /**
     * Get the web server name.
     */
    public String getBaseServletPath()
    {
        return m_strBaseServletPath;
    }
    /**
     * Create the proxy transport.
     * @param strCommand The command.
     * @return The transport.
     */
    public BaseTransport createProxyTransport(String strCommand)
    {
        return new EncodedTransport(this, strCommand);
    }
//------------------------------Remote Implementation---------------------------------
    /**
     * Build a new remote session and initialize it.
     * @return The remote Task.
     */
    public RemoteTask createRemoteTask(Map<String, Object> properties) throws RemoteException
    {
        try {
            return new TaskProxy(this, properties);
        } catch (RemoteException e) {
            if ((m_strBaseServletPath != null) && (m_strBaseServletPath.length() > 0))
            {
                try {
                    URL url = new URL(m_strBaseServletPath);
                    String path = url.getPath();
                    if (path.length() > 0)
                        if (m_strBaseServletPath.endsWith(path))
                    {
                        m_strBaseServletPath = m_strBaseServletPath.substring(0, m_strBaseServletPath.length() - path.length());
                        return new TaskProxy(this, properties);
                    }
                } catch (MalformedURLException e1) {
                    // Throw original error if bad URL
                }
            }
            throw e;
        }
    }
    /**
     * 
     */
    public String toString()
    {
        return m_strServer + ' ' + m_strBaseServletPath + ' ' + m_strRemoteApp;
    }
}
