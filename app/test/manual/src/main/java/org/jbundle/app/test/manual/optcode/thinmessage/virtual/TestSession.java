/*
 *  @(#)RequestSession.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.optcode.thinmessage.virtual;

import java.util.Map;

import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.remote.db.Session;
import org.jbundle.base.remote.db.TaskSession;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.remote.RemoteException;


/**
 *  RequestSession - Remote side of the thin brochure request session.
 */
public class TestSession extends Session
{
    /**
     *  Default constructor.
     */
    public TestSession() throws RemoteException
    {
        super();
    }
    /**
     *  Constructor.
     */
    public TestSession(TaskSession parentSessionObject) throws RemoteException
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
     *  Add behaviors to this session.
     */
    public void addListeners()
    {
        super.addListeners();
        
//x     this.getMainRecord().setupRecordListener(this);   // This will be hooked up to the remote listener
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
    /** A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: Be very careful as this code is running in an independent thread
     * (synchronize to the task before calling record calls).
     * NOTE: For now, you are only notified of the main record changes.
     */
    public int handleMessage(BaseMessage message)
    {
System.out.println("---------------------------------------------------------- TestSession/86");
        return super.handleMessage(message);    // Override this to process change
    }
    /**
     * Give a copy of the message filter, set up a remote filter.
     */
//x   public BaseMessageFilter setupRemoteFilter(BaseMessageFilter messageFilter)
//x   {
//x     Record record = this.getMainRecord();
//x     RecordMessageFilter recordMessageFilter = new RecordMessageFilter(record, null);
//x     return recordMessageFilter;
//x   }
}
