/*
 * LockServer.java
 *
 * Created on March 25, 2002, 12:57 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.util.Utility;


/**
 * A lock table for a record.
 */
class LockTable extends HashMap<Object,SessionInfo>
{
	private static final long serialVersionUID = 1L;

	public LockTable()
    {
        super();
    }
    /**
     * Add this lock to the table.
     * @param bookmark The bookmark to lock.
     * @param session The session to lock.
     * @return null if successful, the session of the locking user if not successful.
     */
    public synchronized SessionInfo addLock(Object bookmark, SessionInfo sessionInfo)
    {
        Utility.getLogger().info("Lock: " + bookmark + ", Session: " + sessionInfo.m_iSessionID + " success: " + !this.containsKey(bookmark));
        if (this.containsKey(bookmark))
            return (SessionInfo)this.get(bookmark);   // Error, already locked
        return (SessionInfo)this.put(bookmark, sessionInfo);    // Returns null
    }
    /**
     * Remove this bookmark from the lock list.
     * @param bookmark The bookmark to lock.
     * @param session The session to lock.
     * @return true if successful.
     */
    public synchronized boolean removeLock(Object bookmark, SessionInfo sessionInfo)
    {
        Utility.getLogger().info("Unlock: " + bookmark + ", Session: " + sessionInfo.m_iSessionID + " success: " + sessionInfo.equals(this.get(bookmark)));
        if (!sessionInfo.equals(this.get(bookmark)))
            return false;   // Not my session - error.
        if (this.getWaitlist(bookmark, false) != null)
        	if (this.getWaitlist(bookmark, false).size() > 0)
        {   // Notify this person that the wait is over
    		SessionInfo sessionInfoToUnlock = this.getWaitlist(bookmark, true).get(0);
            if (sessionInfoToUnlock == null)
            	return false;	// Never
            synchronized (sessionInfoToUnlock)
            {
        		if (sessionInfoToUnlock != this.popWaitlistSession(bookmark))
                	return false;	// Never since the only remove is this (synchronized line)
                boolean bSuccess = sessionInfo.equals(this.remove(bookmark));
                if (bSuccess)	// Always
                	sessionInfoToUnlock.notify();	// Tell locked session to continue
            	return bSuccess;
            }
        }
        return sessionInfo.equals(this.remove(bookmark));
    }
    /**
     * Add this lock to the table.
     * @param bookmark The bookmark to lock.
     * @param session The session to lock.
     * @return null if successful, the session of the locking user if not successful (timeout).
     */
    public SessionInfo waitForUnlockAndLock(Object bookmark, SessionInfo sessionInfo, int iTimeToWait)
    {
        Utility.getLogger().info("Wait for unlock: " + bookmark + ", Session: " + sessionInfo.m_iSessionID);
        try {
        	synchronized (sessionInfo)
        	{
                if (!this.containsKey(bookmark))
                    return null; // Was unlocked between sync calls
                this.getWaitlist(bookmark, true).add(sessionInfo);
        		sessionInfo.wait(iTimeToWait);  // Wait to be unlocked
        		// If the timeout is reached, The next line will return the session of the hog who has the lock
                return this.addLock(bookmark, sessionInfo); // Was unlocked between sync calls
        	}
        } catch (InterruptedException ex)  {
            ex.printStackTrace();   // Never
        }
        return null;	// Error
    }
    /**
     * Unlock all the locks for this session.
     */
    public synchronized boolean unlockAll(SessionInfo sessionInfo)
    {
        Utility.getLogger().info("Unlock all, Session: " + sessionInfo.m_iSessionID);
        for (Object bookmark : this.keySet())
        {
            SessionInfo thisSession = (SessionInfo)this.get(bookmark);
            if (sessionInfo.equals(thisSession))
                this.removeLock(bookmark, thisSession);            
        }
        return true;
    }
    /**
     * Get the waitlist for this bookmark.
     */
    public Vector<SessionInfo> getWaitlist(Object bookmark, boolean bCreateIfNotFound)
    {
    	Vector<SessionInfo> vector = m_htWaitLists.get(bookmark);
        if (bCreateIfNotFound)
            if (vector == null)
                m_htWaitLists.put(bookmark, vector = new Vector<SessionInfo>());
        return vector;
    }
    /**
     * Get the waitlist for this bookmark.
     */
    public SessionInfo popWaitlistSession(Object bookmark)
    {
    	Vector<SessionInfo> vector = m_htWaitLists.get(bookmark);
    	if (vector == null)
    		return null;
    	SessionInfo sessionInfo = vector.remove(0);
    	if (vector.size() == 0)
    		m_htWaitLists.remove(bookmark);
    	return sessionInfo;
    }
    /**
     * Sessions waiting on this bookmark's lock.
     * Key: bookmark
     * Value: Vector filled with locktables waiting for this lock to unlock.
     */
    protected Map<Object,Vector<SessionInfo>> m_htWaitLists = new HashMap<Object,Vector<SessionInfo>>();
}
