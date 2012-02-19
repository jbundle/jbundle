/*
 *  @(#)RequestSession.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.optcode.thinmessage.remote;

import java.util.Map;

import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.remote.db.TaskSession;
import org.jbundle.base.remote.opt.TableModelSession;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.remote.RemoteException;


/**
 *  RequestSession - Remote side of the thin brochure request session.
 */
public class TestGridSession extends TableModelSession
{
    /**
     *  Default constructor.
     */
    public TestGridSession() throws RemoteException
    {
        super();
    }
    /**
     *  Constructor.
     */
    public TestGridSession(TaskSession parentSessionObject) throws RemoteException
    {
        this();
        this.init(parentSessionObject);
    }
    /**
     *  Initialize class fields.
     */
    public void init(TaskSession parentSessionObject)
    {
        super.init(parentSessionObject, null, null);
    }
    /**
     *  Override this to do an action sent from the client.
     */
    public Object doRemoteCommand(String strCommand, Map<String, Object> properties) throws RemoteException, DBException
    {
        if (strCommand.equals("RebuildRequestInput"))
        {
        }
        return super.doRemoteCommand(strCommand, properties);
    }
    /**
     *  Override this to open the main file for this session.
     */
    public Record openMainRecord()
    {
        return new TestTable(this);
    }
}
