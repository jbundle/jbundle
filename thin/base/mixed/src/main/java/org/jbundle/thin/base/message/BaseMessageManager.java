package org.jbundle.thin.base.message;

import java.util.Hashtable;
import java.util.Properties;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.message.event.FieldListMessageHandler;
import org.jbundle.thin.base.message.event.ModelMessageHandler;
import org.jbundle.thin.base.message.session.ClientSessionMessageFilter;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.screen.JBaseScreen;
import org.jbundle.thin.base.screen.grid.JGridScreen;
import org.jbundle.thin.base.util.Application;


/**
 * The MessageManager organizes the message queues.
 * NOTE: This should probably implement the Task interface (Note: app is already passed in).
 */
public class BaseMessageManager extends Object
{
    /**
     * My parent application.
     */
    protected Application m_app = null;
    /**
     * My Message queues.
     */
    protected Hashtable<String,BaseMessageQueue> m_messageMap = null;

    /**
     * Constuctor.
     */
    public BaseMessageManager()
    {
        super();
    }
    /**
     * Constuctor.
     * @param app My parent application.
     */
    public BaseMessageManager(Application app)
    {
        this();
        this.init(app);
    }
    /**
     * Constuctor.
     * @param app My parent application.
     */
    public void init(Application app)
    {
        m_app = app;
        m_messageMap = new Hashtable<String,BaseMessageQueue>();
    }
    /**
     * Free this message manager.
     */
    public void free()
    {
        if (m_messageMap != null)
        {
            for (BaseMessageQueue messageQueue : m_messageMap.values())
            {
                if (messageQueue != null)
                {       // Don't worry about removing these, since you are removing them all.
                    messageQueue.setMessageManager(null);   // Make sure it doesn't try to remove itself again
                    messageQueue.free();
                }                
            }
        }
        m_messageMap = null;
        m_app = null;
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed.
     */
    public void freeFiltersWithListener(JMessageListener listener)
    {
        if (m_messageMap != null)
        {
            for (BaseMessageQueue messageQueue : m_messageMap.values())
            {
                if (messageQueue != null)
                    messageQueue.freeFiltersWithListener(listener);
            }
        }
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed.
     */
    public void freeFiltersWithSource(Object objSource)
    {
        if (m_messageMap != null)
        {
            for (BaseMessageQueue messageQueue : m_messageMap.values())
            {
                if (messageQueue != null)
                    messageQueue.freeFiltersWithSource(objSource);
            }
        }
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed.
     */
    public void freeListenersWithSource(Object objSource)
    {
        if (m_messageMap != null)
        {
            for (BaseMessageQueue messageQueue : m_messageMap.values())
            {
                if (messageQueue != null)
                    messageQueue.freeListenersWithSource(objSource);
            }
        }
    }
    /**
     * Get this Message Queue (or create one if this name doesn't exist).
     * Override this to supply the correct message queue if it hasn't already been built.
     * @param strQueueName The queue name to lookup.
     * @param strQueueType The queue type if this queue needs to be created.
     * @return The message queue.
     */
    public BaseMessageQueue getMessageQueue(String strQueueName, String strQueueType)
    {   // Look up the message Queue!
        return (BaseMessageQueue)m_messageMap.get(strQueueName);
    }
    /**
     * Add this message queue to the map.
     * Note: Don't call this, it is called from the MessageQueue constructor.
     * @param messageQueue The message queue to add to my list.
     */
    public void addMessageQueue(BaseMessageQueue messageQueue)
    {
        m_messageMap.put(messageQueue.getQueueName(), messageQueue);
    }
    /**
     * Remove this message queue from the map.
     * Note: Don't call this, it is called from the MessageQueue constructor.
     * @param messageQueue The message queue to remove from my list.
     */
    public void removeMessageQueue(BaseMessageQueue messageQueue)
    {
        m_messageMap.remove(messageQueue.getQueueName());
    }
    /**
     * Get the application for this task.
     * @return My parent application.
     */
    public Application getApplication()
    {
        return m_app;
    }
    /**
     * Add this message filter to the appropriate queue.
     * The message filter contains the queue name and type and a listener to send the message to.
     * @param messageFilter The message filter to add.
     * @return An error code.
     */
    public int addMessageFilter(BaseMessageFilter messageFilter)
    {
        BaseMessageReceiver receiver = this.getMessageQueue(messageFilter.getQueueName(), messageFilter.getQueueType()).getMessageReceiver();
        receiver.addMessageFilter(messageFilter);
        return Constants.NORMAL_RETURN;
    }
    /**
     * Send this message to the appropriate queue.
     * The message's message header has the queue name and type.
     * @param The message to send.
     * @return An error code.
     */
    public int sendMessage(BaseMessage message)
    {
        BaseMessageHeader messageHeader = message.getMessageHeader();
        String strQueueType = messageHeader.getQueueType();
        String strQueueName = messageHeader.getQueueName();
        BaseMessageSender sender = this.getMessageQueue(strQueueName, strQueueType).getMessageSender();
        if (sender != null)
            sender.sendMessage(message);
        else
            return Constants.ERROR_RETURN;  // Queue doesn't exist
        return Constants.NORMAL_RETURN;
    }
    /**
     * Create a screen message listener for this screen.
     */
    public static JMessageListener createScreenMessageListener(FieldList record, JBaseScreen screen)
    {
        return BaseMessageManager.createMessageListener(record, screen);
    }
    /**
     * Create a screen message listener for this screen.
     */
    public static JMessageListener createGridScreenMessageListener(FieldList record, JGridScreen screen)
    {
        return BaseMessageManager.createMessageListener(record, screen);
    }
    /**
     * Create a screen message listener for this screen.
     */
    public static JMessageListener createMessageListener(FieldList record, JBaseScreen screen)
    {
        // Now add listeners to update screen when data changes
        FieldTable table = record.getTable();
        RemoteSession remoteSession = ((org.jbundle.thin.base.db.client.RemoteFieldTable) table).getRemoteTableType(java.rmi.server.RemoteStub.class);

        BaseMessageManager messageManager = screen.getBaseApplet().getApplication().getMessageManager();
        BaseMessageReceiver handler = messageManager.getMessageQueue(MessageConstants.RECORD_QUEUE_NAME, MessageConstants.INTRANET_QUEUE).getMessageReceiver();

        JMessageListener listenerForSession = null;
        Properties properties = new Properties();
        if (screen instanceof JGridScreen)
        {
            listenerForSession = new ModelMessageHandler(null, ((JGridScreen)screen).getGridModel());
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
        BaseMessageManager messageManager = screen.getBaseApplet().getApplication().getMessageManager();
        messageManager.freeListenersWithSource(screen);
        messageManager.freeFiltersWithSource(screen);
    }
}
