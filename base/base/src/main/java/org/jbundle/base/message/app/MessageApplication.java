/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.app;

import java.util.Map;

import org.jbundle.base.model.MessageApp;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.model.App;
import org.jbundle.thin.base.message.BaseMessageManager;


/**
 * A MessageApplication is the server application for all the message processing programs.
 * <p>
 * This Application is always running, waiting for incoming messages.
 * Once a message is received, the listeners list is checked to see if any processes require
 * notification. If not, the process list is checked to see if a process should be spawned to
 * process this message.
 * <p>
 * The task for a MessageApplication is typically an AutoTask or ProcessRunnerTask
 * and the RecordOwner is a MessageProcess.
 */
public class MessageApplication extends BaseApplication
    implements MessageApp
{
    public static final String AUTOSTART = "autostart";

    /**
     * The message manager for the application.
     */
    protected BaseMessageManager m_messageManager = null;

    /**
     * Default constructor.
     */
    public MessageApplication()
    {
        super();
    }
    /**
     * Constructor.
     * @param env Environment is ignored in the thin context.
     * @param strURL The application parameters as a URL.
     * @param args The application parameters as an initial arg list.
     * @param applet The application parameters coming from an applet.
     */
    public MessageApplication(Object env, Map<String,Object> properties, Object applet)
    {
        this();
        this.init(env, properties, applet);
    }
    /**
     * Initializes the MainApplication.
     * Usually you pass the object that wants to use this sesssion.
     * <p />Note: This will setup all the JMS servers if messageserver=true.
     * For example, the applet or MainApplication.
     * @param env Environment is ignored in the thin context.
     * @param strURL The application parameters as a URL.
     * @param args The application parameters as an initial arg list.
     * @param applet The application parameters coming from an applet.
     */
    public void init(Object env, Map<String,Object> properties, Object applet)
    {
        super.init(env, properties, applet);
    }
    /**
     * Release the user's preferences.
     */
    public void free()
    {
        if (m_messageManager != null)
            m_messageManager.free();
        m_messageManager = null;

        super.free();
    }
    /**
     * Get this Message Queue (or create one if this name doesn't exist).
     */
    public BaseMessageManager getThickMessageManager()
    {
        if (m_messageManager == null)
            m_messageManager = new ThickMessageManager(this, null, null);
        return m_messageManager;
    }
    /**
     * Set this property in the user's property area.
     * @param strProperty The key to lookup.
     * @return The property for this key.
     */
    public String getProperty(String strProperty)
    {
        String strValue = super.getProperty(strProperty);
        if (strValue == null)
        {
            App appDefault = this.getEnvironment().getDefaultApplication();
            if ((appDefault != null)
                && (appDefault != this))
                    strValue = appDefault.getProperty(strProperty);
        }
        return strValue;
    }
}
