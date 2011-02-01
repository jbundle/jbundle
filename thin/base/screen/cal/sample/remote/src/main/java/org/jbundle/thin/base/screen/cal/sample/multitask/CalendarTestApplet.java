package org.jbundle.thin.base.screen.cal.sample.multitask;

import java.awt.Container;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.cal.opt.TaskCalendarStatusHandler;
import org.jbundle.thin.base.screen.cal.popup.ProductConstants;
import org.jbundle.util.calendarpanel.CalendarPanel;
import org.jbundle.util.calendarpanel.model.CachedCalendarModel;
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
    public boolean addSubPanels(Container parent)
    {
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
        JScrollPane scroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        scroller.setPreferredSize(new Dimension(800, 400));
        scroller.setAlignmentX(LEFT_ALIGNMENT);
        scroller.setAlignmentY(TOP_ALIGNMENT);

        CalendarModel model = CalendarTestApplet.setupTestModel();

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
        BaseApplet applet = CalendarTestApplet.getSharedInstance();
        if (applet == null)
            applet = new CalendarTestApplet(args);
        new JBaseFrame("Calendar", applet);
    }

    public static CalendarModel setupTestModel()
    {
    	ImageIcon headerIcon = org.jbundle.thin.base.screen.cal.popup.ProductTypeInfo.getProductType(org.jbundle.thin.base.screen.cal.popup.ProductConstants.MEAL).getStartIcon();
        CachedCalendarModel model = new CachedCalendarModel(headerIcon);

        Calendar m_calendar = Calendar.getInstance();
        Date lStartTime;
        Date lEndTime;
        m_calendar.set(1998, Calendar.JUNE, 11, 2, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 13, 0, 0, 0);
        lEndTime = m_calendar.getTime();

//?        Color colorHotel = new Color(192, 255, 255);    // HACK Light blue
//?        Color colorSelectHotel = colorHotel.darker();
//?        Color colorLand = new Color(192, 192, 255);   // HACK Light blue
//?        Color colorSelectLand = colorLand.darker();
//?        Color colorAir = new Color(255, 192, 192);  // HACK Light blue
//?        Color colorSelectAir = colorAir.darker();
        
        model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Mandarin Hotel - 3 Nights $200.00", ProductConstants.HOTEL, "H", 1));
        
        m_calendar.set(1998, Calendar.JUNE, 9, 12, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 9, 15, 0, 0);
        lEndTime = m_calendar.getTime();
        model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Airport/Hotel Transfer - SIC", ProductConstants.LAND, "L", 1));

        m_calendar.set(1998, Calendar.JUNE, 10, 20, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 12, 20, 0, 0);
        lEndTime = m_calendar.getTime();
        model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Flight", ProductConstants.AIR, "A", 1));
        
        m_calendar.set(1998, Calendar.JUNE, 10, 3, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 15, 12, 0, 0);
        lEndTime = m_calendar.getTime();
        model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Dusit Thani Hotel", ProductConstants.HOTEL, "H2", 1));

        m_calendar.set(1998, Calendar.JUNE, 12, 12, 0, 0);
        lStartTime = m_calendar.getTime();
        m_calendar.set(1998, Calendar.JUNE, 15, 12, 0, 0);
        lEndTime = m_calendar.getTime();
//      model.addElement(new CalendarProduct(model, lStartTime, lEndTime, "Orchid Hotel", new ImageIcon("images/tour/buttons/Hotel.gif"), new ImageIcon("images/tour/buttons/Hotel.gif"), "D", colorHotel, colorSelectHotel));

        return model;
    }
}
