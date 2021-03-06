/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.sharedtest;

/**
 * OrderEntry.java:   Applet
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Container;

import javax.swing.BoxLayout;

import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.mem.GridMemoryFieldTable;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.memory.MDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.JBaseScreen;


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
    public boolean addSubPanels(Container parent, int options)
    {
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
        FieldList record = null;
        record = new TestTable(null);
        PhysicalDatabaseParent dbOwner = new PhysicalDatabaseParent(null);
        PDatabase db = dbOwner.getPDatabase(record.getDatabaseName(), ThinPhysicalDatabase.MEMORY_TYPE, false);
        if (db == null)
            db = new MDatabase(dbOwner, record.getDatabaseName());
        PTable table = db.getPTable(record, true, true);
        JBaseScreen screen = new JTestGridScreen(this, record);
        parent.add(screen);
        return true;
/*      JScreen screen = new TestThinScreen(null);
        FieldList record = new TestTable(this);
//      FieldList record = screen.getFieldList();
        this.linkNewRemoteTable(null, record, true);

        model = new TestGridModel(record.getFieldTable());
        thinscreen = new JTable(model);
        model.setGridScreen(thinscreen, false);
            TableColumn newColumn = thinscreen.getColumnModel().getColumn(0);
            ImageIcon icon = (ImageIcon)model.getValueAt(0, 0);
            newColumn.setPreferredWidth(20);    // Yeah I know, but I know the width and I don't want to have to wait to load the icon.
            JCellButton button = new JCellButton(icon);
            newColumn.setCellEditor(button);
            button.addActionListener(this);
            button = new JCellButton(icon);
            newColumn.setCellRenderer(button);
        JScrollPane scrollpane = new JScrollPane(thinscreen);
        parent.add(scrollpane);
*/  }
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
