/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.sample.remote;

/**
 * OrderEntry.java:   Applet
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jbundle.thin.base.screen.AbstractThinTableModel;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.cal.grid.CalendarThinTableModel;
import org.jbundle.thin.main.calendar.db.CalendarEntry;

/**
 * Main Class for applet OrderEntry
 */
public class AppointmentApplet extends BaseApplet
{
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public AppointmentApplet()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public AppointmentApplet(String[] args)
    {
        this();
        this.init(args);
    }
    /**
     * The getAppletInfo() method returns a string describing the applet's
     * author, copyright date, or miscellaneous information.
     */
    public String getAppletInfo()
    {
        return "Name: Thin Test\r\n" +
               "Author: Don Corley\r\n" +
               "E-Mail: don@tourgeek.com";
    }
    /**
     * Add any applet sub-panel(s) now.
     */
    public boolean addSubPanels(Container parent)
    {
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));

//      FieldList record = new AppointmentCalendarItem(this);
//      this.linkNewRemoteTable(null, record);
//      ThinTableModel model = new CalendarThinTableModel(record.getFieldTable());

        CalendarEntry record = new CalendarEntry(this);
        this.linkNewRemoteTable(record);
        AbstractThinTableModel model = new CalendarThinTableModel(record.getTable(), "StartDateTime", "EndDateTime", "Description", null);

        
        JTable thinscreen = new JTable(model);
        model.setGridScreen(thinscreen, false);
        JScrollPane scrollpane = new JScrollPane(thinscreen);
        parent.add(scrollpane);
        return true;
    }
    /**
     *  The main() method acts as the applet's entry point when it is run
     *  as a standalone application. It is ignored if the applet is run from
     *  within an HTML page.
     */
    public static void main(String args[])
    {
        BaseApplet.main(args);
        AppointmentApplet applet = (AppointmentApplet)AppointmentApplet.getSharedInstance();
        if (applet == null)
            applet = new AppointmentApplet(args);
        new JBaseFrame("Test", applet);
    }
}
