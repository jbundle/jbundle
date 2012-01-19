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

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.MainApplication;
import org.jbundle.base.util.UserProperties;
import org.jbundle.base.util.Utility;
import org.jbundle.model.App;
import org.jbundle.model.DBException;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.util.Application;


/**
 * TaskSession - Base task session.
 * This is the base top-level session for most tasks.
 * An application object is automatically created for a task session.
 * Note: A task session is usually only created once for an application.
 * For general-purpose tasks, use the RemoteTaskSessionObject.
 */
public class BaseTaskSession extends BaseSession
    implements Task
{

    /**
     * The parent application.
     */
    protected Application m_application = null;
    /*
     * My properties.
     */
    protected Map<String,Object> m_properties = null;
    /**
     * Last display message.
     */
    protected String m_strCurrentStatus = null;
    /**
     * Last display level.
     */
    protected int m_iCurrentWarningLevel = Constants.INFORMATION;
    /**
     * Required for the Task interface calls.
     */
    protected static int m_iLastErrorCode = -2;
    /**
     * The last error string set for this task.
     */
    protected String m_strLastError = Constants.BLANK;  // Last Error message

    /**
     * Build a new task session.
     */
    public BaseTaskSession() throws RemoteException
    {
        super();
    }
    /**
     * Build a new task session.
     * @param application Parent application (optional - usually take default [null]).
     */
    public BaseTaskSession(App application) throws RemoteException
    {
        this();
        m_application = (Application)application;    // Don't pass down, because init matched standard session init.
        this.init(null, null, null);
    }
    /**
     * Build a new task session.
     * @param parentSessionObject Parent that created this session object (usually null for task sessions).
     * @param record Main record for this session  (always null for task sessions).
     * @param objectID ObjectID of the object that this SessionObject represents  (usually null for task sessions).
     */
    public void init(BaseSession parentSessionObject, Record record, Map<String, Object> objectID)
    {
        if (m_application == null)
            m_application = new MainApplication(null, null, null);
        this.addToApplication();
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
        if (m_application != null)
            this.removeFromApplication(true);    // Remove this session from the list
    }
    /**
     * Add this new task or session to my parent application.
     */
    public void addToApplication()
    {
        m_application.addTask(this, null);
    }
    /**
     * Remove this task or session to my parent application.
     * @param bFreeIfDone If true and the application is empty, free the application.
     */
    public void removeFromApplication(boolean bFreeIfDone)
    {
        if (m_application == null)
            return; // In free.
        Application app = m_application;
        m_application = null;
        boolean bEmptyTaskList = app.removeTask(this);    // Remove this session from the list
        if (bFreeIfDone)
            if (bEmptyTaskList)
                app.free();     // No more tasks -> Quit app!
    }
    /**
     * Get the application for this task.
     * @return this task's application.
     */
    public Application getApplication()
    {
        return m_application; // I am the root task (I am the EJB Session root object)
    }
    /**
     * This is task's parent application.
     */
    public void setApplication(App application)
    {
        m_application = (Application)application;
    }
    /**
     * Get the task (In a TaskSession returns this).
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask()
    {
        return this;    // I am the root task (I am the EJB Session root object)
    }
    /**
     * Get this property.
     * @param strProperty Tke key for this property.
     * @return The value for this property key.
     */
    public String getProperty(String strProperty)
    {
        String strValue = null;
            // Task sessions rarely use local properties.
        if (m_properties != null)
        {   // Try local properties
            strValue = (String)m_properties.get(strProperty);
            if (strValue != null) //x && (strValue.length() > 0))
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
        if (DBParams.RETRIEVE_USER_PROPERTIES.equalsIgnoreCase(strCommand))
        {
            String strRegistrationKey = null;
            if (properties != null)
                strRegistrationKey = (String)properties.get(Constants.TIP);
            if (strRegistrationKey == null)
                strRegistrationKey = Params.SCREEN;
            PropertyOwner propertyOwner = this.retrieveUserProperties(strRegistrationKey);
            Map<String,Object> propertiesReturn = null;
            if (propertyOwner != null)
                propertiesReturn = propertyOwner.getProperties();
            if (propertyOwner instanceof UserProperties)    // Always
                ((UserProperties)propertyOwner).free();
            return propertiesReturn;
        }
        else if (DBParams.SAVE_USER_PROPERTIES.equalsIgnoreCase(strCommand))
        {
            String strRegistrationKey = null;
            if (properties != null)
                strRegistrationKey = (String)properties.get(Constants.TIP);
            if (strRegistrationKey == null)
                strRegistrationKey = Params.SCREEN;
            PropertyOwner propertyOwner = this.retrieveUserProperties(strRegistrationKey);
            if (propertyOwner instanceof UserProperties)    // Always
            {
                propertyOwner.setProperties(properties);
                ((UserProperties)propertyOwner).free();
            }
            return Integer.toString(Constants.NORMAL_RETURN);
        }
        return super.doRemoteCommand(strCommand, properties);   // Override this to handle this command
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
            m_properties = new Hashtable<String, Object>();
        if (strValue != null)
            m_properties.put(strProperty, strValue);
        else
            m_properties.remove(strProperty);
    }
    /**
     * This is a special method that runs some code when this screen is opened as a task.
     */
    public void run()
    {
        // This will never be called for a task
    }
    /**
     * This is a special method that stops the code when this screen is opened as a task.
     * For a TaskSession, this will only be called when the Server is shutting down.
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
     * (This is an error for a TaskSession).
     */
    public void initTask(App application, Map<String, Object> properties)
    {
        Utility.getLogger().warning("error: initTask() can never be called for a Task Session");
        new Exception().printStackTrace();
    }
    /**
     * Get the last error code.
     * This call clears the last error code.
     * @param iErrorCode Pass the error code of the last error code or 0 to get the last error.
     * @return The last error string.
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
     * @param strLastError The error string.
     * @return The error code to return, so I get this message.
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
     * @param strStatus The status text.
     */
    public void setStatusText(String strStatus)
    {
        this.setStatusText(strStatus, DBConstants.INFORMATION_MESSAGE);
    }
    /**
     * Display this status message in the status box or at the bottom of the browser.
     * This is not used for a TaskSession.
     * @param strStatus The status text.
     * @param bWarning If true, display a message box that the user must dismiss.
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
     * Should not be called for a Task session.
     * @return The application's server.
     */
    public Object getRemoteTask()
    {
        return  this.getApplication().getRemoteTask(this);
    }
    /**
     * Convert this key to a localized string.
     * In thin, this just calls the getLocalString method in application,
     * in thick, a local resource can be saved.
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
        InputStream inStream = Utility.getInputStream(strFilename, this.getApplication());
        if (inStream == null)
        	if (this.getParentSession() != null)
        { 
        	Task task = this.getParentSession().getTask();
        	if ((task != null) && (task != this))
        		inStream = task.getInputStream(strFilename);
        }
        return inStream;
    }
    /**
     * Can this task be the main task?
     * @return true If it can.
     */
    public boolean isMainTaskCandidate()
    {
        return true;    // All task session are candidates for the main task.
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
}
