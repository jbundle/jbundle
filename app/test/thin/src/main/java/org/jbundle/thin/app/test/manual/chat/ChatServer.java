/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.chat;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.util.Hashtable;
import java.util.Map;


import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.screen.ThinApplication;


/**
 * Main Class for applet OrderEntry
 */
public class ChatServer extends ThinApplication
    implements ChatConstants
{
    /**
     * The message filter.
     */
    protected BaseMessageFilter m_messageFilter = null;
    /**
     * The current channel.
     */
    protected String m_strTree = ChatScreen.m_rgTrees[0];
    /**
     * The current room.
     */
    protected String m_strFilter = ChatScreen.m_rgFilters[0];
    /**
     * The current room.
     */
    protected String m_strOut = ChatScreen.m_strBack;
    /**
     * The current room.
     */
    protected String m_strBack = ChatScreen.m_strOut;

    /**
     *  Chat Screen Constructor.
     */
    public ChatServer()
    {
        super();
    }
    /**
     *  Chat Screen Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public ChatServer(String[] args)
    {
        this();
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        this.init(null, properties, null);
    }
    /**
     *  Chat Screen Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public void init(Object env, Map<String,Object> properties, Object applet)
    {
        super.init(env, properties, applet);
        if (this.registerUniqueApplication(CHAT_QUEUE_NAME, CHAT_QUEUE_TYPE) != Constants.NORMAL_RETURN)
        {
            this.free();    // Don't start this application (It's already running somewhere)
            return;
        }
        BaseMessageManager messageManager = this.getMessageManager();
        BaseMessageReceiver receiver = (BaseMessageReceiver)messageManager.getMessageQueue(CHAT_QUEUE_NAME, CHAT_QUEUE_TYPE).getMessageReceiver();
        
        m_messageFilter = new ChatMessageFilter(m_strTree, m_strBack, m_strFilter, null);
        new BaseMessageListener(m_messageFilter)    // Listener added to filter.
        {
            public int handleMessage(BaseMessage message)
            {
                String strMessage = (String)message.get(MESSAGE_PARAM);
                sendText(strMessage);
                return Constants.NORMAL_RETURN;
            }
        };
        receiver.addMessageFilter(m_messageFilter);
        
    }
    /**
     * Process this action.
     * @param strAction The action to process.
     * By default, this method handles RESET, SUBMIT, and DELETE.
     */
    public void sendText(String strMessage)
    {
        strMessage = "@@@" + strMessage + "@@@";
        Map<String,Object> properties = new Hashtable<String,Object>();
        properties.put(MESSAGE_PARAM, strMessage);
        BaseMessage message = new MapMessage(new ChatMessageHeader(m_strTree, m_strOut, m_strFilter, null), properties);
        this.getMessageManager().sendMessage(message);
    }
    /**
     *  The main() method acts as the applet's entry point when it is run
     *  as a standalone application. It is ignored if the applet is run from
     *  within an HTML page.
     */
    public static void main(String args[])
    {
        new ChatServer(args);
    }
}
