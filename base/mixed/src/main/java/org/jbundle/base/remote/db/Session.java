package org.jbundle.base.remote.db;

/**
 * @(#)RemoteDatabaseImpl.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.message.record.GridRecordMessageFilter;
import org.jbundle.base.message.record.RecordMessageFilter;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.Debug;
import org.jbundle.base.util.Utility;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.message.session.ClientSessionMessageFilter;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.util.Application;


/**
 * SessionObject - Implement the remote session.
 * A Remote Session is typically the remote peer for a thin or table session.
 * Typically, you override openMainRecord and addListeners, then provide the remote logic
 * in the doRemoteAction method.
 * If you need to link a thin message filter with a thick message filter here, just override
 * the setupRemoteFilter method and return 
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class Session extends BaseSession
    implements RemoteSession
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public Session() throws RemoteException
    {
        super();
    }
    /**
     * Constructor
     */
    public Session(BaseSession parentSessionObject) throws RemoteException
    {
        this();
        this.init(parentSessionObject, null, null);
    }
    /**
     * Constructor
     */
    public void init(BaseSession parentSessionObject, Record record, Object objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Lookup this record for this session.
     * @param strFileName The file name.
     * @return The main record (or null if none).
     */
    public Record getRecord(String strFileName)
    {
        Record record = super.getRecord(strFileName);
        if (record == null)
        {   // Special check for sessions - see if the record is owned by a sub-"table session"
            for (int iIndex = 0; iIndex < this.getSessionObjectCount(); iIndex++)
            {
                BaseSession sessionObject = this.getSessionObjectAt(iIndex);
                if (sessionObject instanceof TableSession)
                    if (sessionObject.getMainRecord() != null)
                        if (sessionObject.getMainRecord().getTableNames(false).equals(strFileName))
                            return sessionObject.getMainRecord();
            }
        }
        return record;  // Look thru the parent window now
    }
    /**
     * Get the remote table for this session.
     * @param strRecordName Table Name or Class Name of the record to find
     * Note: This method is used when the SessionObject is used as an application's remote peer.
     */
    public RemoteTable getRemoteTable(String strRecordName)
    {
        if ((strRecordName == null) || (strRecordName.length() == 0))
            strRecordName = this.getMainRecord().getTableNames(false);  // Main Record
        if (strRecordName.lastIndexOf('.') != -1) // Correct this if a class name is passed
            strRecordName = strRecordName.substring(strRecordName.lastIndexOf('.') + 1);
        Record record = this.getRecord(strRecordName);
        if (record == null)
            return null;    // It must be one of this session's files
        if (this instanceof TableSession)
        {
            if (this.getMainRecord() == record)
                return (RemoteTable)this;
        }
        RemoteTable table = null;
        for (int iIndex = 0; iIndex < this.getSessionObjectCount(); iIndex++)
        {
            BaseSession sessionObject = this.getSessionObjectAt(iIndex);
            if (sessionObject instanceof TableSession)
            {
                if (sessionObject.getMainRecord() != null)
                    if (sessionObject.getMainRecord().getTableNames(false).equals(strRecordName))
                        return (RemoteTable)sessionObject;
            }
        }
        // Not wrapped yet, wrap in a new TableSessionObject
        try   {
            RecordOwner recordOwner = record.getRecordOwner();
            boolean bMainQuery = false;
            if (recordOwner != null)
                if (record == recordOwner.getMainRecord())
                    bMainQuery = true;
            table = new TableSession(this, record, null);
            if (recordOwner != null)
                if (bMainQuery)
                {
                    recordOwner.addRecord(record, bMainQuery);  // If the session wanted to access this record too, make sure it still can.
                    Utility.getLogger().warning("Should not create a sub-session for the main record!");
                }
        } catch (Exception ex)  {
            table = null;
        }
        return table;
    }
    /**
     * Link the filter to this remote session.
     * This is a special method that is needed because the remote link is passed as a remote reference to the session
     * even though it is in the same JVM. What you need to do in your implementation is lookup the message filter
     * and call messageFilter.linkRemoteSession(this);
     * If you need to change the behavior, override setupRemoteFilter in the session or linkRemoteSession in the filter.
     * Even though this is a remote method, it is always called within the same JVM from ReceiveQueueSessionObject.addRemoteMessageFilter.
     * @param messageFilter A serialized copy of the messageFilter to link this session to.
     */
    public final BaseMessageFilter setupRemoteSessionFilter(BaseMessageFilter messageFilter) throws RemoteException
    {
        boolean bThinClient = false;
        if (messageFilter instanceof ClientSessionMessageFilter)
            bThinClient = true;
        messageFilter = this.setupRemoteFilter(messageFilter);  // Change the filter?
        messageFilter.setThinTarget(bThinClient);   // This way the server process knows not to send a thick message here (before converting it to thin).
        BaseMessageManager messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
        BaseMessageReceiver messageReceiver = messageManager.getMessageQueue(messageFilter.getQueueName(), messageFilter.getQueueType()).getMessageReceiver();
        messageReceiver.addMessageFilter(messageFilter);    // I do this just in case the link code wants to add a filter to the tree (ie., RecordFilters).
        BaseMessageFilter messageFilterNew = messageFilter.linkRemoteSession(this);  // Link the filter and give filter a chance to change the remote version.
        if (messageFilterNew != messageFilter)
        {       // Rarely, but if it is changed, free the old one.
            messageReceiver.removeMessageFilter(messageFilter, true);
            if (messageFilterNew.getMessageReceiver() == null)
            {   // And it hasn't already added itself, add it to it's receiver
                messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
                messageReceiver = messageManager.getMessageQueue(messageFilterNew.getQueueName(), messageFilterNew.getQueueType()).getMessageReceiver();
                messageReceiver.addMessageFilter(messageFilterNew);
            }
        }
        messageFilterNew.addMessageListener(this);
        return messageFilterNew;
    }
    /**
     * Give a copy of the message filter, set up a remote filter.
     * @oaram messageFilter The message filter to setup (in relation to this class).
     * @return The same message filter.
     */
    public BaseMessageFilter setupRemoteFilter(BaseMessageFilter messageFilter)
    {
        Map<String,Object> propertiesForFilter = messageFilter.getProperties();
        if (propertiesForFilter != null)
        {
            String strClassName = (String)propertiesForFilter.get(MessageConstants.CLASS_NAME);
            if (strClassName != null)
            {
                if (strClassName.indexOf(MessageConstants.GRID_FILTER) != -1)
                {
                    Record record = this.getMainRecord();
                    if (record != null)
                        messageFilter = new GridRecordMessageFilter(record, null, true);
                    messageFilter.setMessageSource(null);   // Since this filter auto sets the source
                }
                else if (strClassName.indexOf(MessageConstants.RECORD_FILTER) != -1)
                {
                    Record record = this.getMainRecord();
                    if (record != null)
                        messageFilter = new RecordMessageFilter(record, null);
                    messageFilter.setMessageSource(null);
                }
                else
                {
                    BaseMessageFilter newMessageFilter = (BaseMessageFilter)Utility.makeObjectFromClassName(Object.class.getName(), strClassName);
                    if (newMessageFilter != null)
                        newMessageFilter.init(messageFilter.getQueueName(), messageFilter.getQueueType(), null, null);
                    if (newMessageFilter != null)
                        messageFilter = newMessageFilter;
                }
            }
        }
        return messageFilter; // By default, don't change
    }
}
