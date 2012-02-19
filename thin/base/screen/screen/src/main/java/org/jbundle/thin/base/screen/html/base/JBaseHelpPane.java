/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.html.base;

import org.jbundle.thin.base.screen.JBasePanel;

/**
 * ErrorDialog.java:    Applet
 *  Copyright © 2012 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */

/**
 * Status bar for displaying status in standalone windows.
 */
public abstract class JBaseHelpPane extends JBasePanel
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public JBaseHelpPane()
    {
        super();
    }
    /**
     * JBasePanel Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public JBaseHelpPane(Object parent, Object record)
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
    }
    /**
     * Get the Help display panel.
     * @return The JHelpView component.
     */
    public abstract JBaseHtmlView getHelpView();
}
