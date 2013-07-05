/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.blink;

/**
 * OrderEntry.java:   Applet
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.client.RemoteFieldTable;
import org.jbundle.thin.base.db.client.memory.MemoryRemoteTable;
import org.jbundle.thin.base.screen.AbstractThinTableModel;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.util.JBlinkLabel;


/**
 * Main Class for applet OrderEntry
 */
public class TestThinGridScreen extends BaseApplet
{
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinGridScreen()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinGridScreen(String args[])
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
//      JScreen screen = new TestThinScreen(null);
        FieldList record = new TestTable(this);
//      FieldList record = screen.getFieldList();
//x     this.linkNewRemoteTable(null, record);
        MemoryRemoteTable table = new MemoryRemoteTable(record);
        RemoteFieldTable testTable = new RemoteFieldTable(record, table, null);

        DBBaseTest test = new DBBaseTest(null);
        test.initTable(testTable);

        AbstractThinTableModel model = new TestGridModel(record.getTable());
        JTable thinscreen = new JTable(model);
TableColumnModel columnModel = thinscreen.getColumnModel();
TableColumn tableColumn = columnModel.getColumn(1);
JBlinkLabel label = new JBlinkLabel(null);
label.addIcon(this.loadImageIcon("tour/buttons/Item", null), 0);
label.addIcon(this.loadImageIcon("tour/buttons/Hotel", null), 1);
label.addIcon(this.loadImageIcon("tour/buttons/Land", null), 2);
label.addIcon(this.loadImageIcon("tour/buttons/Tour", null), 3);
label.addIcon(this.loadImageIcon("tour/buttons/Air", null), 4);
label.addIcon(this.loadImageIcon("tour/buttons/Transportation", null), 5);
label.addIcon(this.loadImageIcon("tour/buttons/Cruise", null), 6);
tableColumn.setCellRenderer(label);
tableColumn.setMaxWidth(20);
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
        TestThinGridScreen applet = (TestThinGridScreen)TestThinGridScreen.getSharedInstance();
        if (applet == null)
            applet = new TestThinGridScreen(args);
        new JBaseFrame("Test", applet);
    }
}
