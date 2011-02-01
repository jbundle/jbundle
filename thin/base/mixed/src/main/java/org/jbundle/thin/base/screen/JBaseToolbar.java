package org.jbundle.thin.base.screen;

/**
 * ErrorDialog.java:    Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.jbundle.thin.base.db.Constants;

/**
 * Screen Toolbar.
 */
public class JBaseToolbar extends JBaseScreen
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public JBaseToolbar()
    {
        super();
    }
    /**
     * Constructor.
     * @param parent The parent screen.
     * @param record (null for a toolbar).
     */
    public JBaseToolbar(Object parent, Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Constructor.
     * @param parent The parent screen.
     * @param record (null for a toolbar).
     */
    public void init(Object parent, Object record)
    {
        super.init(parent, record);
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.addButtons();
    }
    /**
     * Add the buttons to this window.
     * Override this to include buttons other than the default buttons.
     */
    public void addButtons()
    {
        this.addButton(Constants.BACK);
    }
    /**
     * Add a button to this window.
     * Convert this param to the local string and call addButton.
     * @param The key for this button.
     * @return TODO
     */
    public JButton addButton(String strParam)
    {
        String strDesc = strParam;
        BaseApplet applet = this.getBaseApplet();
        if (applet != null)
            strDesc = applet.getString(strParam);
        return this.addButton(strDesc, strParam);
    }
    /**
     * Add this button to this window.
     * @param strIcon The command and Icon name.
     * @param strLink The button name.
     * @return TODO
     */
    public JButton addButton(String strLink, String strIcon)
    {
        return this.addButton(strLink, strIcon, strIcon);
    }
    /**
     * Add this button to this window.
     * @param strIcon The command and Icon name.
     * @param strLink The button name.
     * @return TODO
     */
    public JButton addButton(String strLink, String strIcon, String strParam)
    {
        return this.addButton(strLink, strIcon, strParam, LAST);
    }
    public static final int LAST = -1;
    public static final int BEFORE_HELP = -2;
    /**
     * Add this button to this window.
     * @param strIcon The command and Icon name.
     * @param strLink The button name.
     * @return TODO
     */
    public JButton addButton(String strLink, String strIcon, String strParam, int iLocation)
    {
        ImageIcon icon = BaseApplet.getSharedInstance().loadImageIcon(strIcon);
        JButton button = new JButton(strLink, icon);
        button.setName(strParam);
        button.setOpaque(false);
        if (iLocation == LAST)
            this.add(button);
        else if (iLocation == BEFORE_HELP)
            this.add(button, this.getComponentCount() - 1);
        else
            this.add(button, iLocation);
        button.addActionListener(this);
        return button;
    }
    /**
     * Process this action.
     * Override this for functionality.
     * @param strAction The action command or message.
     * @return true if handled.
     */
    public boolean doAction(String strAction, int iOptions)
    {
    	return false;	// I don't directly handle any actions (but the other panels do).
    }
}
