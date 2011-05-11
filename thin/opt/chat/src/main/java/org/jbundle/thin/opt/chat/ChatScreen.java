package org.jbundle.thin.opt.chat;

/**
 * OrderEntry.java:   Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBasePanel;
import org.jbundle.thin.base.screen.JBaseToolbar;
import org.jbundle.thin.base.screen.message.RemoteMessageManager;


/**
 * Main Class for applet OrderEntry
 */
public class ChatScreen extends JBasePanel
{
    private static final long serialVersionUID = 1L;

    /**
     * The send button name/param.
     */
    public static final String SEND = "Send";
    /**
     * The remote queue name.
     */
    public static final String CHAT_TYPE = "chat";
    /**
     * The message property param.
     */
    public static final String MESSAGE_PARAM = "message";
    /**
     * The screen textin area.
     */
    protected JTextField m_tfTextIn = null;
    /**
     * The chat dialog output box.
     */
    protected JTextArea m_taChatDialog = null;

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
        BaseMessageReceiver receiver = (BaseMessageReceiver)messageManager.getMessageQueue(CHAT_TYPE, MessageConstants.INTRANET_QUEUE).getMessageReceiver();
        
        new BaseMessageListener(receiver)   // Listener automatically added to receiver
        {
            public int handleMessage(BaseMessage message)
            {
                String strMessage = (String)message.get(MESSAGE_PARAM);
                addText(strMessage);
                return Constants.NORMAL_RETURN;
            }
        };
    }
    /**
     * Add any screen sub-panel(s) now.
     * You might want to override this to create an alternate parent screen.
     * @param parent The parent to add the new screen to.
     * @return TODO
     */
    public boolean addSubPanels(Container parent)
    {
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
        JPanel topScreen = new JPanel();
        topScreen.setOpaque(false);
        topScreen.setLayout(new BoxLayout(topScreen, BoxLayout.Y_AXIS));
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
        
//        JScrollPane scrollpane = new JScrollPane(thinscreen);
        parent.add(topScreen);
        return true;
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
            BaseMessage message = new MapMessage(new BaseMessageHeader(CHAT_TYPE, MessageConstants.INTRANET_QUEUE, null, null), properties);
            this.getBaseApplet().getApplication().getMessageManager().sendMessage(message);
            return true;
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
