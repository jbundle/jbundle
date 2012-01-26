/*
 * TaskHolder.java
 *
 * Created on November 16, 2002, 8:53 PM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.proxy;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.jbundle.base.model.Utility;
import org.jbundle.model.message.Message;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteReceiveQueue;
import org.jbundle.thin.base.remote.RemoteSession;


/**
 *
 * @author  don
 */
public class ReceiveQueueHolder extends BaseSessionHolder
{

    /**
     * Creates a new instance of TaskHolder
     */
    public ReceiveQueueHolder()
    {
        super();
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public ReceiveQueueHolder(BaseHolder parentHolder, RemoteReceiveQueue remoteObject)
    {
        this();
        this.init(parentHolder, remoteObject);
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public void init(BaseHolder parentHolder, RemoteReceiveQueue remoteObject)
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
        if (RECEIVE_REMOTE_MESSAGE.equals(strCommand))
        {
            Message message = ((RemoteReceiveQueue)m_remoteObject).receiveRemoteMessage();
            if (message != null)	// If the client quits, this is freed and I get a null message (which I don't need to return)
            	this.setReturnObject(out, message);
        }
        else if (ADD_REMOTE_MESSAGE_FILTER.equals(strCommand))
        {
            BaseMessageFilter messageFilter = (BaseMessageFilter)this.getNextObjectParam(in, FILTER, properties);
            String strSessionPathID = this.getNextStringParam(in, SESSION, properties);
            BaseHolder sessionHolder = this.getSessionFromPath(strSessionPathID);
            BaseMessageFilter objReturn = null;
            if (sessionHolder instanceof SessionHolder)
            {       // Always
                RemoteSession remoteSession = (RemoteSession)sessionHolder.getRemoteObject();
                objReturn = ((RemoteReceiveQueue)m_remoteObject).addRemoteMessageFilter(messageFilter, remoteSession);
            }
            else if (sessionHolder == null)
            {   // Session may be null.
                objReturn = ((RemoteReceiveQueue)m_remoteObject).addRemoteMessageFilter(messageFilter, null);
            }
            else
                Utility.getLogger().warning("Remote session not found");
            this.setReturnObject(out, objReturn);
        }
        else if (REMOVE_REMOTE_MESSAGE_FILTER.equals(strCommand))
        {
            BaseMessageFilter messageFilter = (BaseMessageFilter)this.getNextObjectParam(in, FILTER, properties);
            boolean bFreeFilter = this.getNextBooleanParam(in, FREE, properties);
            boolean bSuccess = ((RemoteReceiveQueue)m_remoteObject).removeRemoteMessageFilter(messageFilter, bFreeFilter);
            this.setReturnObject(out, new Boolean(bSuccess));
        }
        else if (UPDATE_REMOTE_FILTER_PROPERTIES.equals(strCommand))
        {
            BaseMessageFilter messageFilter = (BaseMessageFilter)this.getNextObjectParam(in, FILTER, properties);
            Object[][] propIn = (Object[][])this.getNextObjectParam(in, PROPERTIES, properties);
            Map<String,Object> propFilter = this.getNextPropertiesParam(in, MAP, properties);
            ((RemoteReceiveQueue)m_remoteObject).updateRemoteFilterProperties(messageFilter, propIn, propFilter);
        }
        else
            super.doProcess(in, out, properties);
    }
}
