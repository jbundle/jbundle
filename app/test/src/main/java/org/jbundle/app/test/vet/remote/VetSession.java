/**
 * @(#)VetSession.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.vet.remote;

import java.util.Map;

import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.base.db.Record;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.db.Session;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.remote.RemoteException;

/**
 *  VetSession - .
 */
public class VetSession extends Session
{
    /**
     * Default constructor.
     */
    public VetSession() throws RemoteException
    {
        super();
    }
    /**
     * VetSession Method.
     */
    public VetSession(BaseSession parentSessionObject, Record record, Object objectID) throws RemoteException
    {
        this();
        this.init(parentSessionObject, record, objectID);
    }
    /**
     * Override this to open the main file for this session.
     */
    public Record openMainRecord()
    {
        return new Vet(this);
    }
    /**
     * Override this to do an action sent from the client.
     * @param strCommand The command to execute
     * @param properties The properties for the command
     * @returns Object Return a Boolean.TRUE for success, Boolean.FALSE for failure.
     */
    public Object doRemoteCommand(String strCommand, Map<String,Object> properties) throws RemoteException, DBException
    {
        if (DBConstants.RESET.equalsIgnoreCase(strCommand))
        {
            Vet recVet = (Vet)this.getMainRecord();
            try {
                recVet.addNew();
            } catch (DBException ex)   {
                ex.printStackTrace();
            }
        }
        return super.doRemoteCommand(strCommand, properties);
    }

}
