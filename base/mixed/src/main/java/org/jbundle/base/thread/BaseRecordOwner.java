/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.thread;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Enumeration;
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordList;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DatabaseOwner;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DatabaseCollection;
import org.jbundle.base.util.Environment;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.MessageListenerFilterList;
import org.jbundle.thin.base.thread.RecordOwnerCollection;


/**
 * A Base Record Owner is a class that handles the main RecordList Functions.
 * This class can be extended to handle the basic functions of a RecordOwner.
 * NOTE: LAME CODE ALERT: BaseRecordOwner is exactly the same as RemoteRecordOwner
 * except BaseRecordOwner subclasses Object and RemoteRecordOwner subclasses UnicastRemoteObject.
 */
public class BaseRecordOwner extends Object
    implements RecordOwner
{
    /**
     * List of all the records in use by this object (same as the one in BaseScreen).
     */
    protected RecordList m_vRecordList = null;
    /**
     * Session object that made me.
     */
    protected RecordOwnerParent m_taskParent = null;
    /**
     * The list of databases. (Only used for transaction support).
     */
    private DatabaseCollection m_databaseCollection = null;   // List of database lists.
    /**
     * Keep track of any filters that have me as a listener.
     */
    protected MessageListenerFilterList m_messageFilterList = null;
    /**
     * Children record owners.
     */
    protected RecordOwnerCollection m_recordOwnerCollection = null;

    /**
     * Initialization.
     */
    public BaseRecordOwner()
    {
        super();
    }
    /**
     * Initialization.
     */
    public BaseRecordOwner(RecordOwnerParent parent, Rec recordMain, Map<String, Object> properties)
    {
        this();
        this.init(parent, recordMain, properties);
    }
    /**
     * Initialize the RecordOwner.
     * @param parentSessionObject Parent that created this session object.
     * @param record Main record for this session (opt).
     * @param objectID ObjectID of the object that this SessionObject represents (usually a URL or bookmark).
     */
    public void init(RecordOwnerParent parent, Rec recordMain, Map<String, Object> properties)
    {
        m_taskParent = parent;
        if (m_taskParent != null)
        	m_taskParent.addRecordOwner(this);
        
        if (recordMain == null)
            recordMain = this.openMainRecord(); // No, open it!
        // Now look through the list and see if this query was added, it not, add it!
        boolean bQueryInTable = false;
        if (m_vRecordList != null)
        {
            for (Enumeration<Rec> e = m_vRecordList.elements() ; e.hasMoreElements() ;)
            {   // This should only be called for Imaged GridScreens (Child windows would be deleted by now if Component)
                Record cQueryInTable = (Record)e.nextElement();
                if (cQueryInTable == recordMain)
                    bQueryInTable = true;
            }
        }
        if (recordMain != null)
            if (!bQueryInTable)
                this.addRecord((Record)recordMain, false);
        this.openOtherRecords();    // Open the other files
        if (this.getScreenRecord() == null)
            this.setScreenRecord(this.addScreenRecord());

        this.addListeners();        // Add the session behaviors
    }
    /**
     * Free this record owner.
     */
    public void free()
    {
    	if (m_recordOwnerCollection != null)
    		m_recordOwnerCollection.free();
    	m_recordOwnerCollection = null;
        if (m_messageFilterList != null)
            m_messageFilterList.free();
        m_messageFilterList = null;
        // Close all records associated with this SessionObject
        if (m_vRecordList != null)
            m_vRecordList.free(this); // Free the records that belong to me
        m_vRecordList = null;

        if (m_databaseCollection != null)
            m_databaseCollection.free();
        m_databaseCollection = null;

        if (m_taskParent != null)
        	m_taskParent.removeRecordOwner(this);
        m_taskParent = null;
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        if (m_taskParent != null)
            return m_taskParent.getTask();  // Go up through the hierarchy until you hit the Task.
        return null;        // Never
    }
    /**
     * Add this record to this screen.
     * @param record The record to add.
     */
    public void addRecord(Rec record)
    {
        this.addRecord(record, false);
    }
    /**
     * Add this record to this screen.
     * @param record The record to add.
     * @param bMainQuery If this is the main record.
     */
    public void addRecord(Rec record, boolean bMainQuery)
    {
        if (m_vRecordList == null)
            m_vRecordList = new RecordList(null);
        if (record == null)
            return;
        if (((Record)record).getRecordOwner() != null)      // Screen already here?
            if (((Record)record).getRecordOwner() != this)      // Screen already here?
                ((Record)record).getRecordOwner().removeRecord(record);      // Already belongs to another owner, remove it!
        m_vRecordList.addRecord(record, bMainQuery);
        if (((Record)record).getRecordOwner() == null)      // Screen already here?
            ((Record)record).setRecordOwner(this);    // This is the file for this screen
    }
    /**
     * Remove this record from this screen.
     * @param record The record to remove.
     * @return true if successful.
     */
    public boolean removeRecord(Rec record)
    {
        if (m_vRecordList == null)
            return false;
        boolean bFlag = m_vRecordList.removeRecord(record);
        if (((Record)record).getRecordOwner() == this)
            ((Record)record).setRecordOwner(null);
        return bFlag;
    }
    /**
     * Get the main record for this screen.
     * @return The main record (or null if none).
     */
    public Record getMainRecord()
    {
        Record record = null;
        if (m_vRecordList != null)
            record = m_vRecordList.getMainRecord();
        if (record == null)
            if (m_taskParent instanceof RecordOwner)
                record = ((RecordOwner)m_taskParent).getMainRecord(); // Look thru the parent window now
        return record;
    }
    /**
     * Lookup this record for this recordowner.
     * @param The record's name.
     * @return The record with this name (or null if not found).
     */
    public Record getRecord(String strFileName)
    {
        Record record = null;
        if (m_vRecordList != null)
            record = m_vRecordList.getRecord(strFileName);
        if (record == null)
            if (m_taskParent instanceof RecordOwner)
                record = ((RecordOwner)m_taskParent).getRecord(strFileName); // Look thru the parent window now
        return record;  // Look thru the parent window now
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return null;
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
    }
    /**
     *  Override this to add the behaviors.
     */
    public void addListeners()
    {
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return null;
    }
    /**
     * Get the screen record.
     * @return The screen record.
     */
    public Record getScreenRecord()
    {
        Record record = null;
        if (m_vRecordList != null)
            record = m_vRecordList.getScreenRecord();
        return record;
    }
    /**
     * Set the screen record.
     * @param screenRecord The screen record.
     */
    public void setScreenRecord(Record screenRecord)
    {
        if (m_vRecordList != null)
            m_vRecordList.setScreenRecord(screenRecord);
    }
    /**
     * A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: Be very careful as this code is running in an independent thread
     * (synchronize to the task before calling record calls).
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return The error code.
     */
    public int handleMessage(BaseMessage message)
    {
        return DBConstants.NORMAL_RETURN; // Override this to process change
    }
    /**
     * Add this message filter to my list.
     */
    public void addListenerMessageFilter(BaseMessageFilter messageFilter)
    {
        if (m_messageFilterList == null)
            m_messageFilterList = new MessageListenerFilterList(this);
        m_messageFilterList.addMessageFilter(messageFilter);
    }
    /**
     * Have the message listener remove this filter from its list.
     */
    public void removeListenerMessageFilter(BaseMessageFilter messageFilter)
    {
        if (m_messageFilterList != null)
            m_messageFilterList.removeMessageFilter(messageFilter);
    }
    /**
     * Have the message listener remove this filter from its list.
     * NOTE: DO NOT call this method, this method is auto-called when you do filter.addMessageListener(listener);
     * @param messageFilter The message filter to remove.
     */
    public BaseMessageFilter getListenerMessageFilter(int iIndex)
    {
        if (m_messageFilterList != null)
            return m_messageFilterList.getMessageFilter(iIndex);
        return null;
    }
    /**
     * Is this listener going to send its messages to a thin client?
     * @return true if yes.
     */
    public boolean isThinListener()
    {
        return false;
    }
    /**
     * Get this record owner's parent.
     * Could be anotherRecordOwner or could be a Task.
     * @return The this record owner's parent.
     */
    public RecordOwnerParent getParentRecordOwner()
    {
        return m_taskParent;
    }
    /**
     * Get this Property for this key.
     * Override this to do something other than return the parent's property.
     * @param strProperty The key to lookup.
     * @return The value for this key.
     */
    public String getProperty(String strProperty)
    {
        if (this.getParentRecordOwner() != null)
            return this.getParentRecordOwner().getProperty(strProperty);
        return null;
    }
    /**
     * Get the properties.
     * @return A <b>copy</b> of the properties in the propertyowner.
     */
    public Map<String, Object> getProperties()
    {
        if (this.getParentRecordOwner() != null)
            return this.getParentRecordOwner().getProperties();
        return null;
    }
    /**
     * Set this property.
     * @param strProperty The property key.
     * @param strValue The property value.
     * Override this to do something.
     */
    public void setProperty(String strProperty, String strValue)
    {
        if (this.getParentRecordOwner() != null)
            this.getParentRecordOwner().setProperty(strProperty, strValue);
    }
    /**
     * Set the properties.
     * @param strProperties The properties to set.
     * Override this to do something.
     */
    public void setProperties(Map<String, Object> properties)
    {
        if (this.getParentRecordOwner() != null)
            this.getParentRecordOwner().setProperties(properties);
    }
    /**
     * Retrieve/Create a user properties record with this lookup key.
     * @param strPropertyCode The key I'm looking up.
     * @return The UserProperties for this registration key.
     */
    public PropertyOwner retrieveUserProperties(String strRegistrationKey)
    {
        if (this.getParentRecordOwner() != null)
            return this.getParentRecordOwner().retrieveUserProperties(strRegistrationKey);
        return null;
    }
    /**
     * Get the database owner for this recordowner.
     * Typically, the Environment is returned.
     * If you are using transactions, then the recordowner is returned, as the recordowner
     * needs private database connections to track transactions.
     * Just remember, if you are managing transactions, you need to call commit or your trxs are toast.
     * Also, you have to set the AUTO_COMMIT to false, before you init your records, so the database
     * object will be attached to the recordowner rather than the environment.
     * @return The database owner.
     */
    public DatabaseOwner getDatabaseOwner()
    {
        DatabaseOwner databaseOwner = null;
        if (DBConstants.FALSE.equalsIgnoreCase(this.getProperty(SQLParams.AUTO_COMMIT_PARAM)))
            databaseOwner = this; // If auto-commit is off, I am the db owner.
        else
            databaseOwner = (BaseApplication)this.getTask().getApplication();
        return databaseOwner;
    }
    /**
     * Given the name, either get the open database, or open a new one.
     * @param strDBName The name of the database.
     * @param iDatabaseType The type of database/table.
     * @return The database (new or current).
     */
    public BaseDatabase getDatabase(String strDBName, int iDatabaseType, Map<String, Object> properties)
    {
        if (m_databaseCollection == null)
            m_databaseCollection = new DatabaseCollection(this);
        return m_databaseCollection.getDatabase(strDBName, iDatabaseType, properties);
    }
    /**
     * Add this database to my database list.<br />
     * Do not call these directly, used in database init.
     * @param database The database to add.
     */
    public void addDatabase(BaseDatabase database)
    {
        m_databaseCollection.addDatabase(database);
    }
    /**
     * Remove this database from my database list.
     * Do not call these directly, used in database free.
     * @param database The database to free.
     * @return true if successful.
     */
    public boolean removeDatabase(BaseDatabase database)
    {
        return m_databaseCollection.removeDatabase(database);
    }
    /**
     * Get the environment.
     * From the database owner interface.
     * @return The Environment.
     */
    public Environment getEnvironment()
    {
        if ((this.getTask() != null) && (this.getTask().getApplication() != null))
            return ((BaseApplication)this.getTask().getApplication()).getEnvironment();
        return null;
    }
    /**
     * Is this recordowner the master or slave.
     * The slave is typically the TableSessionObject that is created to manage a ClientTable.
     * @return The MASTER/SLAVE flag.
     */
    public int getMasterSlave()
    {
        return RecordOwner.MASTER;      // A base process is always the master process.
    }
    /**
     * Is this recordowner a batch process, or an interactive screen?
     * @return True if this is a batch process.
     */
    public boolean isBatch()
    {
        return true;    // This is a batch process
    }
    /**
     * Add this record owner to my list.
     * @param recordOwner The record owner to add
     */
    public boolean addRecordOwner(RecordOwnerParent recordOwner)
    {
    	if (m_recordOwnerCollection == null)
    		m_recordOwnerCollection = new RecordOwnerCollection(this);
    	return m_recordOwnerCollection.addRecordOwner(recordOwner);
    }
    /**
     * Remove this record owner to my list.
     * @param recordOwner The record owner to remove.
     */
    public boolean removeRecordOwner(RecordOwnerParent recordOwner)
    {
    	if (m_recordOwnerCollection != null)
    		return m_recordOwnerCollection.removeRecordOwner(recordOwner);
    	return false;
    }
}
