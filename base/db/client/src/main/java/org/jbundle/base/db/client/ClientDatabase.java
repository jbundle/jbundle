/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.client;

/**
 * @(#)ClientDatabase.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.DatabaseOwner;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.thin.base.remote.RemoteDatabase;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;


/**
 * ClientDatabase - Implement the database over RMI.
 * NOTE: Since BaseDatabase is suppose to be thread-safe, you have to be careful when
 * you call the RemoteDatabase since Ejb can only have one session - you must synchronize
 * to the server to be safe:<br />
 *              synchronized (record.getRecordOwner().getTask().getApplication().getServer())<br />
 *              {   // In case this is called from another task<br />
 * NOTE: This code is a little complex:<br />
 * There can be more than one remote database (one for each EJB server pipe), so
 * I don't do the db open, until I'm asked to make a table. If the table's record's server
 * isn't in my list, I add and open it, otherwise I just use the current remote database.
 * @author    Don Corley
 */
public class ClientDatabase extends BaseDatabase    
{
    /**
     * The TableSessionObject session.
     */
    protected RemoteDatabase m_remoteDatabase = null;

    /**
     * Properties for this database.
     * When I first open the database, I get all the DB properties so I don't have to keep calling remotely.
     */
    protected Map<String,Object> m_remoteProperties = null;

