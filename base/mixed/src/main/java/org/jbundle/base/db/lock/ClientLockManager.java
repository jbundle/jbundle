/*
 * SessionInfo.java
 *
 * Created on March 25, 2002, 12:56 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.lock;

import java.util.IdentityHashMap;
import java.util.Map;

import org.jbundle.base.db.DatabaseException;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;


/**
 * The ClientLockManager manages the links to the lock server(s).
 */
public class ClientLockManager extends Object
{
    /**
     * My owner/parent.
     */
    protected Object m_owner = null;

    /**
     * Constructor.
     */
    public ClientLockManager()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ClientLockManager(Object owner)
    {
        this();
        this.init(owner);
    }
    /**
     * Constructor.
     */
    public void init(Object owner)
    {
        m_owner = owner;
    }
    /**
     * Free this object's resources.
     */
    public void free()
    {
    }
    /**
     * Lock this bookmark in this record for this session.
     * @param strRecordName The record to create the lock in.
     * @param bookmark The bookmark to lock.
     * @param objSession The session that wants the lock.
     * @param strUser The name of the user that wants the lock.
     * @param iLockType The type of lock (wait or error lock).
     * @return null if successful, the name of the user locking if not.
     */
    public int lockThisRecord(Task objSession, String strDatabaseName, String strRecordName, Object bookmark, String strUserName, int iLockType)
        throws DBException
    {
        SessionInfo sessionInfo = this.getLockSessionInfo(objSession, strUserName);
        try {
            return this.getRemoteLockSession(sessionInfo).lockThisRecord(strDatabaseName, strRecordName, bookmark, sessionInfo, iLockType);
        } catch (java.rmi.RemoteException ex)   {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Unlock this bookmark in this record for this session.
     * @param strRecordName The record to unlock in.
     * @param bookmark The bookmark to unlock (or null to unlock all for this session).
     * @param objSession The session that wants to unlock.
     * @param iLockType The type of lock (wait or error lock).
     * @return True if successful.
     */
    public boolean unlockThisRecord(Task objSession, String strDatabaseName, String strRecordName, Object bookmark)
        throws DBException
    {
        SessionInfo sessionInfo = this.getLockSessionInfo(objSession, null);
        try {
            boolean bSuccess = this.getRemoteLockSession(sessionInfo).unlockThisRecord(strDatabaseName, strRecordName, bookmark, sessionInfo);
//?            if (bookmark == null)
//?                this.removeLockSessionInfo(objSession);    // For close all, remove the session
            return bSuccess;
        } catch (java.rmi.RemoteException ex)   {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * The remote link to the lock server.
     */
    protected RemoteTask m_lockServer = null;
    /**
     * For testing, set this to false to create the task server in your local space.
     */
    public boolean m_gbRemoteTaskServer = true;
    /**
     * Get the remote lock server.
     * @return The remote lock server.
     */
    public RemoteLockSession getRemoteLockSession(SessionInfo sessionInfo)
    {
        if (m_lockServer == null)
        {   // Haven't done any locks yet, hook up with the lock server.
            Application app = ((Environment)m_owner).getDefaultApplication();
            if (m_gbRemoteTaskServer)
            {
                String strServer = app.getAppServerName();
                String strRemoteApp = DBParams.DEFAULT_REMOTE_LOCK;
                String strUserID = null;
                String strPassword = null;
                m_lockServer = (RemoteTask)app.createRemoteTask(strServer, strRemoteApp, strUserID, strPassword);
            }
            if (m_lockServer == null)
            {
                m_gbRemoteTaskServer = false;
                try {
                    m_lockServer = new org.jbundle.base.remote.db.TaskSession(app);
                } catch (RemoteException ex)    {
                    ex.printStackTrace();
                }
            }
        }
        RemoteLockSession remoteLockSession = (RemoteLockSession)sessionInfo.m_remoteLockSession;
        if (remoteLockSession == null)
        {   // Setup the remote lock server
            try {
                if (m_gbRemoteTaskServer)
                {
                    remoteLockSession = (RemoteLockSession)m_lockServer.makeRemoteSession(LockSession.class.getName());
                }
                else
                {
                    remoteLockSession = new LockSession((org.jbundle.base.remote.db.TaskSession)m_lockServer);
                }
                sessionInfo.m_remoteLockSession = remoteLockSession;
            } catch (RemoteException ex)    {
                ex.printStackTrace();
            }
        }
        return remoteLockSession;
    }
    /**
     * Lookup the information for this session (or create new sessioninfo).
     * @param objSession The unique object identifying this session (Typically the Task).
     * @param strUser The (optional) user name.
     * @return The new or looked up session information.
     */
    public SessionInfo getLockSessionInfo(Task objSession, String strUserName)
    {
        if (objSession == null)
        {
            Utility.getLogger().warning("null session");
            return new SessionInfo(0, strUserName);     // 
        }
        SessionInfo intSession = (SessionInfo)m_hmLockSessions.get(objSession);
        if (intSession == null)
            m_hmLockSessions.put(objSession, intSession = new SessionInfo(m_iNextLockSession++, strUserName));
        return intSession;
    }
    /**
     * Remove this session information.
     * @param objSession The unique object identifying this session (Typically the JdbcTable).
     * @param strUser The (optional) user name.
     * @return The new or looked up session information.
     */
    public SessionInfo removeLockSessionInfo(Task objSession)
    {
        return (SessionInfo)m_hmLockSessions.remove(objSession);
    }
    /**
     * Each session has a unique identifying number, so the remote server can do a lookup.
     */
    protected int m_iNextLockSession = 1;
    /**
     * I keep my session information here; an Identity hash map maps the objects directly to sessions.
     */
    protected Map<Task,SessionInfo> m_hmLockSessions = new IdentityHashMap<Task,SessionInfo>();
}
