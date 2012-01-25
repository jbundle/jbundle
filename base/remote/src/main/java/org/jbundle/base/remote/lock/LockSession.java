/*
 * LockServer.java
 *
 * Created on March 25, 2002, 12:57 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.lock;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.db.TaskSession;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.Unreferenced;


/** 
 * The LockSessionObject is a session from a unique client task that will be asking
 * for record locks and unlocks.
 * <b>Note:</b> Be very careful of concurrency as these methods will be called by many tasks simutaneously.
 * @author  Administrator
 * @version 1.0.0
 */
public class LockSession extends BaseSession
    implements Unreferenced, RemoteLockSession
{
	private static final long serialVersionUID = 1L;

	public static final int LOCK_TIMEOUT = 30 * 1000;  // 30 Seconds
    
	protected int m_iLockTimeout = LOCK_TIMEOUT;

    /**
     * Creates new RmiSessionServer.
     */
    public LockSession() throws RemoteException
    {
        super();
    }
    /**
     * Constructor.
     */
    public LockSession(TaskSession parentSessionObject) throws RemoteException
    {
        this();
        this.init(parentSessionObject, null, null);
    }
    /**
     * Constructor
     */
    public void init(BaseSession parentSessionObject, Record record, Map<String, Object> objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Free this session.
     */
    public void free()
    {
        // First, make sure there are no locks in any of the lock tables that I touched.
        SessionInfo session = new SessionInfo(0, null); // This will match any session with the same remote session ID
        session.m_iRemoteSessionID = this.getSessionID();
        Iterator<LockTable> iterator = m_hsMyLockTables.iterator();
        while (iterator.hasNext())
        {
            LockTable lockTable = iterator.next();
            lockTable.unlockAll(session);
            iterator.remove();
        }
        super.free();
    }
    /**
     * Get the current lock timeout.
     */
    public int getLockTimeout()
    {
    	return m_iLockTimeout;
    }
    /**
     * Set the new lock timeout.
     */
    public void setLockTimeout(int iLockTimeout)
    {
    	m_iLockTimeout = iLockTimeout;
    }
    /**
     * Lock this bookmark in this record for this session.
     * @param strRecordName The record to create the lock in.
     * @param bookmark The bookmark to lock.
     * @param objSession The session that wants the lock.
     * @param strUser The name of the user that wants the lock.
     * @param iLockMode The type of lock (wait or error lock).
     * @return null if successful, the name of the user locking if not.
     */
    public int lockThisRecord(String strDatabaseName, String strRecordName, Object bookmark, SessionInfo sessionInfo, int iLockMode)
        throws DBException, RemoteException
    {
        sessionInfo.m_iRemoteSessionID = this.getSessionID();
        LockTable lockTable = this.getLockTable(strDatabaseName, strRecordName);
        SessionInfo sessionLocking = lockTable.addLock(bookmark, sessionInfo);
        if (sessionInfo.equals(sessionLocking))
        	sessionLocking = null;		// Ignore if I'm the one who is already locking it.
        if (sessionLocking != null)    // Someone is already locking it
            if (((iLockMode & DBConstants.OPEN_WAIT_FOR_LOCK_STRATEGY) == DBConstants.OPEN_WAIT_FOR_LOCK_STRATEGY) || ((iLockMode & DBConstants.OPEN_MERGE_ON_LOCK_STRATEGY) == DBConstants.OPEN_MERGE_ON_LOCK_STRATEGY))
        {   // Wait for lock
            sessionLocking = lockTable.waitForUnlockAndLock(bookmark, sessionInfo, this.getLockTimeout());
            if (sessionLocking == null)
            	return DBConstants.RECORD_CHANGED;	// Lock is okay, but the data has possibly changed
            else if ((iLockMode & DBConstants.OPEN_EXCEPTION_ON_LOCK_TYPE) != DBConstants.OPEN_EXCEPTION_ON_LOCK_TYPE)
                return DBConstants.ERROR_RETURN;    // Exception not returned
        }
        if (sessionLocking != null)
            if ((iLockMode & DBConstants.OPEN_EXCEPTION_ON_LOCK_TYPE) == DBConstants.OPEN_EXCEPTION_ON_LOCK_TYPE)
        {
            String strError = "Record locked by {0}";
            if (((iLockMode & DBConstants.OPEN_WAIT_FOR_LOCK_STRATEGY) == DBConstants.OPEN_WAIT_FOR_LOCK_STRATEGY) || ((iLockMode & DBConstants.OPEN_MERGE_ON_LOCK_STRATEGY) == DBConstants.OPEN_MERGE_ON_LOCK_STRATEGY))
                strError = "Lock Timeout error - locked by {0}";
            strError = this.getTask().getString(strError);
            String strUserName = sessionLocking.m_strUserName;
            if (strUserName == null)
                strUserName = "Unknown user";
            strError = MessageFormat.format(strError, strUserName);
            throw new DBException(strError);
        }
        return (sessionLocking == null ? DBConstants.NORMAL_RETURN : DBConstants.ERROR_RETURN);    // Success if nothing returned
    }
    /**
     * Unlock this bookmark in this record for this session.
     * @param strRecordName The record to unlock in.
     * @param bookmark The bookmark to unlock (or null to unlock all for this session).
     * @param objSession The session that wants to unlock.
     * @param iLockType The type of lock (wait or error lock).
     * @return True if successful.
     */
    public boolean unlockThisRecord(String strDatabaseName, String strRecordName, Object bookmark, SessionInfo sessionInfo)
            throws DBException, RemoteException
    {
        sessionInfo.m_iRemoteSessionID = this.getSessionID();
        LockTable lockTable = this.getLockTable(strDatabaseName, strRecordName);
        if (bookmark != null)
            return lockTable.removeLock(bookmark, sessionInfo);
        else
            return lockTable.unlockAll(sessionInfo);
    }
    /**
     * Called by the RMI runtime sometime after the runtime determines that
     * the reference list, the list of clients referencing the remote object,
     * becomes empty.
     * Here, I unlock all the locked records held by this remote session.
     * @since JDK1.1
     */
    public void unreferenced()
    {
        Utility.getLogger().info("unreferenced()");
        this.free();
    }
    /**
     * A list of the database/records touched by THIS session (so I can make sure they are gone when I am freed).
     * Note: Synchronization is not needed.
     */
    protected HashSet<LockTable> m_hsMyLockTables = new HashSet<LockTable>();
    /**
     * List of lock tables (one for each record).
     */
    protected static Map<String,LockTable> m_htLockTables = new HashMap<String,LockTable>();
    /**
     * Get the lock table for this record.
     * @param strRecordName The record to look up.
     * @return The lock table for this record (create if it doesn't exist).
     */
    public LockTable getLockTable(String strDatabaseName, String strRecordName)
    {
        String strKey = strDatabaseName + ':' + strRecordName;
        LockTable lockTable = (LockTable)m_htLockTables.get(strKey);
        if (lockTable == null)
        {
            synchronized (m_htLockTables)
            {
                if ((lockTable = (LockTable)m_htLockTables.get(strKey)) == null)
                    m_htLockTables.put(strKey, lockTable = new LockTable());
            }
            m_hsMyLockTables.add(lockTable);   // Keep track of the lock tables that I used
        }
        return lockTable;
    }
    /**
     * Each session has a unique identifying number, so the remote ID in the session is unique.
     */
    protected static int m_iNextSession = 1;
    /**
     * I keep my session information here; an Identity hash map maps the objects directly to sessions.
     */
    protected static Map<LockSession,Integer> m_hmSessions = new IdentityHashMap<LockSession,Integer>();
    /**
     * Get the unique ID for this session (or assign one if it doesn't exist).
     * @return The unique session ID.
     */
    public int getSessionID()
    {
        Integer intSession = (Integer)m_hmSessions.get(this);
        if (intSession == null)
        {
            synchronized (m_hmSessions)
            {
                m_hmSessions.put(this, intSession = new Integer(m_iNextSession++));
            }
        }
        return intSession.intValue();
    }
}