    /**
     * Constructor.
     */
    public ClientDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public ClientDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)   
    {
        this();
        this.init(databaseOwner, strDbName, iDatabaseType, null);
    }
    /**
     * Init this database and add it to the databaseOwner.
     * @param databaseOwner My databaseOwner.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     * @param strDBName The database name.
     */
    public void init(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType, Map<String, Object> properties)
    {
        super.init(databaseOwner, strDbName, iDatabaseType, properties);

        this.setMasterSlave(RecordOwner.MASTER);      // This is in the client space (Don't execute Server Behaviors)
        
        this.setProperty(DBParams.MESSAGES_TO_REMOTE, DBConstants.FALSE);   // Sent by server version
        this.setProperty(DBParams.CREATE_REMOTE_FILTER, DBConstants.TRUE);  // Yes
        this.setProperty(DBParams.UPDATE_REMOTE_FILTER, DBConstants.FALSE); // Updated by server version
    }
    /**
     * Free the objects.
     */
    public void free()
    {
        super.free(); // Do any inherited
        try   {
            if (m_remoteDatabase != null)
            {
                synchronized (this.getSyncObject(m_remoteDatabase))
                {
                    m_remoteDatabase.freeRemoteSession();
                    m_remoteDatabase = null;
                }
            }
        } catch (RemoteException ex)    {
            Utility.getLogger().warning("Remote free error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    /**
     * Open the physical database.
     * <p />I don't do the physical open until the first makeTable!
     * @exception DBException On open errors.
     */
    public void open() throws DBException
    {
        super.open();
    }
    /**
     * Close the remote databases.
     */
    public void close()
    {
        super.close();  // Do any inherited
        try   {
            if (m_remoteDatabase != null)
            {
                synchronized (this.getSyncObject(m_remoteDatabase))
                {   // In case this is called from another task
                    m_remoteDatabase.close();
                }
            }
        } catch (RemoteException ex)    {
            Utility.getLogger().warning("Remote close error: " + ex.getMessage());
            ex.printStackTrace();
        }
    } 
    /**
     * Make a table for this record.
     * @param record The record to make a table for (look at the database type).
     * @return The new table for this record.
     */
    public BaseTable doMakeTable(Record record)
    {
        BaseTable table = null;
        boolean bIsQueryRecord = record.isQueryRecord();
        int iRawDBType = (record.getDatabaseType() & DBConstants.TABLE_TYPE_MASK);
        if ((((iRawDBType == DBConstants.LOCAL) || (iRawDBType == DBConstants.TABLE))
                && (!bIsQueryRecord))
            || ((iRawDBType == DBConstants.LOCAL)
                    || (iRawDBType == DBConstants.REMOTE)
                    || (iRawDBType == DBConstants.REMOTE_MEMORY)
                    || (iRawDBType == DBConstants.TABLE)))
        {
            table = new ClientTable(this, record);
            String strTableClassName = record.getClass().getName();
            try   {
                Task task = null;
                RemoteTask server = null;
                if (record.getRecordOwner() != null)
                    if (record.getRecordOwner().getTask() != null)
                        task = record.getRecordOwner().getTask();
                if (task != null)
                    server = (RemoteTask)task.getRemoteTask();
                if (server == null)
                    server = (RemoteTask)this.getDatabaseOwner().getEnvironment().getDefaultApplication().getRemoteTask(task, null, true);
//?                String strUserID = null;
//?                if (record.getRecordOwner() != null)
//?                    strUserID = record.getRecordOwner().getProperty(DBParams.USER_ID);
                RemoteTable tableRemote = null;
                synchronized (this.getSyncObject(server))
                {   // In case this is called from another task
                    Map<String,Object> properties = table.getProperties();
                    Map<String,Object> propDatabase = BaseDatabase.addDBProperties(this.getProperties(), this, null); 
                    tableRemote = server.makeRemoteTable(strTableClassName, null, properties, propDatabase);  // NOTE: I DO Send the table properties down.
                    ((ClientTable)table).setRemoteTable(tableRemote);
                }
                // Do not get the remote db connection unless I need it
            } catch (RemoteException ex)    {
                Utility.getLogger().warning("remote doMakeTable error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        else
            table = super.doMakeTable(record);
        return table;
    }
    /**
     * Open the remote database.
     * @param environment The remote server object.
     * @param The user id/name (recommended on the initial server open).
     * @return The RemoteDatabase.
     */
    public RemoteDatabase getRemoteDatabase()
        throws RemoteException
    {
        if (m_remoteDatabase != null)
            return m_remoteDatabase;
        try {
            if (this.getTableCount() == 0)
                return null;    // Never
            ClientTable table = (ClientTable)m_vTableList.get(0);
            RemoteTable tableRemote = table.getRemoteTable();
            if (tableRemote != null)
            {
                synchronized (this.getSyncObject(tableRemote))
                {   // In case this is called from another task
                    // Move these properties up in case they are in the environment properties.
                    Map<String,Object> propDatabase = BaseDatabase.addDBProperties(this.getProperties(), this, null);
                    BaseDatabase.addDBProperty(propDatabase, this, null, this.getDatabaseName(false) + BaseDatabase.DBSHARED_PARAM_SUFFIX);
                    BaseDatabase.addDBProperty(propDatabase, this, null, this.getDatabaseName(false) + BaseDatabase.DBUSER_PARAM_SUFFIX);
                    m_remoteDatabase = tableRemote.getRemoteDatabase(propDatabase);
                }
            }
            if (m_remoteDatabase != null)
            {
                synchronized (this.getSyncObject(m_remoteDatabase))
                {   // In case this is called from another task
                    m_remoteProperties = m_remoteDatabase.getDBProperties();
                }
            }
        } catch (RemoteException ex)    {
            Utility.getLogger().warning("dbServer exception: " + ex.getMessage());
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        return m_remoteDatabase;
    }
    /**
     * Get this property from the remote database.
     * This does not make a remote call, it just return the property cached on remote db open.
     * @param strProperty The key to the remote property.
     * @return The value.
     */
    public String getRemoteProperty(String strProperty, boolean readIfNotCached)
    {
        if (m_remoteProperties == null)
        	if (readIfNotCached)
        	{
        		try {
	                this.getRemoteDatabase();
                } catch (RemoteException e) {
	                e.printStackTrace();
                }
        	}
        if (m_remoteProperties != null)
            return (String)m_remoteProperties.get(strProperty);
        return this.getFakeRemoteProperty(strProperty);
    }
    /**
     * To keep from having to contact the remote database, remote a property that will not effect the logic.
     * @param properties The properties object to add these properties to.
     */
    public String getFakeRemoteProperty(String strProperty)
    {
        if (SQLParams.EDIT_DB_PROPERTY.equalsIgnoreCase(strProperty))
            return SQLParams.DB_EDIT_NOT_SUPPORTED;     // By default, remote locks are not supported natively by the database
        return null;
    }
    /**
     * Commit the transactions since the last commit.
     * @exception DBException An exception.
     */
    public void commit() throws DBException
    {
        super.commit();   // Will throw an error if something is not set up right.
        try   {
            if (this.getRemoteDatabase() != null)
            {
                synchronized (this.getSyncObject(this.getRemoteDatabase()))
                {
                    this.getRemoteDatabase().commit();
                }
            }
        } catch (RemoteException ex)    {
            Utility.getLogger().warning("Remote commit error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex)  {
            throw this.convertError(ex);
        }
    }
    /**
     * Rollback the transactions since the last commit.
     * @exception DBException An exception.
     */
    public void rollback() throws DBException
    {
        super.rollback(); // Will throw an error if something is not set up right.
        try   {
            if (this.getRemoteDatabase() != null)
            {
                synchronized (this.getSyncObject(this.getRemoteDatabase()))
                {   // In case this is called from another task
                    this.getRemoteDatabase().rollback();
                }
            }
        } catch (RemoteException ex)    {
            Utility.getLogger().warning("Remote commit error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex)  {
            throw this.convertError(ex);
        }
    }
    /**
     * Get the sync object serving this remote table.
     * This is needed to synchronize the remote calls.
     * For regular Jini calls, just synchronize to the remote object.
     * If you are calling through a proxy or Ejb, sync to the server.
     * @param remoteObject The remote object that I will be calling.
     * @return The object to sync on (for ClientDatabases, just sync on this).
     */
    public Object getSyncObject(Object remoteObject)
    {
        return remoteObject;
    }
    /**
     * Get this property.
     * @param strProperty The key to lookup.
     * @return The return value.
     */
    public String getProperty(String strProperty)
    {
    	String value = super.getProperty(strProperty);
    	if (value == null)
    		if ((BaseDatabase.STARTING_ID.equalsIgnoreCase(strProperty))
				|| (BaseDatabase.ENDING_ID.equalsIgnoreCase(strProperty)))
    				value = this.getRemoteProperty(strProperty, true);
    	return value;
    }
}
