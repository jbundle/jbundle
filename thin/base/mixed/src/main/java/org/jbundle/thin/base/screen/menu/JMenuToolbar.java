package org.jbundle.thin.base.screen.menu;

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
 * A standard menu toolbar.
 */
public class JMenuToolbar extends JBaseToolbar
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public JMenuToolbar()
    {
        super();
    }
    /**
     * Constructor.
     * @param parent The parent screen.
     * @param record (null for a toolbar).
     */
    public JMenuToolbar(Object parent, Object record)
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
        this.addButton(Constants.HELP);
    }
}
