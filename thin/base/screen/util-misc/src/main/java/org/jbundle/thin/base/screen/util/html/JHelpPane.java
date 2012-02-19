/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util.html;

/**
 * ErrorDialog.java:    Applet
 *  Copyright © 2012 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseToolbar;
import org.jbundle.thin.base.screen.html.base.JBaseHelpPane;
import org.jbundle.thin.base.util.ThinMenuConstants;

/**
 * Status bar for displaying status in standalone windows.
 */
public class JHelpPane extends JBaseHelpPane
{
	private static final long serialVersionUID = 1L;

    protected JHelpView m_helpView = null;
    protected JComponent m_iconArea = null;

    /**
     * Constructor.
     */
    public JHelpPane()
    {
        super();
    }
    /**
     * JBasePanel Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public JHelpPane(Object parent, Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object record)
    {
    	super.init(parent, record);
    	
        this.setOpaque(false);
        this.setBorder(new EtchedBorder());
        
        this.setLayout(new BorderLayout());
        this.add(m_helpView = new JHelpView(this, null), BorderLayout.CENTER);
        this.add(m_iconArea = new JBaseToolbar(this, null)
	        {
	            private static final long serialVersionUID = 1L;
	            /**
	             * Add the buttons to this window.
	             * Override this to include buttons other than the default buttons.
	             */
	            public void addButtons()
	            {
	               //super.addButtons();
	               //addButton(ThinMenuConstants.HOME);
	               JButton button = addButton(null, ThinMenuConstants.CLOSE, ThinMenuConstants.CLOSE);
	               button.setOpaque(false);
	               button.setBorder(new LineBorder(Color.black, 1));
	             }
	        }, BorderLayout.NORTH);
        m_iconArea.setOpaque(false);
        
        m_helpView.getHtmlEditor().setHelpPane(this);
    }
    /**
     * Do some applet-wide action.
     * For example, submit or reset.
     * Here are how actions are handled:
     * When a BasePanel receives a command it calls it's doAction method. If the doAction
     * doesn't handle the action, it is passed to the parent's doAction method, until it
     * hits the (this) applet method. This applet method tries to pass the command to all
     * sub-BasPanels until one does the action.
     * For example, submit or reset. Pass this action down to all the JBaseScreens.
     * Do not override this method, override the handleAction method in the JBasePanel.
     * @param strAction The command to pass to all the sub-JBasePanels.
     * @param source The source component
     * @param parent The parent to start the sub-search from (non-inclusive).
     * @return true If handled.
     */
    public boolean handleAction(String strAction, Component source, int iOptions)
    {
        if (source == this.getParent())
            return false;   // Don't handle commands coming from the outside
        if (source.getParent() == this)
            source = this.getParent();  // This will keep my commands from being passed to my parent
        return super.handleAction(strAction, source, iOptions);
    }
    /**
     * Process this action.
     * Override this for functionality.
     * @param strAction The action command or message.
     * @return true if handled.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        if (Constants.CLOSE.equalsIgnoreCase(strAction))
        {
            this.linkActivated(null, null);
            return true;	// Don't let anyone else handle my actions
        }
    	return super.doAction(strAction, iOptions);
    }
    /**
     * Get the Help display panel.
     * @return The JHelpView component.
     */
    public JHelpView getHelpView()
    {
    	return m_helpView;
    }
    /**
     * Follows the reference in an
     * link.  The given url is the requested reference.
     * By default this calls <a href="#setPage">setPage</a>,
     * and if an exception is thrown the original previous
     * document is restored and a beep sounded.  If an 
     * attempt was made to follow a link, but it represented
     * a malformed url, this method will be called with a
     * null argument.
     *
     * @param url the URL to follow
     */
    public void linkActivated(URL url, BaseApplet applet)
    {
    	this.getHelpView().linkActivated(url, applet);
    }
    /**
     * Preferred panel size.
     * @return The preferred help panel width.
     */
    public Dimension getPreferredSize()
    {
    	if (this.getParent() != null)
        	return new Dimension(this.getParent().getWidth() / 3, super.getPreferredSize().height);
    	return super.getPreferredSize();
    }
}
