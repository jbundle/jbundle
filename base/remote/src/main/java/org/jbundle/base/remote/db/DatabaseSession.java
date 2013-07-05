/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.db;

/**
 * @(#)RemoteDatabaseImpl.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.BaseTaskSession;
import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.RemoteException;
import org.jbundle.thin.base.remote.RemoteDatabase;
import org.jbundle.thin.base.util.Application;


/**
 * RemoteDatabaseImpl - Implement the Vector database
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DatabaseSession extends BaseTaskSession
    implements RemoteDatabase
{
    private static final long serialVersionUID = 1L;

    protected BaseDatabase m_database = null;

    /**
     * Constructor.
     */
    public DatabaseSession() throws RemoteException
    {
        super();
    }
    /**
     * Build a new task session.
     * @param application Parent application (optional - usually take default [null]).
     */
    public DatabaseSession(App application) throws RemoteException
    {
        this();
        m_application = (Application)application;    // Don't pass down, because init matched standard session init.
        this.init(null, null, null);
    }
    /**
     * Constructor.
     * @param parentSessionObject My parent session (usually a RemoteTaskSessionObject).
     * @param database The database I'm passing calls to.
     */
    public DatabaseSession(BaseSession parentSessionObject, BaseDatabase database) throws RemoteException
    {
        this();
        this.init(parentSessionObject, null, null);
        this.setDatabase(database);
    }
    /**
     * Constructor.
     * @param parentSessionObject My parent session (usually a RemoteTaskSessionObject).
     * @param database The database I'm passing calls to.
     */
    public void init(BaseSession parentSessionObject, Record record, Map<String, Object> objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Close the physical database.
     */
    synchronized public void close() throws RemoteException
    {
        m_database.close();   // Do any inherrited
    } 
    /**
     * Set the database object.
     * @param database The database I'm passing calls to.
     */
    public void setDatabase(BaseDatabase database)
    {
        m_database = database;
    }
    /**
     * Set the database object.
     * @param database The database I'm passing calls to.
     */
    public BaseDatabase getDatabase()
    {
        return m_database;
    }
    /**
     * Free the objects.
     * This method is called from the remote client, and frees this session.
     */
    public void freeRemoteSession() throws RemoteException
    {
        try   {
            if (m_database.getTableCount() == 0)
                m_database.free();  // Free if this is my private database, or there are no tables left
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        super.freeRemoteSession();
    } 
    /**
     * Free the objects.
     */
    public void free()
    {
        super.free();
    } 
    /**
     * Commit the transactions since the last commit.
     * @exception Exception An exception.
     */
    public void commit() throws DBException, RemoteException
    {
        m_database.commit();
    }
    /**
     * Rollback the transactions since the last commit.
     * @exception Exception An exception.
     */
    public void rollback() throws DBException, RemoteException
    {
        m_database.rollback();
    }
    /**
     * Get the database properties.
     * @return The database properties object (Always non-null).
     */
    synchronized public Map<String, Object> getDBProperties() throws DBException, RemoteException
    {
        return m_database.getProperties();
    }
    /**
     * Get the database properties.
     * @return The database properties object (Always non-null).
     */
    synchronized public void setDBProperties(Map<String, Object> properties) throws DBException, RemoteException
    {
        m_database.setProperties(properties);
    }
    /**
     * If this database is in my database list, return this object.
     * @param database The database to lookup.
     * @return this if successful.
     */
    public DatabaseSession getDatabaseSession(BaseDatabase database)
    {
        if (database == this.getDatabase())
            return this;    // You found me.
        return super.getDatabaseSession(database);
    }
}
