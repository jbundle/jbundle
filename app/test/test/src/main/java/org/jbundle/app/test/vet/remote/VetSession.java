/**
 * @(#)VetSession.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.vet.remote;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.remote.db.*;
import org.jbundle.base.remote.*;
import org.jbundle.thin.base.remote.*;
import org.jbundle.app.test.vet.db.*;

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
    public VetSession(BaseSession parentSessionObject, Record record, Map<String, Object> objectID) throws RemoteException
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
