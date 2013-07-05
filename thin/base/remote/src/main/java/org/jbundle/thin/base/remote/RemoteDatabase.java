/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote;

/**
 * @(#)RemoteDatabase.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.model.DBException;
import org.jbundle.model.RemoteException;


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
     * @throws RemoteException TODO
     */
    public void close() throws RemoteException;
    /**
     * Commit the transactions since the last commit.
     * Override this for SQL implementations.
     * @exception DBException An exception.
     * @throws RemoteException TODO
     */
    public void commit() throws DBException, RemoteException;
    /**
     * Rollback the transactions since the last commit.
     * Override this for SQL implementations.
     * @exception DBException An exception.
     * @throws RemoteException TODO
     */
    public void rollback() throws DBException, RemoteException;
    /**
     * Get the database properties (opt).
     * @throws RemoteException TODO
     */
    public Map<String, Object> getDBProperties() throws DBException, RemoteException;
    /**
     * Set the database properties (opt).
     * @throws RemoteException TODO
     */
    public void setDBProperties(Map<String, Object> properties) throws DBException, RemoteException;
}
