/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.calendar;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import javax.swing.JComponent;

import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.JScreenToolbar;
import org.jbundle.thin.main.calendar.db.Appointment;


/**
 * Main Class for applet OrderEntry
 */
public class AppointmentScreen extends JScreen
{
	private static final long serialVersionUID = 1L;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public AppointmentScreen()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public AppointmentScreen(Object parent,Object obj)
    {
        this();
        this.init(parent, obj);
    }
    /**
     * The init() method is called by the AWT when an applet is first loaded or
     * reloaded.  Override this method to perform whatever initialization your
     * applet needs, such as initializing data structures, loading images or
     * fonts, creating frame windows, setting the layout manager, or adding UI
     * components.
     */
    public void init(Object parent, Object obj)
    {
        super.init(parent, obj);
        if (this.getFieldList().getTable() == null)
            ((BaseApplet)parent).linkNewRemoteTable(this.getFieldList());
    }
    /**
     * Build the list of fields that make up the screen.
     */
    public FieldList buildFieldList()
    {
        return new Appointment(null); // If overriding class didn't set this
    }
    /**
     * Add the toolbars?
     */
    public JComponent createToolbar()
    {
        return new JScreenToolbar(this, null);
    }
    /**
     * Get this field (or return null if this field doesn't belong on the screen).
     * This is the method to use to filter the items to display on the screen.
     * @param The index of this field in the record.
     * @return The fieldinfo object.
     */
    public Converter getFieldForScreen(int iIndex)
    {
        switch (iIndex)
        {
            case 0:
                return this.getFieldList().getField(Appointment.START_DATE_TIME).getFieldConverter();
            case 1:
                return this.getFieldList().getField(Appointment.END_DATE_TIME).getFieldConverter();
            case 2:
                return this.getFieldList().getField(Appointment.DESCRIPTION).getFieldConverter();
            default:
        }
        return null;
    }
    /**
     * Add the screen controls to the second column of the grid.
     * Create a default component for this fieldInfo.
     * @param fieldInfo the field to create a control for.
     * @return The component.
     */
    public JComponent createScreenComponent(Converter fieldInfo)
    {
        if ((Appointment.START_DATE_TIME.equalsIgnoreCase(fieldInfo.getFieldName()))
            || (Appointment.END_DATE_TIME.equalsIgnoreCase(fieldInfo.getFieldName())))
                return new org.jbundle.thin.base.screen.util.cal.JCalendarDualField(fieldInfo);
        return super.createScreenComponent(fieldInfo);
    }
}
