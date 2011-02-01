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

import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.remote.RemoteSendQueue;


/**
 *
 * @author  don
 */
public class SendQueueHolder extends BaseSessionHolder
{

    /**
     * Creates a new instance of TaskHolder
     */
    public SendQueueHolder()
    {
        super();
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public SendQueueHolder(BaseHolder parentHolder, RemoteSendQueue remoteObject)
    {
        this();
        this.init(parentHolder, remoteObject);
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public void init(BaseHolder parentHolder, RemoteSendQueue remoteObject)
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
        if (SEND_MESSAGE.equals(strCommand))
        {
            BaseMessage message = (BaseMessage)this.getNextObjectParam(in, MESSAGE, properties);
            ((RemoteSendQueue)m_remoteObject).sendMessage(message);
        }
        else
            super.doProcess(in, out, properties);
    }
}
