/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.screentest;

/**
 * OrderEntry.java:   Applet
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.JScreen;


/**
 * Main Class for applet OrderEntry
 */
public class ThinTestApplet extends BaseApplet
{
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public ThinTestApplet()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public ThinTestApplet(String args[])
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
    public boolean addSubPanels(Container parent, int options)
    {
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
        FieldList record = null;
        JScreen thinscreen = new TestThinScreen(this, record);
        record = thinscreen.getFieldList();
        this.linkNewRemoteTable(record);
        
//      MemoryRemoteTable table = new MemoryRemoteTable(record);
//      ThinTable testTable = new ThinTable(record, table, null);

//      record = new org.jbundle.thin.main.Menus(this);    // If overriding class didn't set this
//      this.linkNewRemoteTable(null, record);

//      JScreen thinscreen = new org.jbundle.thin.base.screen.menu.JRemoteMenuScreen(this, record);    // test
 
        thinscreen.setMinimumSize(new Dimension(100, 100));
        thinscreen.setAlignmentX(LEFT_ALIGNMENT);
        thinscreen.setAlignmentY(TOP_ALIGNMENT);
        parent.add(thinscreen);
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
        ThinTestApplet applet = (ThinTestApplet)ThinTestApplet.getSharedInstance();
        if (applet == null)
            applet = new ThinTestApplet(args);
        new JBaseFrame("Test", applet);
    }
    /**
     * Update the order object with the current screen information.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        boolean bHandled = super.doAction(strAction, iOptions);
        if (strAction == Constants.SUBMIT)
            return true;
        return bHandled;
    }
}
