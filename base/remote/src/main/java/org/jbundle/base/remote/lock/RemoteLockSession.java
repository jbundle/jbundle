/*
 * LockServer.java
 *
 * Created on March 25, 2002, 1:00 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.lock;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.remote.RemoteException;

/**
 *
 * @author  don
 */
public interface RemoteLockSession extends org.jbundle.thin.base.remote.RemoteBaseSession
{
    /**
     * Lock this bookmark in this record for this session.
     * @param strRecordName The record to create the lock in.
     * @param bookmark The bookmark to lock.
     * @param objSession The session that wants the lock.
     * @param strUser The name of the user that wants the lock.
     * @param iLockType The type of lock (wait or error lock).
     * @return null if successful, the name of the user locking if not.
     */
    public int lockThisRecord(String strDatabaseName, String strRecordName, Object bookmark, SessionInfo sessionInfo, int iLockType)
        throws DBException, RemoteException;
    /**
     * Unlock this bookmark in this record for this session.
     * @param strRecordName The record to unlock in.
     * @param bookmark The bookmark to unlock (or null to unlock all for this session).
     * @param objSession The session that wants to unlock.
     * @param iLockType The type of lock (wait or error lock).
     * @return True if successful.
     */
    public boolean unlockThisRecord(String strDatabaseName, String strRecordName, Object bookmark, SessionInfo sessionInfo)
        throws DBException, RemoteException;
}
