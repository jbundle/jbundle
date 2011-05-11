package org.jbundle.thin.base.thread;

/**
 * @(#)AutoTask.java    1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jbundle.model.App;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.RecordOwnerCollection;


/**
 * An autotask is a wrapper for an independent task thread.
 * Override the runTask method to do something.
 * NOTE: This class is commonly used as a basic task process. This is fine, just remember
 * to free this task manually when you are finished (or free the app that owns this task).
 */
public class AutoTask extends Object
	implements Task
{
    /**
     * The parent application.
     */
    protected Application m_application = null;
    /**
     * The task properties.
     */
    protected Map<String,Object> m_properties = null;
    /**
     * The last error string set for this task.
     */
    protected String m_strLastError = Constants.BLANK;  // Last Error message
    /**
     * The error code that goes with the last error string.
     */
    protected static int m_iLastErrorCode = -2;
    /**
     * Last display message.
     */
    protected String m_strCurrentStatus = null;
    /**
     * Last display level.
     */
    protected int m_iCurrentWarningLevel = Constants.INFORMATION;
    /**
     * Children record owners.
     */
    protected RecordOwnerCollection m_recordOwnerCollection = null;

    /**
     * Constructor.
     */
    public AutoTask()
    {
        super();
    }
    /**
     * Constructor.
     * @param application The parent application.
     * @param strParams The task properties.
     */
    public AutoTask(App application, String strParams, Map<String, Object> properties)
    {
        this();
        this.init(application, strParams, properties);
    }
    /**
     * Constructor.
     * @param application The parent application.
     * @param strParams The task properties.
     */
    public void init(App application, String strParams, Map<String, Object> properties)
    {
        m_application = (Application)application;
        if (m_application != null)
            m_application.addTask(this, null);                    // Add this task to the list
        if (properties != null)
        {
            if (m_properties != null)
                m_properties.putAll(properties);
            else
                m_properties = properties;
        }
        if (strParams != null)
        {
            if (m_properties == null)
                m_properties = new HashMap<String,Object>();
            Util.parseArgs(m_properties, strParams);
        }
        m_recordOwnerCollection = new RecordOwnerCollection(this);
    }
    /**
     * Free all the resources belonging to this task.
     */
    public void free()
    {
    	if (m_recordOwnerCollection != null)
    		m_recordOwnerCollection.free();
    	m_recordOwnerCollection = null;
        if (m_application != null)
            m_application.removeTask(this);   // Remove this session from the list
        m_application = null;
    }
    /**
     * Run this task.
     * Note: Be sure to call super!
     * Override this method to do something.
     */
    public void run()
    {
        this.runTask();
        this.free();
    }
    /**
     * Run this task.
     * Note: Be sure to call super!
     * Override this method to do something.
     */
    public void runTask()
    {
        // Add this code to run the job in the overriding class
    }
    /**
     * Get my parent application.
     */
    public Application getApplication()
    {
        return m_application;
    }
    /**
     * Get this property.
     * @param strProperty The property key to lookup.
     * @return The value for this property (or null if nor found).
     */
    public String getProperty(String strProperty)
    {
        String strValue = null;
        if (m_properties != null)
        {   // Try local properties
            if (m_properties.get(strProperty) != null)
                strValue = m_properties.get(strProperty).toString();
            if ((strValue != null) && (strValue.length() > 0))
                return strValue;
        }
        if (this.getApplication() != null)
            strValue = this.getApplication().getProperty(strProperty);  // Try app
        return strValue;
    }
    /**
     * Retrieve/Create a user properties record with this lookup key.
     * @param strPropertyCode The key I'm looking up.
     * @return The UserProperties for this registration key.
     */
    public PropertyOwner retrieveUserProperties(String strRegistrationKey)
    {
        if (this.getApplication() != null)
            return this.getApplication().retrieveUserProperties(strRegistrationKey);
        return null;
    }
    /**
     * This is task's parent application.
     * @param application The parent application.
     */
    public void setApplication(App application)
    {
        m_application = (Application)application;
    }
    /**
     * Set the properties.
     * @param strProperties The properties to set.
     */
    public void setProperties(Map<String, Object> properties)
    {
        m_properties = properties;
    }
    /**
     * Get the properties.
     * @return A <b>copy</b> of the properties in the propertyowner.
     */
    public Map<String, Object> getProperties()
    {
        return m_properties;
    }
    /**
     * Set this property.
     * @param strProperty The property key.
     * @param strValue The property value.
     */
    public void setProperty(String strProperty, String strValue)
    {
        if (m_properties == null)
            m_properties = new Hashtable<String,Object>();
        if (strValue != null)
            m_properties.put(strProperty, strValue);
        else
            m_properties.remove(strProperty);
    }
    /**
     * This is a special method that stops the code when this screen is opened as a task.
     */
    public void stopTask()
    {
        this.free();
    }
    /**
     * Is this task currently involved in computations?
     * @return True if the task is currently active.
     */
    public boolean isRunning()
    {
    	return false;
    }
    /**
     * If this task object was created from a class name, call init(xxx) for the task.
     * You may want to put logic in here that checks to make sure this object was not already inited.
     * Typically, you init a Task object and pass it to the job scheduler. The job scheduler
     * will check to see if this task is owned by an application... if not, initTask() is called.
     * @param application The parent application.
     * @param strParams The task properties.
     */
    public void initTask(App application, Map<String, Object> properties)
    {
        if (m_application != null)
            return;     // Never
        this.init((Application)application, null, properties);
    }
    /**
     * Get the last error code.
     * This call clears the last error code.
     * @param iErrorCode Pass the error code of the last error code or 0 to get the last error.
     * @return The error string for this code.
     */
    public String getLastError(int iErrorCode)
    {
        if ((m_strLastError == null) || ((iErrorCode != 0) && (iErrorCode != m_iLastErrorCode)))
            return Constants.BLANK;
        String string = m_strLastError;
        m_strLastError = null;
        return string;
    }
    /**
     * Set the last (next) error code to display.
     * @param strLastError The error text.
     * @return An error code that can be used later in the getLastError method.
     */
    public int setLastError(String strLastError)
    {
        m_strLastError = strLastError;
        if (m_iLastErrorCode > -2)
            m_iLastErrorCode = -2;
        return --m_iLastErrorCode;
    }
    /**
     * Display this status message in the status box or at the bottom of the browser.
     * @param strStatus The status message to display.
     */
    public void setStatusText(String strStatus)
    {
        this.setStatusText(strStatus, JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Display this status message in the status box or at the bottom of the browser.
     * @param strStatus The status message to display.
     * @param iWarningLevel If true, display a message box that the user must dismiss.
     */
    public void setStatusText(String strStatus, int iWarningLevel)
    {
        if (strStatus == null)
            strStatus = Constants.BLANK;
        m_strCurrentStatus = strStatus;
        m_iCurrentWarningLevel = iWarningLevel;
    }
    /**
     * Get the last status message if it is at this level or above.
     * Typically you do this to see if the current message you want to display can
     * be displayed on top of the message that is there already.
     * Calling this method will clear the last status text.
     * @param iWarningLevel The maximum warning level to retrieve.
     * @return The current message if at this level or above, otherwise return null.
     */
    public String getStatusText(int iWarningLevel)
    {
        String strStatus = m_strCurrentStatus;
        if (m_iCurrentWarningLevel < iWarningLevel)
            strStatus = null;
        return strStatus;
    }
    /**
     * Get the remote server for this task.
     * @return The remote server.
     */
    public Object getRemoteTask()
    {
        return  this.getApplication().getRemoteTask(this);
    }
    /**
     * Get the task for this record owner parent.
     * If this is a RecordOwner, return the parent task. If this is a Task, return this.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        return this;
    }
    /**
     * Convert this key to a localized string.
     * Here this method just calls the getString method in application.
     * @param strKey The key to lookup in the resource file.
     * @return The localized key.
     */
    public String getString(String strKey)
    {
        if (this.getApplication() != null)
            return this.getApplication().getString(strKey);
        return strKey;
    }
    /**
     * A utility method to get an Input stream from a filename or URL string.
     * @param strFilename The filename or url to open as an Input Stream.
     * @return The imput stream (or null if there was an error).
     */
    public InputStream getInputStream(String strFilename)
    {
        return Util.getInputStream(strFilename, this.getApplication());
    }
    /**
     * Can this task be the main task?
     * @return true If it can.
     */
    public boolean isMainTaskCandidate()
    {
        return false;    // All autotask sessions are NOT candidates for the main task.
    }
    /**
     * Get the default lock strategy to use for this type of table.
     * @return The lock strategy.
     */
    public int getDefaultLockType(int iDatabaseType)
    {
        if ((iDatabaseType & Constants.REMOTE) != 0)
            return Constants.OPEN_LAST_MOD_LOCK_TYPE;
        if ((iDatabaseType & Constants.LOCAL) != 0)
            return Constants.OPEN_NO_LOCK_TYPE;
        return Constants.OPEN_NO_LOCK_TYPE;
    }
    /**
     * Add this record owner to my list.
     * @param recordOwner The record owner to add
     */
    public boolean addRecordOwner(RecordOwnerParent recordOwner)
    {
    	return m_recordOwnerCollection.addRecordOwner(recordOwner);
    }
    /**
     * Remove this record owner to my list.
     * @param recordOwner The record owner to remove.
     */
    public boolean removeRecordOwner(RecordOwnerParent recordOwner)
    {
    	return m_recordOwnerCollection.removeRecordOwner(recordOwner);
    }
}
