/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.objecttest;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jbundle.thin.app.test.vet.db.Animal;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;


public class ObjectTest extends BaseApplet
{
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public ObjectTest()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public ObjectTest(String[] args)
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

//x     CalendarEntry record = new CalendarEntry(this);
        FieldList record = new Animal(this);
        this.linkNewRemoteTable(record);

//      CalendarModel model = new CalendarThinTableModel(record.getFieldTable());

//      CalendarPanel panel = new CalendarPanel(model, true);
        
//      scroller.setViewportView(panel);
//      parent.add(scroller);
        
        try   {
            FieldTable table = record.getTable();
            for (int i = 0; i < 100; i++)
            {
                record = (FieldList)table.get(i);
                if (record == null)
                    break;
                System.out.println(record);
                if (i == 3)
                {
//                  table.seek("=");
//                  table.edit();
//                  record.getFieldInfo("Name").setString("newname");
//                  table.set(record);
                }
            }
            
            record = (FieldList)table.get(2);
                    table.edit();
            record.getField("Name").setString("name 2");
            table.set(record);
            table.get(2);
            record = (FieldList)table.get(3);
                    table.edit();
            record.getField("Name").setString("name 3");
            table.set(record);
            table.get(3);
            record = (FieldList)table.get(2);
                    table.edit();
            record.getField("Name").setString("name 2 again");
            table.set(record);
            table.get(2);
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        System.exit(0);
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
        BaseApplet applet = ObjectTest.getSharedInstance();
        if (applet == null)
            applet = new ObjectTest(args);
        new JBaseFrame("Calendar", applet);
    }
}
