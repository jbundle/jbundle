/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.remote.db.DatabaseSession;
import org.jbundle.base.thread.RemoteRecordOwner;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Debug;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.remote.RemoteBaseSession;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * A SessionObject is the base class for all session types.
 * It manages sub-sessions, records and properties as required by
 * TaskSessions and RecordOwner Sessions
 * (ie., RemoteTaskSessionObject and RemoteSessionObject).
 * You usually don't want to extend this directly, unless you aren't
 * managing records or you have a specialized Session type.
 */
public class BaseSession extends RemoteRecordOwner
    implements RemoteBaseSession
{
    private static final long serialVersionUID = 1L;
    /**
     * List of all the DBObjects spawned by this object (so you can clean up on EndOfSession).
     */
    transient protected Vector<BaseSession> m_vSessionObjectList = null;
    /**
     * Record bookmark of this Remote object.
     * (ObjectID passed in on initialization).
     */
    protected Object m_objectID = null;

    /**
     * Initialization.
     */
    public BaseSession() throws RemoteException
    {
        super();
    }
    /**
     * Initialization.
     * @param parentSessionObject Parent that created this session object.
     * @param record Main record for this session (opt).
     * @param objectID ObjectID of the object that this SessionObject represents (usually a URL or bookmark).
     */
    public BaseSession(BaseSession parentSessionObject, Record record, Object objectID) throws RemoteException
    {
        this();
        this.init(parentSessionObject, record, objectID);
    }
    /**
     * Initialization.
     * @param parentSessionObject Parent that created this session object.
     * @param record Main record for this session (opt).
     * @param objectID ObjectID of the object that this SessionObject represents (usually a URL or bookmark).
     */
    public void init(BaseSession parentSessionObject, Record record, Object objectID)
    {
        m_iMasterSlave = -1;    // Default
        if (parentSessionObject != null)
        {
            m_sessionObjectParent = parentSessionObject;    // Yeah, I know this is set in super, but I'm setting it now!
            ((BaseSession)m_sessionObjectParent).addSessionObject(this);
        }
        m_objectID = objectID;

        super.init(parentSessionObject, record, objectID);

        this.setRecordCurrent();    // Read the record passed in as a bookmark
    }
    /**
     * Initialization.
     */
    public void free()
    {
        if (m_sessionObjectParent != null)
            ((BaseSession)m_sessionObjectParent).removeSessionObject(this);   // Have my parent remove me from their list.
        // Remove all the session objects that I am responsible for
        if (m_vSessionObjectList != null)
        {
            while (m_vSessionObjectList.size() > 0)
            {
                BaseSession sessionObject = (BaseSession)m_vSessionObjectList.elementAt(0);
                sessionObject.free(); // They will automatically call me.removeSessionObject(them);
            } 
            m_vSessionObjectList.removeAllElements();
            m_vSessionObjectList = null;
        }
        super.free();
    }
    /**
     * Release the session and its resources.
     */
    public void freeRemoteSession() throws RemoteException
    {
        try   {
            this.free();
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }
    /**
     * Build a new remote session and initialize it.
     * @param parentSessionObject The remote parent (or null to use this TaskSession as the parent).
     * @param strSessionClass The class name of the session to create.
     * @return The RemoteSession object.
     */
    public RemoteBaseSession makeRemoteSession(String strSessionClassName) throws RemoteException
    {
        BaseSession session = null;
        try   {
            if (strSessionClassName.indexOf('.') == 0)
                strSessionClassName = Constants.ROOT_PACKAGE + strSessionClassName.substring(1);
            Utility.getLogger().info("Make remote session. Remote class: " + strSessionClassName);
        	session = (BaseSession)ClassServiceUtility.getClassService().makeObjectFromClassName(strSessionClassName);
            session.init(this, null, null);
        } catch (Exception ex)  {
            ex.printStackTrace();
            session = null;
        }
        return session;
    }
    /**
     * Do a remote action.
     * @param strCommand Command to perform remotely.
     * @param properties Properties for this command (optional).
     * @return boolean success.
     */
    public Object doRemoteAction(String strCommand, Map<String, Object> properties) throws DBException, RemoteException
    {
        synchronized (this.getTask())
        {   // Just being careful (in case the user decides to do some data access)
            // Don't override this, override doRemoteCommand(xxx);
            return this.handleRemoteCommand(strCommand, properties, this);
        }
    }
    /**
     * Get the main record for this session.
     * @return The main record (or null if none).
     */
    public Record getMainRecord()
    {
        Record record = super.getMainRecord();
        if (record == null) if (m_sessionObjectParent != null)
            record = ((BaseSession)m_sessionObjectParent).getMainRecord();    // Look thru the parent window now
        return record;
    }
    /**
     * Lookup this record for this session.
     * @param strFileName The file name.
     * @return The main record (or null if none).
     */
    public Record getRecord(String strFileName)
    {
        Record record = super.getRecord(strFileName);
        if (record == null) if (m_sessionObjectParent != null)
            record = ((BaseSession)m_sessionObjectParent).getRecord(strFileName);   // Look thru the parent window now
        return record;  // Look thru the parent window now
    }
    /**
     * Get the screen query.
     * @return The screen record (or null if none).
     */
    public Record getScreenRecord()
    {
        Record record = super.getScreenRecord();
        if (record == null) if (m_sessionObjectParent != null)
            record = ((BaseSession)m_sessionObjectParent).getScreenRecord();  // Look thru the parent window now
        return record;
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Environment getEnvironment()
    {
        if (m_sessionObjectParent != null)
            return ((BaseSession)m_sessionObjectParent).getEnvironment();   // Look thru the parent window now
        return null;
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's task.
     */
    public Task getTask()
    {
        if (this.getParentSession() instanceof RecordOwner)
            return ((RecordOwner)this.getParentSession()).getTask();
        return super.getTask();   // Never
    }
    /**
     * Set the record to the bookmark passed in.
     * @return the record with the bookmark set to the initial value.
     */
    public Record setRecordCurrent()
    {
        Record recordMain = this.getMainRecord();
        try   {
            if (recordMain != null)
            {
                if (m_objectID != null)
                { // Read the request
                    if (recordMain.setHandle(m_objectID, DBConstants.BOOKMARK_HANDLE) != null)
                        recordMain.edit();
                    else
                        m_objectID = null;
                }
                if (m_objectID == null)
                    if ((recordMain.getEditMode() != DBConstants.EDIT_CURRENT) && (recordMain.getEditMode() != DBConstants.EDIT_IN_PROGRESS))   // If the record is current, don't touch it.
                {
                    recordMain.addNew();
                }
            }
        } catch (DBException ex)    {
            Debug.print(ex);
            ex.printStackTrace();
            m_objectID = null;
        }
        if (m_objectID == null)
            return null;
        else
            return recordMain;
    }
    /**
     * Set the remote property for this session.
     * Ususally used to Enable/Disable autosequence for this table.
     * @param strProperty The property key.
     * @param strValue The value to set.
     * @throws RemoteException TODO
     */
    public void setRemoteProperty(String strProperty, String strValue) throws RemoteException
    {
        this.setProperty(strProperty, strValue);
    }
    /**
     * Get my parent session.
     * @return The parent session.
     */
    public BaseSession getParentSession()
    {
        return (BaseSession)this.getParentRecordOwner();
    }
    /**
     * Add this sub-session to this session.
     * @param sessionObject The session to add.
     */
    public void addSessionObject(BaseSession sessionObject)
    {
        if (sessionObject == null)
            return;
        if (m_vSessionObjectList == null)
            m_vSessionObjectList = new Vector<BaseSession>();
        m_vSessionObjectList.addElement(sessionObject);
    }
    /**
     * Remove this sub-session from this session.
     * @param sessionObject The session to remove.
     * @return true If successful.
     */
    public boolean removeSessionObject(BaseSession sessionObject)
    {
        if (m_vSessionObjectList == null)
            return false;
        boolean bFlag = m_vSessionObjectList.removeElement(sessionObject);
        return bFlag;
    }
    /**
     * Number of sub-sessions.
     * @return Number of sub-sessions.
     */
    public int getSessionObjectCount()
    {
        if (m_vSessionObjectList == null)
            return 0;
        return m_vSessionObjectList.size();
    }
    /**
     * Get the sub-session at this location.
     * @param iIndex index.
     * @return The sub-session (or null).
     */
    public BaseSession getSessionObjectAt(int iIndex)
    {
        if (m_vSessionObjectList == null)
            return null;
        return (BaseSession)m_vSessionObjectList.elementAt(iIndex);
    }
    /** A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: Be very careful as this code is running in an independent thread
     * (synchronize to the task before calling record calls).
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return The error code.
     */
    public int handleMessage(BaseMessage message)
    {
        int iErrorCode = super.handleMessage(message);
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        for (int iFieldSeq = 0; iFieldSeq < this.getSessionObjectCount(); iFieldSeq++)
        {   // See if any of my children want to handle this command
            BaseSession session = this.getSessionObjectAt(iFieldSeq);
            if (message.getMessageHeader().getMessageSource() != null)
                if (session.getMainRecord() == message.getMessageHeader().getMessageSource())
            {
                iErrorCode = session.handleMessage(message);
                if (iErrorCode != DBConstants.NORMAL_RETURN)
                    return iErrorCode;
            }
        }
        return iErrorCode;
    }
    /**
     * Process the command.
     * Step 1 - Process the command if possible and return true if processed.
     * Step 2 - If I can't process, pass to all children (with me as the source).
     * Step 3 - If children didn't process, pass to parent (with me as the source).
     * Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param bUseSameWindow If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public Object handleRemoteCommand(String strCommand, Map<String,Object> properties, Object sourceSession) throws RemoteException, DBException
    {
        Object objHandled = this.doRemoteCommand(strCommand, properties);   // Do I handle it?

        if (Boolean.FALSE.equals(objHandled))
        {   // Not handled by this screen, try child windows
            for (int iFieldSeq = 0; iFieldSeq < this.getSessionObjectCount(); iFieldSeq++)
            {   // See if any of my children want to handle this command
                BaseSession sField = this.getSessionObjectAt(iFieldSeq);
                if (sField != sourceSession)   // Don't call the child that passed this up
                {
                    objHandled = sField.handleRemoteCommand(strCommand, properties, this);  // Send to children (make sure they don't call me)
                    if (!Boolean.FALSE.equals(objHandled))
                        return objHandled;	// If handled by sub-session, return
                }
            }
        }
        if (Boolean.FALSE.equals(objHandled))
            objHandled = super.handleRemoteCommand(strCommand, properties, sourceSession); // This will send the command to my parent
        return objHandled;
    }
    /**
     * Process the command.
     * Step 1 - Process the command if possible and return true if processed.
     * Step 2 - If I can't process, pass to all children (with me as the source).
     * Step 3 - If children didn't process, pass to parent (with me as the source).
     * Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param bUseSameWindow If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public Object doRemoteCommand(String strCommand, Map<String, Object> properties) throws RemoteException, DBException
    {
        if (DBParams.GET_FIELD_DATA.equalsIgnoreCase(strCommand))
        {
            return this.getFieldData(properties);
        }
        return Boolean.FALSE;   // Override this to handle this command
    }
    /**
     * GetFieldData Method.
     */
    public Map<String,Object> getFieldData(Map<String,Object> properties)
    {
        Map<String, Object> propReturn = null;
        if (properties != null)
        {
            propReturn = new Hashtable<String, Object>();
            for (int i = 1; ; i++)
            {
                String strFieldNumber = DBParams.FIELD + Integer.toString(i);
                String strFieldName = (String)properties.get(strFieldNumber);
                if (strFieldName == null)
                    break;  // Done.
                Record record = this.getMainRecord();
                if (strFieldName.indexOf('.') != -1)
                {
                    record = this.getRecord(strFieldName.substring(0, strFieldName.indexOf('.')));
                    strFieldName = strFieldName.substring(strFieldName.indexOf('.') + 1);
                }
                BaseField field = null;
                if (record != null)
                    field = record.getField(strFieldName);
                if (field != null)
                    propReturn.put(strFieldNumber, field.getData());
            }
        }
        return propReturn;
    }
    /**
     * If this database is in my database list, return this object.
     * @param database The database to lookup.
     * @return this if successful.
     */
    public DatabaseSession getDatabaseSession(BaseDatabase database)
    {
        for (int iFieldSeq = 0; iFieldSeq < this.getSessionObjectCount(); iFieldSeq++)
        {   // See if any of my children want to handle this command
            if (this.getSessionObjectAt(iFieldSeq).getDatabaseSession(database) != null)
                return this.getSessionObjectAt(iFieldSeq).getDatabaseSession(database);
        }
        return null;    // Not found
    }
    protected int m_iMasterSlave = -1;
    /**
     * Set the master slave flag.
     * @param iMasterSlave The flag (-1 = use default)
     * @return The old value.
     */
    public int setMasterSlave(int iMasterSlave)
    {
    	int iOldMasterSlave = m_iMasterSlave;
        m_iMasterSlave = iMasterSlave;
        return iOldMasterSlave;
    }
    /**
     * Is this recordowner the master or slave.
     * The slave is typically the TableSessionObject that is created to manage a ClientTable.
     * @return The MASTER/SLAVE flag.
     */
    public int getMasterSlave()
    {
        if (m_iMasterSlave != -1)    // Fix this with an override!
            return m_iMasterSlave;       // Auto-created session is the Slave session
        return super.getMasterSlave();      // An independent session is a master process.
    }
}
