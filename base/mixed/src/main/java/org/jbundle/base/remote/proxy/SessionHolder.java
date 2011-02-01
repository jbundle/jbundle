/*
 * TaskHolder.java
 *
 * Created on November 16, 2002, 8:53 PM
 */

package org.jbundle.base.remote.proxy;

import java.io.InputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTable;


/**
 *
 * @author  don
 */
public class SessionHolder extends BaseSessionHolder
{

    /**
     * Creates a new instance of TaskHolder
     */
    public SessionHolder()
    {
        super();
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public SessionHolder(BaseHolder parentHolder, RemoteSession remoteObject)
    {
        this();
        this.init(parentHolder, remoteObject);
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public void init(BaseHolder parentHolder, RemoteSession remoteObject)
    {
        super.init(parentHolder, remoteObject);    // TaskHolder doesn't have a parent (BaseHolder).
    }
    /**
     * Free the resources for this holder.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Handle the command send from my client peer.
     * @param in The (optional) Inputstream to get the params from.
     * @param out The stream to write the results.
     */
    public void doProcess(InputStream in, PrintWriter out, Map<String, Object> properties)
        throws RemoteException
    {
        String strCommand = this.getProperty(REMOTE_COMMAND, properties);
        if (GET_REMOTE_TABLE.equals(strCommand))
        {
            String strName = this.getNextStringParam(in, NAME, properties);
            RemoteTable remoteTable = ((RemoteSession)m_remoteObject).getRemoteTable(strName);
            // First, see if this is in my list already?
            String strID = this.find(remoteTable);
            if (strID == null)
                strID = this.add(new TableHolder(this, remoteTable));
            this.setReturnString(out, strID);
        }
        else if (SETUP_REMOTE_SESSION_FILTER.equals(strCommand))
        {
            org.jbundle.thin.base.message.BaseMessageFilter filter = (org.jbundle.thin.base.message.BaseMessageFilter)this.getNextObjectParam(in, FILTER, properties);
            filter = ((RemoteSession)m_remoteObject).setupRemoteSessionFilter(filter);
            this.setReturnObject(out, filter);
        }
        else
            super.doProcess(in, out, properties);
    }
}
