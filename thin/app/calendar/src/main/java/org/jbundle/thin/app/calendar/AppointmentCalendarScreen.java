/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.calendar;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.JComponent;

import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseScreen;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.cal.grid.JCalendarScreen;
import org.jbundle.util.calendarpanel.CalendarPanel;
import org.jbundle.util.calendarpanel.event.MyListSelectionEvent;
import org.jbundle.util.calendarpanel.event.MyListSelectionListener;
import org.jbundle.util.calendarpanel.model.CalendarModel;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.main.calendar.db.Anniversary;


/**
 * A Basic order entry screen.
 * This screen is made of a panel with a GridBagLayout. Labels in the first column, aligned right.
 * Data fields in the second column aligned left.
 */
public class AppointmentCalendarScreen extends JCalendarScreen
    implements MyListSelectionListener
{
	private static final long serialVersionUID = 1L;

	/**
     *  JBaseScreen Class Constructor.
     */
    public AppointmentCalendarScreen()
    {
        super();
    }
    /**
     *  JBaseScreen Class Constructor.
     * Typically, you pass the BaseApplet as the parent, and the record or GridTableModel as the parent.
     */
    public AppointmentCalendarScreen(Object parent,Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     */
    public void init(Object parent, Object record)
    {
        super.init(parent, record);
        
        this.getCalendarModel(null).addMySelectionListener(this);
    }
    /**
     * Build the list of fields that make up the screen.
     */
    public FieldList buildFieldList()
    {
        AppointmentCalendarItem record = new AppointmentCalendarItem(this);
        BaseApplet.getSharedInstance().linkNewRemoteTable(record, true);
        return record;
    }
    /**
     * Add the toolbars?
     */
    public JComponent createToolbar()
    {
        return new AppointmentToolbar(this, null);
    }
    /**
     * Process this action.
     * @param strAction The command to execute.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        JBaseScreen screen = null;
        if (AppointmentToolbar.APPOINTMENT.equalsIgnoreCase(strAction))
        {
            screen = new AppointmentScreen(); // Note: I did not initialize the class by passing the params
        }
        if (AppointmentToolbar.ANNIVERSARY.equalsIgnoreCase(strAction))
        {
            screen = new AnniversaryScreen(); // Note: I did not initialize the class by passing the params
        }
        if (Constants.DELETE.equalsIgnoreCase(strAction))
        {
            CalendarPanel panel = (CalendarPanel)this.getComponent(0);
            ActionEvent event = new ActionEvent(this, 0, Constants.DELETE);
            panel.actionPerformed(event);   // Fake a delete action
        }
        if (screen != null)
        {
            this.initAndDisplayScreen(screen, null);
            return true;    // Handled.
        }
        return super.doAction(strAction, iOptions);
    }
    /**
     * Process this action.
     */
    public void initAndDisplayScreen(JBaseScreen screen, FieldList record)
    {
        BaseApplet applet = this.getBaseApplet();
        if ((record != null) || (screen != null))
        {
            Application application = applet.getApplication();
            BaseApplet job = new BaseApplet();
            Map<String,Object> properties = null;
            ((Task)job).initTask(application, properties);
            if (screen == null)
                screen = new JScreen();
            screen.init(job, record);
            job.changeSubScreen(null, screen, null);
            job.run();
        }
    }
    /**
     * These items have changed, update them on the screen.
     */
    public void selectionChanged(MyListSelectionEvent event)
    {
        int iRowStart = event.getRow();
        int iType = event.getType();
        if (iType == MyListSelectionEvent.CONTENT_CLICK)    // Select + click
        {
            CalendarModel model = this.getCalendarModel(null);
            String strID = null;
            String strMasterID = null;
            synchronized (this.getBaseApplet().getRemoteTask())   // Make sure the other tasks don't mess with this record
            {
                AppointmentCalendarItem item = (AppointmentCalendarItem)model.getItem(iRowStart);
                strID = item.getField(Anniversary.ID).toString();
                if (item.getField(Anniversary.ANNIV_MASTER_ID) != null)
                    strMasterID = item.getField(Anniversary.ANNIV_MASTER_ID).toString();//+++ Need a way to get the type??? Appointment or Anniversary
            }
            JScreen screen = null;
            if ((strMasterID != null) && (strMasterID.length() > 0))
            {
                strID = strMasterID;
                screen = new AnniversaryScreen();
            }
            else
            {
                screen = new AppointmentScreen(); // Note: I did not initialize the class by passing the params
            }
            this.initAndDisplayScreen(screen, null);
            FieldList record = screen.getFieldList();
            FieldInfo field = record.getField(Anniversary.ID);
            field.setString(strID);
            if ((strID != null) && (strID.length() > 0))
                screen.readKeyed(field);
        }
    }
}
