/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.model;

/**
 * QueryBase - Query description class.
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.model.RecordOwnerModel;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.db.DatabaseOwner;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.JMessageListener;


/**
 * Object that uses many records (such as Screens or Sessions).
 * This implements JMessageListener, because it is very common for the RecordOwner to want RecordMessages.
 */
public interface RecordOwner
    extends JMessageListener, RecordOwnerModel, DatabaseOwner
{ // Attributes
    /**
     * Flag for a master process.
     * This is usually the client, except remote processes and remote independent sessions are MASTER processes.
     */
    public static int MASTER = 1;
    /**
     * Flag for a slave process.
     * This is usually the auto-created TableSessionObject.
     */
    public static int SLAVE = 2;
    
    /**
     * Lookup this record for this recordowner.
     * @param The record's name.
     * @return The record with this name (or null if not found).
     */
    public Rec getRecord(String strFileName);
    /**
     * Get the main record for this screen.
     * @return The main record (or null if none).
     */
    public Rec getMainRecord();
    /**
     * Get the screen record.
     * @return The screen record.
     */
    public Rec getScreenRecord();
    /**
     * Get this Property for this key.
     * @param strProperty The key to lookup.
     * @return The value for this key.
     */
    public String getProperty(String strProperty);
    /**
     * A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: Be very careful as this code is running in an independent thread
     * (synchronize to the task before calling record calls).
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return The error code.
     */
    public int handleMessage(BaseMessage message);
    /**
     * Get the task for this record owner.
     * @return Record owner's task, or null to use the default task.
     */
    public Task getTask();
    /**
     * Get the database owner for this recordowner.
     * Typically, the Environment is returned.
     * If you are using transactions, then the recordowner is returned, as the recordowner
     * needs private database connections to track transactions.
     * @return The database owner.
     */
    public DatabaseOwner getDatabaseOwner();
    /**
     * Get this record owner's parent.
     * Could be anotherRecordOwner or could be a Task.
     * @return The this record owner's parent.
     */
    public RecordOwnerParent getParentRecordOwner();
    /**
     * Is this recordowner the master or slave.
     * The slave is typically the TableSessionObject that is created to manage a ClientTable.
     * @return The MASTER/SLAVE flag.
     */
    public int getMasterSlave();
    /**
     * Is this recordowner a batch process, or an interactive screen?
     * @return True if this is a batch process.
     */
    public boolean isBatch();
}
