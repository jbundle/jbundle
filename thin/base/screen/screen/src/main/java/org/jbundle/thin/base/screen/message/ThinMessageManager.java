/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.message;

import java.util.Map;
import java.util.Properties;

import org.jbundle.model.App;
import org.jbundle.model.message.MessageManager;
import org.jbundle.model.message.MessageReceiver;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.JMessageListener;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.message.event.FieldListMessageHandler;
import org.jbundle.thin.base.message.event.ModelMessageHandler;
import org.jbundle.thin.base.message.session.ClientSessionMessageFilter;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.screen.JBaseGridScreen;
import org.jbundle.thin.base.screen.JBaseScreen;


/**
 * The MessageManager organizes the message queues.
 * NOTE: This should probably implement the Task interface (Note: app is already passed in).
 */
public class ThinMessageManager extends BaseMessageManager
{

    /**
     * Constuctor.
     */
    public ThinMessageManager()
    {
        super();
    }
    /**
     * Constructor.
     * @param application The parent application.
     * @param strParams The task properties.
     */
    public ThinMessageManager(App application, String strParams, Map<String, Object> properties)
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
        super.init(application, strParams, properties);
    }
    /**
     * Free this message manager.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Create a screen message listener for this screen.
     */
    public static JMessageListener createScreenMessageListener(FieldList record, JBaseScreen screen)
    {
        return ThinMessageManager.createMessageListener(record, screen);
    }
    /**
     * Create a screen message listener for this screen.
     */
    public static JMessageListener createGridScreenMessageListener(FieldList record, JBaseGridScreen screen)
    {
        return ThinMessageManager.createMessageListener(record, screen);
    }
    /**
     * Create a screen message listener for this screen.
     */
    public static JMessageListener createMessageListener(FieldList record, JBaseScreen screen)
    {
        // Now add listeners to update screen when data changes
        FieldTable table = record.getTable();
        RemoteSession remoteSession = ((org.jbundle.thin.base.db.client.RemoteFieldTable) table).getRemoteTableType(org.jbundle.model.Remote.class);

        MessageManager messageManager = screen.getBaseApplet().getApplication().getMessageManager();
        MessageReceiver handler = messageManager.getMessageQueue(MessageConstants.RECORD_QUEUE_NAME, MessageConstants.INTRANET_QUEUE).getMessageReceiver();

        JMessageListener listenerForSession = null;
        Properties properties = new Properties();
        if (screen instanceof JBaseGridScreen)
        {
            listenerForSession = new ModelMessageHandler(null, ((JBaseGridScreen)screen).getGridModel());
            properties.setProperty(MessageConstants.CLASS_NAME, MessageConstants.GRID_FILTER);
        }
        else
        {
            listenerForSession = new FieldListMessageHandler(record);
            properties.setProperty(MessageConstants.CLASS_NAME, MessageConstants.RECORD_FILTER);
        }

        BaseMessageFilter filterForSession = new ClientSessionMessageFilter(MessageConstants.RECORD_QUEUE_NAME, MessageConstants.INTRANET_QUEUE, screen, remoteSession, properties);
        filterForSession.addMessageListener(listenerForSession);
        handler.addMessageFilter(filterForSession);
        
        return listenerForSession;
    }
    /**
     * Cleanup.
     */
    public static void freeScreenMessageListeners(JBaseScreen screen)
    {
        BaseMessageManager messageManager = (BaseMessageManager)screen.getBaseApplet().getApplication().getMessageManager();
        messageManager.freeListenersWithSource(screen);
        messageManager.freeFiltersWithSource(screen);
    }
}
