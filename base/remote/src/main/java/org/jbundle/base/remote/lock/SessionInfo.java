/*
 * SessionInfo.java
 *
 * Created on March 25, 2002, 12:56 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.lock;

/**
 * The Session Info holds all the information concerning this registered unique object.
 * It is here, because you don't want to send the object over the wire, you want to send this small object.
 */
public class SessionInfo extends Object
    implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     */
    public SessionInfo(int iSessionID, String strUserName)
    {
        super();
        m_iSessionID = iSessionID;
        m_strUserName = strUserName;
        m_iRemoteSessionID = 0;
        m_remoteLockSession = null;
    }
    /**
     * Two sessions are equal only if the sessionID and remote sessionID are equal.
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof SessionInfo)
            if (((((SessionInfo)obj).m_iSessionID == m_iSessionID) || (m_iSessionID == 0) || (((SessionInfo)obj).m_iSessionID == 0))
                && (((SessionInfo)obj).m_iRemoteSessionID == m_iRemoteSessionID))
                    return true;
        return false;
    }
    /**
     * The unique ID for this session.
     */
    public int m_iSessionID;
    /**
     * The user name.
     */
    public String m_strUserName;
    /**
     * The unique remote session ID.
     */
    public transient int m_iRemoteSessionID = 0;
    /**
     * Remote lock server task for this session.
     */
    public transient Object m_remoteLockSession = null;
//      public transient Hashtable m_tableLocks;
}
