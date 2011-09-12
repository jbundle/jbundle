/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.screentest;

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

import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.grid.ThinTableModel;


/**
 * Main Class for applet OrderEntry
 */
public class TestThinGridApplet extends BaseApplet
{
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinGridApplet()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinGridApplet(String[] args)
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
//      JScreen screen = new TestThinScreen(null);
        FieldList record = new TestTable(this);
//      FieldList record = screen.getFieldList();
        this.linkNewRemoteTable(record, true);

        ThinTableModel model = new ThinTableModel(record.getTable());
//      ThinTableModel model = new CalendarThinTableModel(record.getFieldTable());
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
        TestThinGridApplet applet = (TestThinGridApplet)TestThinGridApplet.getSharedInstance();
        if (applet == null)
            applet = new TestThinGridApplet(args);
        new JBaseFrame("Test", applet);
    }
}
