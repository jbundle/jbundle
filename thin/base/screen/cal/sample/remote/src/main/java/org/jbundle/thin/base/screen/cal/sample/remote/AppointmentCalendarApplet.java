/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.sample.remote;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.cal.grid.CalendarThinTableModel;
import org.jbundle.thin.base.screen.cal.opt.TaskCalendarStatusHandler;
import org.jbundle.util.calendarpanel.CalendarPanel;
import org.jbundle.util.calendarpanel.model.CalendarModel;
import org.jbundle.thin.main.calendar.db.Appointment;


public class AppointmentCalendarApplet extends BaseApplet
{
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public AppointmentCalendarApplet()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public AppointmentCalendarApplet(String[] args)
    {
        this();
        this.init(args);
    }
    /**
     * Initializes the applet.  You never need to call this directly; it is
     * called automatically by the system once the applet is created.
     */
    public void init()
    {
        super.init();
    }
    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public void start()
    {
        super.start();
    }
    /**
     * Add any applet sub-panel(s) now.
     */
    public boolean addSubPanels(Container parent)
    {
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
        JScrollPane scroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        scroller.setPreferredSize(new Dimension(800, 400));
        scroller.setAlignmentX(LEFT_ALIGNMENT);
        scroller.setAlignmentY(TOP_ALIGNMENT);

        CalendarModel model = this.setupTestModel();

        ImageIcon backgroundImage = this.getBackgroundImage();	// Calendar panel is transparent, but this helps with rendering see-thru components 
        CalendarPanel panel = new CalendarPanel(model, true, backgroundImage);
        panel.setStatusListener(new TaskCalendarStatusHandler(this));
        
        scroller.setViewportView(panel);
        parent.add(scroller);
        return true;
    }
    /**
     * Called to stop the applet.  This is called when the applet's document is
     * no longer on the screen.  It is guaranteed to be called before destroy()
     * is called.  You never need to call this method directly
     */
    public void stopTask()
    {
        super.stopTask();
    }
    /**
     * Cleans up whatever resources are being held.  If the applet is active
     * it is stopped.
     */
    public void destroy()
    {
        super.destroy();
    }
    /**
     * For Stand-alone.
     */
    public static void main(String[] args)
    {
        BaseApplet.main(args);
        BaseApplet applet = AppointmentCalendarApplet.getSharedInstance();
        if (applet == null)
            applet = new AppointmentCalendarApplet(args);
        new JBaseFrame("Calendar", applet);
    }

    public CalendarModel setupTestModel()
    {
        Appointment record = new Appointment(this);
//+     CalendarEntry record = new CalendarEntry(null);
        this.linkNewRemoteTable(record, true);
        return new CalendarThinTableModel(record.getTable(), "StartDateTime", "EndDateTime", "Description", null);
    }
}
