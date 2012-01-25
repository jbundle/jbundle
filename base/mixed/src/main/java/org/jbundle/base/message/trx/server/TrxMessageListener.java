/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.server;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.app.MessageApplication;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.Utility;
import org.jbundle.model.App;
import org.jbundle.model.Task;
import org.jbundle.model.main.msg.db.MessageProcessInfoModel;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * A MessageProcessRunner waits around for the message. When a message comes through on this
 * queue, the correct task is created and run. If another message is waiting, the process is run
 * again. When the last message is received, the process is freed.
 */
public class TrxMessageListener extends BaseMessageListener
{
    /**
     * My parent application.
     */
    protected Application m_application = null;
    /**
     * The default process class name to run.
     */
    protected String m_strProcessClassName = null;
    /**
     * Properties.
     */
    protected Map<String,Object> m_properties = null;

    /**
     * Constructor.
     */
    public TrxMessageListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TrxMessageListener(BaseMessageFilter messageFilter, Application application, String strProcessClassName, Map<String, Object> properties)
    {
        this();
        this.init(messageFilter, application, strProcessClassName, properties);
    }
    /**
     * Constructor.
     */
    public void init(BaseMessageFilter messageFilter, Application application, String strProcessClassName, Map<String, Object> properties)
    {
        m_application = application;
        m_strProcessClassName = strProcessClassName;
        m_properties = properties;
        super.init(null, messageFilter);
    }
    /**
     * Constructor.
     */
    public void free()
    {
        super.free();
        m_application = null; 
        m_strProcessClassName = null;
    }
    /**
     * Handle this message.
     * Get the name of this process and run it.
     */
    public int handleMessage(BaseMessage message)
    {
        String strClassName = this.getMessageProcessorClassName(message);
        if ((strClassName == null) || (strClassName.length() == 0))
            return this.handleOtherMessage(message);
        message.consume();      // I'll be handling this one.
        String strParams = Utility.addURLParam(null, DBParams.PROCESS, strClassName);
        App application = m_application;
        if (message.getProcessedByClientSession() instanceof RemoteTask)
            if (message.getProcessedByClientSession() instanceof Task)  // Always
                application = ((Task)message.getProcessedByClientSession()).getApplication();    // If I have the task session, run this task under the same app
        /* Don't need to do this since the message client was created by the calling client (with correct db params)
        Map<String, Object> messageDBProperties = BaseDatabase.addDBProperties(null, application, message.getMessageHeader().getProperties());
        boolean dbChanged = false;
    	for (String key : messageDBProperties.keySet())
    	{	// Merged set of application and message db properties
    		if ((message.getMessageHeader().get(key) == null)
    			|| (!message.getMessageHeader().get(key).equals(application.getProperty(key))))
    					dbChanged = true;
        }
		if (dbChanged)
			if (application instanceof BaseApplication)	// Always
		{	// If the db properties are different, need a new app (ouch)
		    	if (application.getProperties() != null)
		    	{
			    	for (String key : application.getProperties().keySet())
			    	{	// Merged set of application and message db properties
			    		if (!application.getProperty(key).equals(message.getMessageHeader().get(key)))
			    			messageDBProperties.put(key, application.getProperty(key));
			        }
		    	}
		    	application = new MessageInfoApplication(((BaseApplication)application).getEnvironment(), messageDBProperties, null);
		}
		*/
        MessageProcessRunnerTask task = new MessageProcessRunnerTask(application, strParams, null);
        task.setMessage(message);
        m_application.getTaskScheduler().addTask(task);
        return DBConstants.NORMAL_RETURN;   // No need to call super.
    }
    /**
     * Get the message processor class name.
     * @return The message processor task name.
     */
    public String getMessageProcessorClassName(BaseMessage message)
    {
        String strClass = (String)message.getMessageHeader().get(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS);
        if ((strClass == null)
            || (strClass.length() == 0))
        {
            String strMessageCode = (String)message.getMessageHeader().get(TrxMessageHeader.MESSAGE_CODE);
            if ((strMessageCode == null)
                || (strMessageCode.length() == 0))
                    strMessageCode = (String)message.get(TrxMessageHeader.MESSAGE_CODE);
            if (strMessageCode != null)
                if (strMessageCode.length() > 0)
                    if (message instanceof BaseMessage)
            {
                MessageProcessInfoModel recMessageProcessInfo = (MessageProcessInfoModel)Record.makeRecordFromClassName(MessageProcessInfoModel.THICK_CLASS, (RecordOwner)m_application.getSystemRecordOwner()); // todo(don) Not the most efficent sharing! Note: Remember the import
                recMessageProcessInfo.setupMessageHeaderFromCode((BaseMessage)message, strMessageCode, null);
                recMessageProcessInfo.free();
                strClass = (String)message.getMessageHeader().get(TrxMessageHeader.MESSAGE_PROCESSOR_CLASS);
            }
        }
        String strPackage = (String)message.getMessageHeader().get(TrxMessageHeader.BASE_PACKAGE);
        if (strPackage != null)
            if (strPackage.length() > 0)
                if (strPackage.charAt(strPackage.length() - 1) != '.')
                    strPackage = strPackage + '.';
        if ((strClass == null) || (strClass.length() == 0))
            strClass = m_strProcessClassName;
        if (strClass != null)
            if (strClass.length() > 0)
        {
            if (strClass.indexOf('.') == -1)
                if (strPackage != null)
                    strClass = strPackage + strClass;
            if (strClass.charAt(0) == '.')
                strClass = DBConstants.ROOT_PACKAGE + strClass.substring(1);
        }
        return strClass;
    }
    /**
     * Handle non-process message (such as messages that start an application).
     * @param message
     * @return
     */
    public int handleOtherMessage(BaseMessage message)
    {
        // Special case - This class can also be used to start an application
        if (m_properties != null)
            if (m_properties.get(DBParams.PROCESS) == null)
                if (m_properties.get("standalone") == null)
        {
            if ((message.getMessageHeader().get(REMOVE_LISTENER) != null)
                || (message.get(REMOVE_LISTENER) != null))
            {   // Special message to get rid of this listener (typically send by server app when it is created)
                this.free();
                message.consume();
            }
            else if (m_properties.get(DBParams.APPLICATION) != null)
            {
                String strApplicationClass = (String)m_properties.get(DBParams.APPLICATION);
                Application app  = (Application)ClassServiceUtility.getClassService().makeObjectFromClassName(strApplicationClass);
                if (app != null)
                {
                    Environment env = ((BaseApplication)m_application).getEnvironment();
                    if (m_properties.get(MessageConstants.QUEUE_NAME) == null)
                        m_properties.put(MessageConstants.QUEUE_NAME, message.getMessageHeader().getQueueName());
                    if (m_properties.get(MessageConstants.QUEUE_TYPE) == null)
                        m_properties.put(MessageConstants.QUEUE_TYPE, message.getMessageHeader().getQueueType());
                    m_properties.remove(DBParams.APPLICATION);
                    m_properties.remove(MessageApplication.AUTOSTART);
                    app.init(env, m_properties, null);
                    
                    // Don't listen any more (Since application will be listening)!
                    this.free();
                    boolean bMessageSent = false;
                    for (int i = 1; ; i++)
                    {
                        String strProcessClass = (String)((MapMessage)message).get(DBParams.PROCESS + Integer.toString(i));
                        if (strProcessClass == null)
                            break;  // End of initial processes
                        bMessageSent = true;
                        Map<String,Object> properties = new HashMap<String,Object>();
                        if (message instanceof MapMessage)  // Always
                        {
                            Map<String,Object> propMessage = (Map)message.getData();
                            if (propMessage != null)
                                properties.putAll(propMessage);
                        }
                        properties.remove(DBParams.APPLICATION);
                        properties.remove(MessageApplication.AUTOSTART);
                        properties.put(DBParams.PROCESS, strProcessClass);
                        BaseMessage messageInitial = new MapMessage(new BaseMessageHeader(message.getMessageHeader().getQueueName(), message.getMessageHeader().getQueueType(), this, null), properties);
                        env.getMessageManager(m_application, true).sendMessage(messageInitial);   // Resend it! (Don't consume it!)
                    }
                    if (!bMessageSent)
                        env.getMessageManager(m_application, true).sendMessage(message);   // Resend it! (Don't consume it!)
                }
            }
        }
        return DBConstants.NORMAL_RETURN;
    }
    public static final String REMOVE_LISTENER = "removeListener";
}
