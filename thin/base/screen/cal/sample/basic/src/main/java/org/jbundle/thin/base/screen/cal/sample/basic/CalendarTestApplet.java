/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.sample.basic;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.cal.opt.TaskCalendarStatusHandler;
import org.jbundle.thin.base.screen.cal.popup.JPopupPanel;
import org.jbundle.util.calendarpanel.CalendarPanel;
import org.jbundle.util.calendarpanel.model.CalendarModel;


public class CalendarTestApplet extends BaseApplet
{
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public CalendarTestApplet()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public CalendarTestApplet(String args[])
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
    public boolean addSubPanels(Container parent, int options)
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
        
        ActionListener listener = new MyAction();
        panel.setPopupComponent(new JPopupPanel(this, listener));
        return true;
    }
    class MyAction extends AbstractAction
    {

        @Override
        public void actionPerformed(ActionEvent e) {
        //+    System.out.println("Action: " + e);
            
        }
        
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
        BaseApplet applet = CalendarTestApplet.getSharedInstance();
        if (applet == null)
            applet = new CalendarTestApplet(args);
        new JBaseFrame("Calendar", applet);
    }

    public CalendarModel setupTestModel()
    {
        CalendarVector model = new CalendarVector(null);
        BaseApplet applet = this;

        Calendar m_calendar = Calendar.getInstance();
        Date lStartTime;
        Date lEndTime;
        m_calendar.set(1998, Calendar.JUNE, 11, 2, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 13, 0, 0, 0);
        lEndTime = m_calendar.getTime();

        int colorHotel = 0x00c0ffff;    // HACK Light blue
        int colorSelectHotel = 0x00e0ffff;
        int colorLand = 0x00c0c0ff;   // HACK Light blue
        int colorSelectLand = 0x00e0e0ff;
        int colorAir = 0x00ffc0c0;  // HACK Light blue
        int colorSelectAir = 0x00ffe0e0;
        
        model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Mandarin Hotel - 3 Nights $200.00", applet.loadImageIcon("tour/buttons/Hotel.gif", null), new ImageIcon("images/tour/buttons/Hotel.gif"), "M", colorHotel, colorSelectHotel, 1));
        
        m_calendar.set(1998, Calendar.JUNE, 9, 12, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 9, 15, 0, 0);
        lEndTime = m_calendar.getTime();
        model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Airport/Hotel Transfer - SIC", applet.loadImageIcon("tour/buttons/Land.gif", null), null, "T", colorLand, colorSelectLand, 1));

        m_calendar.set(1998, Calendar.JUNE, 10, 20, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 12, 20, 0, 0);
        lEndTime = m_calendar.getTime();
        model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Flight", applet.loadImageIcon("tour/buttons/Air.gif", null), null, "F", colorAir, colorSelectAir, 1));
        
        m_calendar.set(1998, Calendar.JUNE, 10, 3, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 15, 12, 0, 0);
        lEndTime = m_calendar.getTime();
        model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Dusit Thani Hotel", applet.loadImageIcon("images/tour/buttons/Hotel.gif", null), new ImageIcon("images/tour/buttons/Hotel.gif"), "D", colorHotel, colorSelectHotel, 1));

        m_calendar.set(1998, Calendar.JUNE, 12, 12, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 15, 12, 0, 0);
        lEndTime = m_calendar.getTime();
        model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Orchid Hotel", applet.loadImageIcon("tour/buttons/Hotel.gif"), applet.loadImageIcon("tour/buttons/Hotel.gif"), "D", colorHotel, colorSelectHotel, 1));

        return model;
    }
}
