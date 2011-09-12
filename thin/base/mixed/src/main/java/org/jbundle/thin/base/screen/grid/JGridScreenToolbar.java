/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.grid;

/**
 * ErrorDialog.java:    Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.JBaseToolbar;

/**
 * The standard toolbar for grid screens.
 */
public class JGridScreenToolbar extends JBaseToolbar
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public JGridScreenToolbar()
    {
        super();
    }
    /**
     * Constructor.
     * @param parent The parent screen.
     * @param record (null for a toolbar).
     */
    public JGridScreenToolbar(Object parent, Object record)
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
     * Override this to include buttons other than the default buttons.
     */
    public void addButtons()
    {
        this.addButton(Constants.BACK);
        this.addButton(Constants.FORM);
        this.addButton(Constants.DELETE);
        this.addButton(Constants.HELP);
    }
}
