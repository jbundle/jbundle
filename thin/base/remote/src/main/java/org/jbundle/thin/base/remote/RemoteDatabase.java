/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote;

/**
 * @(#)RemoteDatabase.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.model.DBException;


/**
 * RemoteDatabase - Interface for the Remote Database.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public interface RemoteDatabase extends RemoteBaseSession
{

    /**
     * Close the physical database (usually overridden)
     */
    public void close() throws RemoteException;
    /**
     * Commit the transactions since the last commit.
     * Override this for SQL implementations.
     * @exception DBException An exception.
     */
    public void commit() throws RemoteException, DBException;
    /**
     * Rollback the transactions since the last commit.
     * Override this for SQL implementations.
     * @exception DBException An exception.
     */
    public void rollback() throws RemoteException, DBException;
    /**
     * Get the database properties (opt).
     */
    public Map<String, Object> getDBProperties() throws RemoteException, DBException;
    /**
     * Set the database properties (opt).
     */
    public void setDBProperties(Map<String, Object> properties) throws RemoteException, DBException;
}
