package org.jbundle.base.model;

import org.jbundle.model.DBException;
import org.jbundle.model.Task;

public interface LockManager {
    
    public static final String CLIENT_LOCK_MANAGER_CLASS = "org.jbundle.base.db.lock.ClientLockManager";
    /**
     * Constructor.
     */
    public void init(Object owner);
    /**
     * Lock this bookmark in this record for this session.
     * @param strRecordName The record to create the lock in.
     * @param bookmark The bookmark to lock.
     * @param objSession The session that wants the lock.
     * @param strUserName The name of the user that wants the lock.
     * @param iLockType The type of lock (wait or error lock).
     * @return null if successful, the name of the user locking if not.
     */
    public int lockThisRecord(Task objSession, String strDatabaseName, String strRecordName, Object bookmark, String strUserName, int iLockType)
        throws DBException;
    /**
     * Unlock this bookmark in this record for this session.
     * @param strRecordName The record to unlock in.
     * @param bookmark The bookmark to unlock (or null to unlock all for this session).
     * @param objSession The session that wants to unlock.
     * @return True if successful.
     */
    public boolean unlockThisRecord(Task objSession, String strDatabaseName, String strRecordName, Object bookmark)
        throws DBException;
}
