/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.opt;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.db.TableSession;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.remote.RemoteException;


/**
 * This is a specialized session that notifies the client of changes in the current gridTable.
 */
public class TableModelSession extends TableSession
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public TableModelSession() throws RemoteException
    {
        super();
    }
    /**
     * Constructor.
     */
    public TableModelSession(BaseSession parentSessionObject, Record record, Map<String, Object> objectID) throws RemoteException
    {
        this();
        this.init(parentSessionObject, record, objectID);
    }
    /**
     * Constructor.
     */
    public void init(BaseSession parentSessionObject, Record record, Map<String, Object> objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Receive to this server and send the response.
     */
    public void freeRemoteSession() throws RemoteException
    {
        super.freeRemoteSession();
    }
    /**
     *  Add behaviors to this session.
     */
    public void addListeners()
    {
        super.addListeners();
        if (this.getMainRecord() != null)
            this.getMainRecord().setupRecordListener(this, true, false);    // Don't allow echos
    }
    /**
     * Get the gridtable for this record (or create one if it doesn't exit).
     * @param gridRecord The record to get/create a gridtable for.
     * @return The gridtable.
     */
    public GridTable getGridTable(Record gridRecord)
    {
        GridTable gridTable = super.getGridTable(gridRecord);
        gridTable.setCache(true);  // Typically, the client is a gridscreen which caches the records (so I don't have to!)
        return gridTable;
    }
    /**
     * Given a copy of the message filter, set up the remote filter.
     */
    public BaseMessageFilter setupRemoteFilter(BaseMessageFilter messageFilter)
    {
        BaseMessageFilter filter = super.setupRemoteFilter(messageFilter);
//        if (filter == messageFilter)    // Did not create a server version, so do it now.
  //          filter = new ServerSessionMessageFilter(messageFilter.getQueueName(), messageFilter.getQueueType(), null, this, false, messageFilter.isThinTarget());
        return filter;
    }
}
