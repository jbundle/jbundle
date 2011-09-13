/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.server;

/**
 * This is a convenience class, so users don't have to remember the path to RemoteSessionServer.
 * Note: DO NOT reference this class as it doesn't have an OSGi home
 */
public class Server extends org.jbundle.base.remote.rmiserver.RemoteSessionServer
{
	private static final long serialVersionUID = 1L;

    public Server() throws java.rmi.RemoteException
    {
        super();
    }
    
    public static void main(String args[])
    {
        org.jbundle.base.remote.rmiserver.RemoteSessionServer.main(args);
    }

}
