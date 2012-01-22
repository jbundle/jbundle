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

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * A Process is a self-contained piece of code that does a particular process.
 * To run a process, just:
 * <pre>
 * If you are running from a screen, you may want to:
 * Process proc = new MyProcess(this.getTask(), recordMain, propertiesOptional);
 * process.run();
 * process.free();
 * If you want to run this as a separate thread, then you can do this:
 * String strJob = null;
 * strJob = Utility.addURLParam(strJob, DBParams.TASK, DBConstants.ROOT_PACKAGE + "app.thread.ProcessRunnerTask");  // Screen class
 * strJob = Utility.addURLParam(strJob, DBParams.PROCESS, DBConstants.ROOT_PACKAGE + "program.MyProcess");  // Screen class
 * strJob = Utility.addURLParam(strJob, "filename", "xyz");   // Some params
 * TaskScheduler.addTask(strJob);
 * </pre>
 */
public class BaseProcess extends BaseRecordOwner
    implements PropertyOwner    // and RecordOwner from BaseRecordOwner
{
    private static final long serialVersionUID = 1L;

    /**
     * Session object that made me.
     */
    protected Map<String,Object> m_properties = null;

    /**
     * Initialization.
     */
    public BaseProcess()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public BaseProcess(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        m_properties = properties;

        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free the resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Run the code in this process (you must override).
     */
    public void run()
    {
    }
    /**
     * Set the properties.
     * @param strProperties The properties to set.
     */
    public void setProperties(Map<String,Object> properties)
    {
        m_properties = properties;
    }
    /**
     * Get the properties.
     * @return A <b>copy</b> of the properties in the propertyowner.
     */
    public Map<String, Object> getProperties()
    {
        if (m_properties != null)
            return m_properties;
        return super.getProperties();
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
     * Get this property.
     */
    public String getProperty(String strProperty)
    {
        String strValue = null;
//  Add this code if Task sessions need local properties
        if (m_properties != null)
        {   // Try local properties
            if (m_properties.get(strProperty) != null)
                return m_properties.get(strProperty).toString();
        }
        if (this.getTask() != null)
            if (this.getTask().getApplication() != null)
                if (this.getTask().getApplication().getSystemRecordOwner() != this)
            strValue = this.getTask().getProperty(strProperty);   // Try app
        return strValue;
    }
    /**
     * Retrieve/Create a user properties record with this lookup key.
     * @param strPropertyCode The key I'm looking up.
     * @return The UserProperties for this registration key.
     */
    public PropertyOwner retrieveUserProperties(String strRegistrationKey)
    {
        if (this.getTask() != null)
        {
            if (this.getTask() instanceof PropertyOwner)
                return ((PropertyOwner)this.getTask()).retrieveUserProperties(strRegistrationKey);  // Try task
            if (this.getTask().getApplication() != null)
                return this.getTask().getApplication().retrieveUserProperties(strRegistrationKey);  // Try app
        }
        return null;    // Not found
    }
    /** A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: Be very careful as this code is running in an independent thread
     * (synchronize to the task before calling record calls).
     * NOTE: For now, you are only notified of the main record changes.
     */
    public int handleMessage(BaseMessage message)
    {
        return DBConstants.NORMAL_RETURN; // Override this to do something
    }
}
