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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBasePanel;
import org.jbundle.thin.base.screen.JBaseToolbar;
import org.jbundle.thin.base.screen.message.RemoteMessageManager;


/**
 * Main Class for applet OrderEntry
 */
public class ChatScreen extends JBasePanel
    implements ChatConstants
{
    private static final long serialVersionUID = 1L;

    /**
     * The screen textin area.
     */
    protected JTextField m_tfTextIn = null;
    /**
     * The chat dialog output box.
     */
    protected JTextArea m_taChatDialog = null;
    /**
     * The topic popup box.
     */
    protected JComboBox m_popupTree = null;
    /**
     * The topic popup box.
     */
    protected JComboBox m_popupFilter = null;
    /**
     * The message filter.
     */
    protected BaseMessageFilter m_messageFilter = null;
    /**
     * The current channel.
     */
   public static String[] m_rgTrees = {"Spruce", "Redwood", "Maple"};
    /**
     * The current channel.
     */
    protected String m_strTree = m_rgTrees[0];
    /**
     * The current channel.
     */
   public static String[] m_rgFilters = {"Paper", "Cloth", "Charcoal"};
    /**
     * The current room.
     */
    protected String m_strFilter = m_rgFilters[0];
    /**
     * The current room.
     */
    public static String m_strOut = "out";
    /**
     * The current room.
     */
    public static String m_strBack = "back";
    /**
     * Server checkbox.
     */
    public JCheckBox m_serverCheckbox = null;

    /**
     *  Chat Screen Constructor.
     */
    public ChatScreen()
    {
        super();
    }
    /**
     *  Chat Screen Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public ChatScreen(Object parent, Object obj)
    {
        this();
        this.init(parent, obj);
    }
    /**
     *  Chat Screen Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object obj)
    {
        super.init(parent, obj);
        BaseApplet baseApplet = (BaseApplet)parent;
        
        this.addSubPanels(this);

        BaseMessageManager messageManager = baseApplet.getApplication().getMessageManager();
        BaseMessageReceiver receiver = (BaseMessageReceiver)messageManager.getMessageQueue(CHAT_QUEUE_NAME, CHAT_QUEUE_TYPE).getMessageReceiver();
        
        m_messageFilter = new ChatMessageFilter(m_strTree, m_strBack, m_strFilter, null);
        new BaseMessageListener(m_messageFilter)   // Listener automatically added to receiver
        {
            public int handleMessage(BaseMessage message)
            {
                String strMessage = (String)message.get(MESSAGE_PARAM);
                addText(strMessage);
                return Constants.NORMAL_RETURN;
            }
        };
        receiver.addMessageFilter(m_messageFilter);
    }
    /**
     * Add any screen sub-panel(s) now.
     * You might want to override this to create an alternate parent screen.
     * @param parent The parent to add the new screen to.
     */
    public boolean addSubPanels(Container parent)
    {
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
        
        JPanel topScreen = new JPanel();
        topScreen.setOpaque(false);
        topScreen.setLayout(new BoxLayout(topScreen, BoxLayout.Y_AXIS));
        
        topScreen.add(m_popupTree = this.setupTreePopup());
        
        topScreen.add(m_popupFilter = this.setupFilterPopup());
        
        topScreen.add(m_serverCheckbox = this.setupServerCheckbox());

        m_taChatDialog = new JTextArea();
        m_taChatDialog.setOpaque(false);
        m_taChatDialog.setBorder(new LineBorder(Color.black));
        topScreen.add(m_taChatDialog);
        
        JPanel panelBottom = new JPanel();
        panelBottom.setOpaque(false);
        topScreen.add(panelBottom);
        
        panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.X_AXIS));
        m_tfTextIn = new JTextField()
        {
            private static final long serialVersionUID = 1L;

            public Dimension getMaximumSize()
            {
                return new Dimension(super.getMaximumSize().width, this.getPreferredSize().height);
            }
        };
        panelBottom.add(m_tfTextIn);
        ImageIcon icon = this.getBaseApplet().loadImageIcon(SEND);
        String strText = this.getBaseApplet().getString(SEND);
        JButton button = new JButton(strText, icon);
        button.setOpaque(false);
        button.setName(SEND);
        panelBottom.add(button);
        button.addActionListener(this);
        
        parent.add(topScreen);
        
        return true;
    }
    /**
     * Setup the pull-down control.
     */
    public JComboBox setupTreePopup()
    {
        JComboBox component = new JComboBox(m_rgTrees);
        component.setName(TREE_NAME);
        component.addActionListener(this);
        return component;
    }
    /**
     * Setup the pull-down control.
     */
    public JComboBox setupFilterPopup()
    {
        JComboBox component = new JComboBox(m_rgFilters);
        component.setName(FILTER_NAME);
        component.addActionListener(this);
        return component;
    }
    /**
     * Setup the pull-down control.
     */
    public JCheckBox setupServerCheckbox()
    {
        JCheckBox component = new JCheckBox("Route messages through server", true);
        component.setName(DOOR_NAME);
        component.setOpaque(false);
        component.addActionListener(this);
        return component;
    }
    /**
     * Process this action.
     * @param strAction The action to process.
     * By default, this method handles RESET, SUBMIT, and DELETE.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        if (SEND.equalsIgnoreCase(strAction))
        {
            String strMessage = m_tfTextIn.getText();
            m_tfTextIn.setText(Constants.BLANK);

            Map<String,Object> properties = new Hashtable<String,Object>();
            properties.put(MESSAGE_PARAM, strMessage);
            BaseMessage message = new MapMessage(new ChatMessageHeader(m_strTree, m_strOut, m_strFilter, null), properties);
            this.getBaseApplet().getApplication().getMessageManager().sendMessage(message);
            return true;
        }
        else if (TREE_NAME.equalsIgnoreCase(strAction))
        {
            m_strTree = (String)m_popupTree.getSelectedItem();
            m_messageFilter.updateFilterTree(TREE_NAME, m_strTree);
        }
        else if (FILTER_NAME.equalsIgnoreCase(strAction))
        {
            m_strFilter = (String)m_popupFilter.getSelectedItem();
            Hashtable<String,Object> properties = new Hashtable<String,Object>();
            properties.put(FILTER_NAME, m_strFilter);
            m_messageFilter.updateFilterMap(properties);
        }
        else if (DOOR_NAME.equalsIgnoreCase(strAction))
        {
            if (m_serverCheckbox.isSelected())
            {
                m_strBack = "back";
                m_strOut = "out";
            }
            else
            {
                m_strBack = "peer";
                m_strOut = "peer";
            }
            m_messageFilter.updateFilterTree(DOOR_NAME, m_strBack);
        }
        return super.doAction(strAction, iOptions);
    }
    /**
     *
     */
    public void addText(String strMessage)
    {
        int offset = m_taChatDialog.getDocument().getLength();
        try {
            m_taChatDialog.getDocument().insertString(offset, strMessage + '\n', null);
        } catch (BadLocationException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     * Add the toolbars?
     * @return The newly created toolbar or null.
     */
    public JComponent createToolbar()
    {
        return new JBaseToolbar(this, null);
    }
}
