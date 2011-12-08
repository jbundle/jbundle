/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen;

/**
 * ErrorDialog.java:    Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import org.jbundle.thin.base.db.Constants;
/**
 * Status bar for displaying status in standalone windows.
 */
public class JScreenToolbar extends JBaseToolbar
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public JScreenToolbar()
    {
        super();
    }
    /**
     * Constructor.
     * @param parent The parent screen.
     * @param record (null for a toolbar).
     */
    public JScreenToolbar(Object parent, Object record)
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
    }
    /**
     * Add the buttons to this window.
     */
    public void addButtons()
    {
        this.addButton(Constants.BACK);
        this.addButton(Constants.GRID);
        this.addButton(Constants.SUBMIT);
        this.addButton(Constants.RESET);
        this.addButton(Constants.DELETE);
        this.addButton(Constants.HELP);
    }
}
