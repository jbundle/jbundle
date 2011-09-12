/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.calendar;

/**
 * ErrorDialog.java:    Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.menu.JMenuToolbar;

/**
 * Status bar for displaying status in standalone windows.
 */
public class AppointmentToolbar extends JMenuToolbar
{
	private static final long serialVersionUID = 1L;

	public static final String APPOINTMENT = "Appointment";
    public static final String ANNIVERSARY = "Anniversary";

    /**
     * Constructor.
     */
    public AppointmentToolbar()
    {
        super();
    }
    /**
     * Constructor.
     */
    public AppointmentToolbar(Object parent,Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Add the buttons to this window.
     * Override this to include buttons other than the default buttons.
     */
    public void addButtons()
    {
        this.addButton(APPOINTMENT);
        this.addButton(ANNIVERSARY);
        this.addButton(Constants.DELETE);
        this.addButton(Constants.HELP);
    }
}
