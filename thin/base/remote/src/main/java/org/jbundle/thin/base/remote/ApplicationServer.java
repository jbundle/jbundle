/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.util.Map;

/**
 *  ApplicationServer - The interface to server objects.
 */
public interface ApplicationServer
{
    /**
     * Build a new remote session and initialize it.
     * @param properties to create the new remote task
     * @return The remote Task.
     * @throws RemoteException TODO
     */
    public RemoteTask createRemoteTask(Map<String, Object> properties) throws RemoteException;
}
