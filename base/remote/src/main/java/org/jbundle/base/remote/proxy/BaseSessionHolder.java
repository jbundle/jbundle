/*
 * TaskHolder.java
 *
 * Created on November 16, 2002, 8:53 PM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.proxy;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.jbundle.base.remote.BaseSession;
import org.jbundle.model.RemoteException;
import org.jbundle.thin.base.remote.RemoteBaseSession;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTable;


/**
 *
 * @author  don
 */
public class BaseSessionHolder extends BaseHolder
{

    /**
     * Creates a new instance of TaskHolder
     */
    public BaseSessionHolder()
    {
        super();
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public BaseSessionHolder(BaseHolder parentHolder, RemoteBaseSession remoteObject)
    {
        this();
        this.init(parentHolder, remoteObject);
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public void init(BaseHolder parentHolder, RemoteBaseSession remoteObject)
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
        try {
            if (FREE_REMOTE_SESSION.equals(strCommand))
            {
                ((BaseSession)m_remoteObject).freeRemoteSession();
                this.free();    // Free this (peer's) resources.
            }
            else if (MAKE_REMOTE_SESSION.equals(strCommand))
            {
                String strSessionClassName = this.getNextStringParam(in, NAME, properties);
                RemoteBaseSession remoteSession = ((RemoteBaseSession)m_remoteObject).makeRemoteSession(strSessionClassName);
                this.setReturnSessionOrObject(out, remoteSession);
            }
            else if (DO_REMOTE_ACTION.equals(strCommand))
            {
                String strAction = this.getNextStringParam(in, NAME, properties);
                Map<String,Object> propIn = this.getNextPropertiesParam(in, PROPERTIES, properties);
                if (propIn != null)
                    properties.putAll(propIn);
                Object objReturn = ((RemoteBaseSession)m_remoteObject).doRemoteAction(strAction, properties);
                this.setReturnSessionOrObject(out, objReturn);  // Could be a session.
            }
            else
                super.doProcess(in, out, properties);
        } catch (Exception ex)    {
            // ex.printStackTrace();    // Don't print this error, since I am returning it to my ajax caller.
            this.setReturnObject(out, ex);
        }
    }
    /**
     * If this is a session, convert to a proxy session and return, if object, convert and return.
     * @param out The return output stream.
     * @param strReturn The string to return.
     */
    public void setReturnSessionOrObject(PrintWriter out, Object objReturn)
    {
        String strID = null;
        String strSessionClass = null;
        if (objReturn instanceof RemoteTable)
        {
            strSessionClass = REMOTE_TABLE;
            strID = this.add(new TableHolder(this, (RemoteTable)objReturn));
        }
        else if (objReturn instanceof RemoteSession)
        {
            strSessionClass = REMOTE_SESSION;
            strID = this.add(new SessionHolder(this, (RemoteSession)objReturn));
        }
        else if (objReturn instanceof RemoteBaseSession)
        {
            strSessionClass = REMOTE_BASE_SESSION;
            strID = this.add(new BaseSessionHolder(this, (RemoteBaseSession)objReturn));
        }
        if (strID != null)
            this.setReturnString(out, strSessionClass + CLASS_SEPARATOR + strID);
        else
            this.setReturnObject(out, objReturn);
    }
}
