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

import org.jbundle.model.DBException;
import org.jbundle.model.RemoteException;
import org.jbundle.thin.base.remote.RemoteDatabase;


/**
 *
 * @author  don
 */
public class DatabaseHolder extends BaseSessionHolder
{

    /**
     * Creates a new instance of TaskHolder
     */
    public DatabaseHolder()
    {
        super();
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public DatabaseHolder(TaskHolder parentHolder, RemoteDatabase remoteObject)
    {
        this();
        this.init(parentHolder, remoteObject);
    }
    /**
     * Creates a new instance of TaskHolder
     */
    public void init(TaskHolder parentHolder, RemoteDatabase remoteObject)
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
            if (CLOSE.equals(strCommand))
            {
                ((RemoteDatabase)m_remoteObject).close();
            }
            else if (GET_DB_PROPERTIES.equals(strCommand))
            {
                Map<String,Object> propIn = ((RemoteDatabase)m_remoteObject).getDBProperties();
                this.setReturnObject(out, propIn);
            }
            else if (SET_DB_PROPERTIES.equals(strCommand))
            {
                Map<String,Object> propIn = this.getNextPropertiesParam(in, PROPERTIES, properties);
                if (propIn != null)
                    properties.putAll(propIn);
                ((RemoteDatabase)m_remoteObject).setDBProperties(properties);
            }
            else if (COMMIT.equals(strCommand))
            {
                ((RemoteDatabase)m_remoteObject).commit();
            }
            else if (ROLLBACK.equals(strCommand))
            {
                ((RemoteDatabase)m_remoteObject).rollback();
            }
            else
                super.doProcess(in, out, properties);
        } catch (DBException ex)    {
            this.setReturnObject(out, ex);
        } catch (Exception ex)    {
            this.setReturnObject(out, ex);
        }
    }
}
