/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.server;

import org.jbundle.thin.base.remote.RemoteException;

/**
 * This is a convenience class, so users don't have to remember the path to RemoteSessionServer.
 * Note: DO NOT reference this class as it doesn't have an OSGi home
 */
public class Server extends org.jbundle.base.remote.server.RemoteSessionServer
{
	private static final long serialVersionUID = 1L;

    public Server() throws RemoteException
    {
        super();
    }
    
    public static void main(String args[])
    {
        org.jbundle.base.remote.server.RemoteSessionServer.main(args);
    }

}
